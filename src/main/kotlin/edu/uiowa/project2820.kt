package edu.uiowa
import java.util.*

import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Alert
import sun.security.krb5.KrbException.errorMessage
import java.awt.Checkbox
import java.awt.CheckboxGroup
import java.awt.SystemColor.text

var mainScene: Scene? = null
var currentScene: Scene? = null
var currentStage: Stage? = null
var menuScene: Scene? = null

val groupset1: Array<ToggleButton?> = arrayOfNulls(4) // for toggle buttons
val tgroup1 = ToggleGroup()

var mainBoard = Board(3,false)
var currentBoard = Board(3,false) //mini game when playing ultimate

var players = 1
var isUlt = false

interface TheBoard{
    fun showBoard()
    fun createBoard()
    fun createGrid()
}

class Board(val size: Int, var main:Boolean): TheBoard{

    var grid = GridPane()
    var board = arrayOf<Array<Int>>()       //background for tracking winner
    var buttons = arrayOf<Array<Button>>() //visual representation of buttons
    var winner = 0
    var currentTurn = true
    var tempButton: Button? = null
    var tempi = 0
    var tempj = 0
    val ai = AI()

    //2D array, Array of rows each containing an array of columns
    override fun createBoard(){
        if(players == 1){
            ai.createMap()
        }

        for(i in 0..size-1){
            var cols = arrayOf<Int>()
            for(j in 0..size-1){
                cols += 0
            }
            board += cols
        }
    }

    //creates the representation of the board
    override fun createGrid(){
        for(i in 0..size-1){
            var bcols = arrayOf<Button>()
            for(j in 0..size-1){
                val b = Button("")
                b.style = "-fx-font-size: 48px; -fx-text-fill: #b22222"
                b.setPrefSize((size * 99).toDouble(), (size * 99).toDouble())
                grid.add(b, j, i)

                if(isUlt && main){
                    b.setOnAction { e -> tempButton = b; tempi = i; tempj = j; gameController(false)}
                }else if(isUlt && !main){
                    b.setOnAction { e ->  tempButton = b; tempi = i; tempj = j; takeTurn(currentBoard)}
                }else if(!isUlt && main){
                    b.setOnAction { e -> tempButton = b; tempi = i; tempj = j; takeTurn(mainBoard)}
                }

                bcols += b
            }
            buttons += bcols
        }

    }

    override fun showBoard() {
        for(i in board){
            for(j in i){
                print(j)
            }
            println()
        }
    }

    inner class AI{

        var open_squares: MutableList<Pair<Int, Int>> = mutableListOf()
        var open_squaresList: MutableList<Int> = mutableListOf()

        fun createMap(){
            open_squares.clear()
            for(i in 0..size-1){
                for(j in 0..size-1){
                    open_squares.add(Pair(i, j))
                }
            }
        }

        fun currentMap(){
            open_squaresList.clear()
            var count = 0
            for(i in board){
                for(j in i){
                    if(j == 0){
                        open_squaresList.add(count)
                    }
                    count++
                }
            }
        }

        fun chooseSquare(){
            val tempRandom = open_squaresList.random()
            val tempPair = open_squares.get(tempRandom)
            val tempNode = grid.children.get(tempRandom) as Button

            tempNode.text = "O"
            board[tempPair.first][tempPair.second] = -1

        }
    }
}

//this is activated on each button click
fun takeTurn(boardClass: Board) {
    if(boardClass.board[boardClass.tempi][boardClass.tempj] != 0){
        message("Square already taken, Choose again")
        boardClass.currentTurn = !boardClass.currentTurn
    }else{
        if(boardClass.currentTurn){
            boardClass.board[boardClass.tempi][boardClass.tempj] = 1
            boardClass.tempButton?.text = "X"
        }else{
            boardClass.board[boardClass.tempi][boardClass.tempj] = -1
            boardClass.tempButton?.text = "O"
        }
    }

    if(isUlt){
        checkBoard(currentBoard)
        checkminiWinner()
        checkBoard(mainBoard)
        checkmainWinner()
    }else{
        checkBoard(boardClass)
        checkmainWinner()
    }

    boardClass.currentTurn = !boardClass.currentTurn

    if(players == 1 && boardClass.currentTurn == false && boardClass.winner == 0){
        boardClass.ai.currentMap()
        if(!boardClass.ai.open_squaresList.isEmpty()){
            boardClass.ai.chooseSquare()

            if(isUlt){
                checkBoard(currentBoard)
                checkminiWinner()
                checkBoard(mainBoard)
                checkmainWinner()
            }else{
                checkBoard(boardClass)
                checkmainWinner()
            }

            boardClass.currentTurn = !boardClass.currentTurn
        }
    }

}

//returns 1 if X wins, and -1 if O wins
fun checkBoard(boardClass: Board) {
    var count = 0

    //checks each row
    for(i in 0..boardClass.size-1){
        count = 0
        for(j in 0..boardClass.size-2){
            if(boardClass.board[i][j] == boardClass.board[i][j+1] && boardClass.board[i][j] != 0){
                count++
            }
        }
        if(count == boardClass.size - 1){
            boardClass.winner = if(boardClass.currentTurn) 1 else -1
        }
    }

    //checks each column
    for(i in 0..boardClass.size-1){
        count = 0
        for(j in 0..boardClass.size-2){
            if(boardClass.board[j][i] == boardClass.board[j+1][i] && boardClass.board[j][i] != 0){
                count++
            }
        }
        if(count == boardClass.size - 1){
            boardClass.winner = if(boardClass.currentTurn) 1 else -1
        }
    }

    //checks diagonally from top left to bottom right
    count = 0
    for(i in 0..boardClass.size-2){
        if(boardClass.board[i][i] == boardClass.board[i+1][i+1] && boardClass.board[i][i] != 0){
            count++
        }
        if(count == boardClass.size - 1){
            boardClass.winner = if(boardClass.currentTurn) 1 else -1
        }
    }

    //checks diagonal from top right to bottom left
    count = 0
    for(i in 0..boardClass.size-2){
        if(boardClass.board[i][boardClass.size-1-i] == boardClass.board[i+1][boardClass.size-2-i] && boardClass.board[i][boardClass.size-1-i] != 0){
            count++
        }
        if(count == boardClass.size - 1){
            boardClass.winner = if(boardClass.currentTurn) 1 else -1
        }
    }


    //checks if board is full with no winner
    count = 0
    for(i in 0..boardClass.size - 1){
        for(j in 0..boardClass.size - 1){
            if(boardClass.board[i][j] != 0 && boardClass.winner == 0){
                count++
            }
        }
    }

    if(count == boardClass.size * boardClass.size){
        if(boardClass.main){
            message("No Winner")
            currentStage?.scene = menuScene
        }else{
            message("No Winner")
            boardClass.winner = 0
            currentStage?.scene = mainScene
        }
    }
}

fun checkminiWinner(){
    if(currentBoard.winner == 1){
        mainBoard.tempButton?.text = "X"
        mainBoard.board[mainBoard.tempi][mainBoard.tempj] = 1
        currentStage?.scene = mainScene
    }else if(currentBoard.winner == -1){
        mainBoard.tempButton?.text = "O"
        mainBoard.board[mainBoard.tempi][mainBoard.tempj] = -1
        currentStage?.scene = mainScene
    }
}
fun checkmainWinner(){
    if(mainBoard.winner == 1){
        message("Player 1 Wins!")
        currentStage?.scene = menuScene
    }else if(mainBoard.winner == -1){
        if(players == 1){
            message("The Computer Wins!")
        }else{
            message("Player 2 Wins!")
        }
        currentStage?.scene = menuScene
    }
}

//this creates a pop up message when called
fun message(s: String){
    val alert = Alert(AlertType.INFORMATION)
    alert.title = "Message"
    alert.headerText = null
    alert.contentText = s
    alert.showAndWait()
}

fun gameController(isMain:Boolean){
    var boardSize = 3
    when(tgroup1.selectedToggle){
        groupset1[0] -> boardSize = 3
        groupset1[1] -> boardSize = 4
        groupset1[2] -> boardSize = 5
        groupset1[3] -> boardSize = 6
    }

    if(isMain){
        mainBoard = Board(boardSize, isMain)
        mainBoard.createBoard()
        mainBoard.createGrid()

        mainScene = Scene(mainBoard.grid, (boardSize * 210).toDouble(),(boardSize * 210)
                .toDouble())
        currentStage?.scene = mainScene
    }else{
        currentBoard = Board(3, isMain)
        currentBoard.createBoard()
        currentBoard.createGrid()

        currentScene = Scene(currentBoard.grid, 310.toDouble(), 310.toDouble())
        currentStage?.scene = currentScene
    }

}

class MyForm: Application() {

    override fun start(primaryStage: Stage) {

        val vbox = VBox()
        vbox.setPrefSize(500.0,250.0)

        val vbox1 = VBox()
        vbox1.setPrefSize(300.0,175.0)

        menuScene = Scene(vbox, 300.0, 250.0)
        var chooseScene = Scene(vbox1, 300.0, 175.0)

        val hbox1 = HBox()
        hbox1.setPadding(Insets(20.0, 20.0, 15.0, 65.0));
        hbox1.setSpacing(15.0);
        vbox.children.add(hbox1)

        val header = Label("Tic Tac Toe")
        header.style = "-fx-font-size: 30px"
        hbox1.children.add(header)

        val hbox2 = HBox()
        hbox2.setPadding(Insets(15.0, 12.0, 15.0, 58.0));
        hbox2.setSpacing(15.0);
        vbox.children.add(hbox2)

        val cb = CheckBox()
        val startb = Button("Start Game")
        startb.setOnAction { e -> primaryStage.scene = chooseScene; isUlt = cb.isSelected}
        hbox2.children.add(startb)

        val exitb = Button("Exit Game")
        exitb.setOnAction { e -> primaryStage.close()}
        hbox2.children.add(exitb)

        val hbox3 = HBox()
        hbox3.setPadding(Insets(5.0, 12.0, 5.0, 30.0));
        hbox3.setSpacing(15.0);
        vbox.children.add(hbox3)

        val labelSize = Label("Choose board size between 3 and 6: ")
        hbox3.children.add(labelSize)

        val hbox3b = HBox()
        hbox3b.setPadding(Insets(5.0, 20.0, 15.0, 70.0));
        hbox3b.setSpacing(15.0);
        vbox.children.add(hbox3b)

        val r1 = ToggleButton("3")
        r1.toggleGroup = tgroup1
        hbox3b.children.add(r1)
        groupset1[0] = r1
        val r2 = ToggleButton("4")
        r2.toggleGroup = tgroup1
        hbox3b.children.add(r2)
        groupset1[1] = r2
        val r3 = ToggleButton("5")
        r3.toggleGroup = tgroup1
        hbox3b.children.add(r3)
        groupset1[2] = r3
        val r4 = ToggleButton("6")
        r4.toggleGroup = tgroup1
        hbox3b.children.add(r4)
        groupset1[3] = r4

        val hbox3c = HBox()
        hbox3c.setPadding(Insets(5.0, 15.0, 15.0, 110.0));
        hbox3c.setSpacing(15.0);
        vbox.children.add(hbox3c)

        cb.text = "Ultimate?"
        hbox3c.children.add(cb)

        val hbox4 = HBox()
        hbox4.setPadding(Insets(15.0, 20.0, 15.0, 40.0));
        hbox4.setSpacing(15.0);
        vbox1.children.add(hbox4)

        val labelPlayers = Label("How many players?")
        labelPlayers.style = "-fx-font-size: 24px"
        hbox4.children.add(labelPlayers)

        val hbox5 = HBox()
        hbox5.setPadding(Insets(20.0, 30.0, 15.0, 70.0));
        hbox5.setSpacing(15.0);
        vbox1.children.add(hbox5)

        val startb2 = Button("1 Player")
        startb2.setOnAction { e -> gameController(true); players = 1}
        hbox5.children.add(startb2)
        val startb3 = Button("2 Players")
        startb3.setOnAction { e -> gameController(true); players = 2}
        hbox5.children.add(startb3)

        val hbox6 = HBox()
        hbox6.setPadding(Insets(10.0, 30.0, 10.0, 65.0));
        hbox6.setSpacing(15.0);
        vbox1.children.add(hbox6)

        val back = Button("Back")
        back.setOnAction { e -> primaryStage.scene = menuScene}
        hbox6.children.add(back)

        currentStage = primaryStage
        primaryStage.scene = menuScene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    launch(MyForm::class.java)
}