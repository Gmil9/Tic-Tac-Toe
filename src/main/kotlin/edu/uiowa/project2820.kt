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


var currentstage: Stage? = null
var menuScene: Scene? = null
var grid = GridPane()
var boardSize: Int = 3
var currentTurn = true
var players = 1
var winner = 0
var b = Board(boardSize)
val ai = AI()
var board = arrayOf<Array<Int>>()       //background for tracking winner
var buttons = arrayOf<Array<Button>>() //visual representation of buttons
val groupset1: Array<ToggleButton?> = arrayOfNulls(4) // for toggle buttons
val tgroup1 = ToggleGroup()

interface TheBoard{
    fun createBoard(): Array<Array<Int>>
    fun createGrid(): Array<Array<Button>>
    fun checkBoard(board: Array<Array<Int>>)
    fun takeTurn(j: Int, i: Int, bs :Button)
}

class Board(val size: Int): TheBoard{

    //2D array, Array of rows each containing an array of columns
    override fun createBoard(): Array<Array<Int>>{
        var rows = arrayOf<Array<Int>>()
        var count = 0
        for(i in 1..size){
            var cols = arrayOf<Int>()
            for(j in 1..size){
                cols += 0
                count++
            }
            rows += cols
        }
        return rows
    }

    //creates the representation of the board
    override fun createGrid(): Array<Array<Button>>{
        var count = 0
        for(i in 0..size-1){
            var bcols = arrayOf<Button>()
            for(j in 0..size-1){
                val b = Button("")
                b.style = "-fx-font-size: 48px; -fx-text-fill: #b22222"
                b.setPrefSize((size * 99).toDouble(), (size * 99).toDouble())
                grid.add(b, j, i)
                b.setOnAction { e -> takeTurn(i, j, b) } //takeTurn is called whenever a square is clicked
                bcols += b
                count++
            }
            buttons += bcols
        }
        return buttons
    }

    //this is activated on each button click
    override fun takeTurn(j: Int, i: Int, bs: Button) {
        if(board[j][i] != 0){
            message("Square already taken, Choose again")
            currentTurn = !currentTurn
        }else{
            if(currentTurn){
                board[j][i] = 1
                bs.text = "X"
            }else{
                board[j][i] = -1
                bs.text = "O"
            }
        }

        b.checkBoard(board)
        checkWinner()

        if(players == 1 && currentTurn == false && winner == 0){
            ai.chooseSquare()
        }
    }

    //returns 1 if X wins, and -1 if O wins
    override fun checkBoard(board: Array<Array<Int>>) {
        var count = 0

        //checks each row
        for(i in 0..size-1){
            count = 0
            for(j in 0..size-2){
                if(board[i][j] == board[i][j+1] && board[i][j] != 0){
                    count++
                }
            }
            if(count == size - 1){
                winner = if(currentTurn) 1 else -1
            }
        }

        //checks each column
        for(i in 0..size-1){
            count = 0
            for(j in 0..size-2){
                if(board[j][i] == board[j+1][i] && board[j][i] != 0){
                    count++
                }
            }
            if(count == size - 1){
                winner = if(currentTurn) 1 else -1
            }
        }

        //checks diagonally from top left to bottom right
        count = 0
        for(i in 0..size-2){
            if(board[i][i] == board[i+1][i+1] && board[i][i] != 0){
                count++
            }
            if(count == size - 1){
                winner = if(currentTurn) 1 else -1
            }
        }

        //checks diagonal from top right to bottom left
        count = 0
        for(i in 0..size-2){
            if(board[i][size-1-i] == board[i+1][size-2-i] && board[i][size-1-i] != 0){
                count++
            }
            if(count == size - 1){
                winner = if(currentTurn) 1 else -1
            }
        }

        //checks if board is full with no winner
        count = 0
        for(i in 0..size - 1){
            for(j in 0..size - 1){
                if(board[i][j] != 0 && winner == 0){
                    count++
                }
            }
        }

        if(count == size * size){
            message("No Winner")
            currentstage?.scene = menuScene
        }
    }
}

class AI{

    var open_squares: MutableList<Pair<Int, Int>> = mutableListOf()
    var open_squaresList: MutableList<Int> = mutableListOf()

    fun createMap(){
        open_squares.clear()
        for(i in 0..boardSize-1){
            for(j in 0..boardSize-1){
                open_squares.add(Pair(i, j))
            }
        }
    }

    fun currentMap(){
        open_squaresList.clear()
        var count = 0
        for(i in 0..boardSize-1){
            for(j in 0..boardSize-1){
                if(board[i][j] == 0){
                    open_squaresList.add(count)
                }
                count++
            }
        }
    }

    fun chooseSquare(){
        ai.currentMap()
        val tempRandom = open_squaresList.random()
        val tempPair = open_squares.get(tempRandom)
        val tempNode = grid.children.get(tempRandom) as Button

        tempNode.text = "O"
        board[tempPair.first][tempPair.second] = -1

        b.checkBoard(board)
        checkWinner()
    }
}

fun checkWinner(){
    if(winner == 1){
        //Player 1 wins "X"
        message("Player 1 Wins!")
        currentstage?.scene = menuScene
    }else if(winner == -1){
        //Player 2/Computer wins "O"
        if(players == 1){
            message("The Computer Wins!")
        }else{
            message("Player 2 Wins!")
        }
        currentstage?.scene = menuScene
    }
    currentTurn = !currentTurn
}

//this creates a pop up message when called
fun message(s: String){
    val alert = Alert(AlertType.INFORMATION)
    alert.title = "Message"
    alert.headerText = null
    alert.contentText = s
    alert.showAndWait()
}

//resets the board for a new game
fun newGame(){
    winner = 0
    if(grid.children.size != 0) grid.children.remove(0, boardSize * boardSize)
    if(grid.children.size > 0){
        grid.children.remove(0,1)
    }
    currentTurn = true
    when(tgroup1.selectedToggle){
        groupset1[0] -> boardSize = 3
        groupset1[1] -> boardSize = 4
        groupset1[2] -> boardSize = 5
        groupset1[3] -> boardSize = 6
    }
    b = Board(boardSize)
    board = b.createBoard()
    buttons = b.createGrid()
    grid.gridLinesVisibleProperty().set(true)
}

class MyForm: Application() {

    override fun start(primaryStage: Stage) {

        var gameScene = Scene(grid, (boardSize * 210).toDouble(),(boardSize * 210)
                .toDouble())

        val vbox = VBox()
        vbox.setPrefSize(500.0,200.0)

        val vbox1 = VBox()
        vbox1.setPrefSize(300.0,150.0)

        menuScene = Scene(vbox, 300.0, 200.0)
        var chooseScene = Scene(vbox1, 300.0, 150.0)

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

        val startb = Button("Start Game")
        startb.setOnAction { e -> primaryStage.scene = chooseScene; newGame() }
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
        startb2.setOnAction { e -> primaryStage.scene = gameScene; players = 1; ai.createMap() }
        hbox5.children.add(startb2)
        val startb3 = Button("2 Players")
        startb3.setOnAction { e -> primaryStage.scene = gameScene; players = 2 }
        hbox5.children.add(startb3)

        currentstage = primaryStage
        primaryStage.scene = menuScene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    launch(MyForm::class.java)
}