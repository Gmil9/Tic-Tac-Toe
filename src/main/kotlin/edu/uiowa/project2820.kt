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

var currentstage: Stage? = null
var menuScene: Scene? = null
var grid = GridPane()
var boardSize: Int = 3
var buttons = arrayOf<Array<Button>>()
var currentTurn = true
var winner = 0
var b = Board(boardSize)
var board = arrayOf<Array<Int>>()
var boardButtons = arrayOf<Array<Button>>()
var sizeInput: TextField ?= null

interface TheBoard{
    fun createBoard(): Array<Array<Int>>
    fun createBoardButtons(): Array<Array<Button>>
    fun checkBoard(board: Array<Array<Int>>)
    fun takeTurn(j: Int, i: Int)
    fun showBoard()
}

class Board(val size: Int): TheBoard{

    //board is created by making an array of rows
    //then each row has an array of columns
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

    override fun createBoardButtons(): Array<Array<Button>>{
        var count = 0
        for(i in 0..size-1){
            var bcols = arrayOf<Button>()
            for(j in 0..size-1){
                val b = Button("X")
                b.setPrefSize((size * 99).toDouble(), (size * 99).toDouble())
                grid.add(b, i, j)

                b.setOnAction { e -> takeTurn(i, j) }

                bcols += b
                count++
            }
            buttons += bcols
        }
        return buttons
    }

    override fun takeTurn(j: Int, i: Int) {
        if(board[i][j] != 0){
            print("Square already taken, Choose again\n\n")
            currentTurn = !currentTurn
        }else{
            if(currentTurn){
                board[i][j] = 1
            }else{
                board[i][j] = -1
            }
        }

        b.showBoard()
        b.checkBoard(board)
        if(winner == 1){
            //Player 1 wins "X"
            print("Player 1 Wins!")
            println()
            currentstage?.scene = menuScene
        }else if(winner == -1){
            //Player 2 wins "O"
            print("Player 2 Wins!")
            println()
            currentstage?.scene = menuScene
        }
        currentTurn = !currentTurn
    }

    //returns 1 if X wins, and -1 if O wins
    override fun checkBoard(board: Array<Array<Int>>) {

        for(i in 0..size-1){
            var count = 0
            for(j in 0..size-2){
                print("Board[i][j]: " + board[i][j] + " and Board[i][j+1]: " + board[i][j+1])
                println()
                if(board[i][j] == board[i][j+1] && board[i][j] != 0){
                    count++
                }
            }
            print("Count: " + count)
            println()
            if(count == size - 1){
                winner = if(currentTurn) 1 else -1
            }
        }

        for(i in 0..size-1){
            var count = 0
            for(j in 0..size-2){
                print("Board[j][i]: " + board[j][i] + " and Board[j][i+1]: " + board[j+1][i])
                println()
                if(board[j][i] == board[j+1][i] && board[j][i] != 0){
                    count++
                }
            }
            print("Count: " + count)
            println()
            if(count == size - 1){
                winner = if(currentTurn) 1 else -1
            }
        }

    }

    override fun showBoard() {
        for (r in 0..size-1){
            for(c in 0..size-1){
                if(board[r][c] == 0){
                    print("[ ]")
                }else if(board[r][c] == 1){
                    print("X")
                }else if(board[r][c] == -1){
                    print("O")
                }
            }
            println()
        }
    }
}

fun newGame(){
    val t = sizeInput as TextField
    boardSize = t.text.toInt()
    b = Board(boardSize)
    board = b.createBoard()
    boardButtons = b.createBoardButtons()
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
        hbox2.setSpacing(15.0);   // to make it look nicer
        vbox.children.add(hbox2)
        val startb = Button("Start Game") // text in button
        startb.setOnAction { e -> primaryStage.scene = chooseScene }    // handler for button click
        hbox2.children.add(startb)             // add button to vbox
        val exitb = Button("Exit Game") // text in button
        exitb.setOnAction { e -> primaryStage.close()}    // handler for button click
        hbox2.children.add(exitb)

        val hbox3 = HBox()
        //hbox3.setPadding(Insets(15.0, 12.0, 15.0, 12.0));
        //hbox3.setSpacing(15.0);   // to make it look nicer
        vbox1.children.add(hbox3)
        val hbox4 = HBox()
        //hbox4.setPadding(Insets(15.0, 12.0, 15.0, 12.0));
        //hbox4.setSpacing(15.0);   // to make it look nicer
        vbox1.children.add(hbox4)
        val labelSize = Label("Choose board size: ")
        hbox3.children.add(labelSize)
        val t = TextField()
        sizeInput = t
        hbox3.children.add(t)
        val startb2 = Button("Start") // text in button
        startb2.setOnAction { e -> primaryStage.scene = gameScene; newGame() }    // handler for button click
        hbox4.children.add(startb2)

        currentstage = primaryStage
        primaryStage.scene = menuScene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    // you can do some testing here, though unit testing needs to be
    // in the src/test/java directory

    launch(MyForm::class.java)
}