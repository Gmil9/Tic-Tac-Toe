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
var winner = 0
var b = Board(boardSize)
var board = arrayOf<Array<Int>>()       //background for tracking winner
var buttons = arrayOf<Array<Button>>() //visual representation of buttons
var sizeInput: TextField ?= null

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
                b.setPrefSize((size * 99).toDouble(), (size * 99).toDouble())
                grid.add(b, i, j)
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
        if(board[i][j] != 0){
            message("Square already taken, Choose again\n\n")
            currentTurn = !currentTurn
        }else{
            if(currentTurn){
                board[i][j] = 1
                bs.text = "X"
            }else{
                board[i][j] = -1
                bs.text = "O"
            }
        }

        b.checkBoard(board)
        if(winner == 1){
            //Player 1 wins "X"
            message("Player 1 Wins!")
            currentstage?.scene = menuScene
        }else if(winner == -1){
            //Player 2 wins "O"
            message("Player 2 Wins!")
            currentstage?.scene = menuScene
        }
        currentTurn = !currentTurn
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

//this creates a pop up message when called
fun message(s: String){
    val alert = Alert(AlertType.INFORMATION)
    alert.title = "Oops"
    alert.headerText = null
    alert.contentText = s
    alert.showAndWait()
}

//resets the board for a new game
fun newGame(){
    winner = 0
    currentTurn = true
    val t = sizeInput as TextField
    boardSize = t.text.toInt()
    b = Board(boardSize)
    board = b.createBoard()
    buttons = b.createGrid()
    grid.gridLinesVisibleProperty().set(true)
}

class MyForm: Application() {

    override fun start(primaryStage: Stage) {

        var gameScene = Scene(grid, (boardSize * 100).toDouble(),(boardSize * 100).toDouble())

        val vbox = VBox()
        vbox.setPrefSize(300.0,200.0)

        val vbox1 = VBox()
        vbox1.setPrefSize(300.0,200.0)

        menuScene = Scene(vbox, 300.0, 200.0)
        var chooseScene = Scene(vbox1, 300.0, 200.0)

        val hbox1 = HBox()
        hbox1.setPadding(Insets(15.0, 12.0, 15.0, 12.0));
        hbox1.setSpacing(15.0);   // to make it look nicer
        vbox.children.add(hbox1)
        val header = Label("Tic Tac Toe")
        hbox1.children.add(header)
        val hbox2 = HBox()
        hbox2.setPadding(Insets(15.0, 12.0, 15.0, 12.0));
        hbox2.setSpacing(15.0);
        vbox.children.add(hbox2)
        val startb = Button("Start Game")
        startb.setOnAction { e -> primaryStage.scene = chooseScene }
        hbox2.children.add(startb)
        val exitb = Button("Exit Game")
        exitb.setOnAction { e -> primaryStage.close()}
        hbox2.children.add(exitb)

        val hbox3 = HBox()
        //hbox3.setPadding(Insets(15.0, 12.0, 15.0, 12.0));
        //hbox3.setSpacing(15.0);
        vbox1.children.add(hbox3)
        val hbox4 = HBox()
        //hbox4.setPadding(Insets(15.0, 12.0, 15.0, 12.0));
        //hbox4.setSpacing(15.0);
        vbox1.children.add(hbox4)
        val labelSize = Label("Choose board size: ")
        hbox3.children.add(labelSize)
        val t = TextField()
        sizeInput = t
        hbox3.children.add(t)
        val startb2 = Button("Start")
        startb2.setOnAction { e -> primaryStage.scene = gameScene; newGame() }
        hbox4.children.add(startb2)

        currentstage = primaryStage
        primaryStage.scene = menuScene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    launch(MyForm::class.java)
}