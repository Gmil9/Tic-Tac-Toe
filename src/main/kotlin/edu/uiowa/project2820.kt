package edu.uiowa
import java.util.*

// this is where you will begin the first version of your project
// but you will also need to connect this project with your github.uiowa.edu
// repository, as explained in the discussion section 9th November

interface TheBoard{
    fun createBoard(): Array<Array<Int>>
    fun checkBoard(board: Array<Array<Int>>): Int
    fun showBoard(board: Array<Array<Int>>)
}


class Board(val size: Int): TheBoard{

    //board is created by making an array of rows
    //then each row has an array of columns
    override fun createBoard(): Array<Array<Int>>{
        var rows = arrayOf<Array<Int>>()
        for(i in 1..size){
            var cols = arrayOf<Int>()
            for(j in 1..size){
                cols += 0
            }
            rows += cols
        }
        return rows
    }

    //returns 1 if X wins, and -1 if O wins
    override fun checkBoard(board: Array<Array<Int>>): Int {
        return 0
    }

    override fun showBoard(board: Array<Array<Int>>) {
        for (r in board){
            for(c in r){
                if(c == 0){
                    print("[ ]")
                }else if(c == 1){
                    print(" X ")
                }else{
                    print(" O ")
                }
            }
            println()
        }
    }
}

fun main(args: Array<String>) {
    // you can do some testing here, though unit testing needs to be
    // in the src/test/java directory

    val b = Board(3)
    var board = b.createBoard()

    var currentTurn = true
    var winner = 0

    while(winner == 0){
        val entry = Scanner(System.`in`)
        print("Enter a row")
        val r = entry.nextInt()
        println("$r\n")
        print("Enter a column")
        val c = entry.nextInt()
        println("$c\n")

        if(currentTurn){
            board[r][c] = 1
        }else{
            board[r][c] = -1
        }

        b.showBoard(board)
        winner = b.checkBoard(board)
        currentTurn = !currentTurn
    }

    if(winner == 1){
        //Player 1 wins "X"
    }else{
        //Player 2 wins "O"
    }


}