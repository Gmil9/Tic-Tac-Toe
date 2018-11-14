package edu.uiowa

// this is where you will begin the first version of your project
// but you will also need to connect this project with your github.uiowa.edu
// repository, as explained in the discussion section 9th November

interface TheBoard{
    fun createBoard()
    fun checkBoard(board: Array<IntArray>): String
    fun showBoard(board: Array<IntArray>)
}


class Board(val size: Int): TheBoard{

    override fun createBoard() {
        TODO("not implemented")
    }

    override fun checkBoard(board: Array<IntArray>): String {
        TODO("not implemented")
    }

    override fun showBoard(board: Array<IntArray>) {
        TODO("not implemented")
    }
}

fun main(args: Array<String>) {
    // you can do some testing here, though unit testing needs to be
    // in the src/test/java directory
    val b = Board(3)
}