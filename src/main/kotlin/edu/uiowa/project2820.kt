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

        for(i in 0..2){
            //checks for 3 in a row in each row
            if(board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] != 0){
                if(board[i][0] == 1){
                    return 1
                }else{
                    return -1
                }
            }
            //checks for 3 in a row in each column
            if(board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] != 0){
                if(board[i][0] == 1){
                    return 1
                }else{
                    return -1
                }
            }
            //checks for 3 in a row diagonal TopLeft to BottomRight
            if(board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != 0){
                if(board[0][0] == 1){
                    return 1
                }else{
                    return -1
                }
            }
            //checks for 3 in a row diagonal TopRight to BottomLeft
            if(board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != 0){
                if(board[0][2] == 1){
                    return 1
                }else{
                    return -1
                }
            }
        }

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

        if(board[r][c] != 0){
            print("Square already taken, Choose again\n\n")
            currentTurn = !currentTurn
        }else{
            if(currentTurn){
                board[r][c] = 1
            }else{
                board[r][c] = -1
            }
        }

        b.showBoard(board)
        winner = b.checkBoard(board)
        currentTurn = !currentTurn
    }

    if(winner == 1){
        //Player 1 wins "X"
        print("Player 1 Wins!")
    }else{
        //Player 2 wins "O"
        print("Player 2 Wins!")
    }
    print("git test")

}