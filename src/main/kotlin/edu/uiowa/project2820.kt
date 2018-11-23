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

var current_stage: Stage? = null
var grid = GridPane()
var boardSize: Int = 3
var buttons = arrayOf<Array<Button>>()
var currentTurn = true
var winner = 0
var b = Board(boardSize)
var board = arrayOf<Array<Int>>()
var boardButtons = arrayOf<Array<Button>>()

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
            current_stage?.close()
        }else if(winner == -1){
            //Player 2 wins "O"
            print("Player 2 Wins!")
            println()
            current_stage?.close()
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

fun newGame(stage: Stage){
    current_stage = stage

    val entry = Scanner(System.`in`)
    print("Choose a board size between 3 and 6")
    boardSize = entry.nextInt()

    b = Board(boardSize)
    board = b.createBoard()
    boardButtons = b.createBoardButtons()

    grid.gridLinesVisibleProperty().set(true)

    stage.run {
        scene = Scene(grid, (boardSize * 100).toDouble(),(boardSize * 100).toDouble())
        show()
    }
}

class MyForm: Application() {

    override fun start(primaryStage: Stage) {
        newGame(primaryStage)
    }
}

fun main(args: Array<String>) {
    // you can do some testing here, though unit testing needs to be
    // in the src/test/java directory

    launch(MyForm::class.java)
}