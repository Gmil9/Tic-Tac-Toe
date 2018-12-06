package edu.uiowa

import javafx.application.Application
import javafx.application.Application.launch
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Alert

var menuScene: Scene? = null
var mainScene: Scene? = null
var currentScene: Scene? = null
var currentStage: Stage? = null

val groupset1: Array<ToggleButton?> = arrayOfNulls(4) // for toggle buttons
val tgroup1 = ToggleGroup()

var mainBoard = Board(3,false)    //main board
var currentBoard = Board(3,false) //mini game when playing ultimate

/**
 * These 3 change depending on the options chosen:
 *      How many players?
 *      If its ultimate or not
 *      If its played on easy or hard
 */
var players = 1
var isUlt = false
var difficulty = false

//interface for the board class
interface TheBoard{
    fun createGrid()
}

/**
 * Parameters:
 *      Size: size of the board
 *      Main: if this board object is the main board or a minigame
 * A new board is created every time a game is started and/or a mini game is started
 */
class Board(val size: Int, var main:Boolean): TheBoard{

    /**
     * winner:
     *      0 = no winner
     *      1 = X wins
     *      -1 = O wins
     * currentTurn: switches back and forth between turns
     * tempIndex: hard AI uses this to choose a specific grid index
     * ai: a new AI is created every time a new board is made, each AI is paired with a given board
     *
     * createGrid(): creates the representation of the board including the grid and buttons
     * class AI(): nested AI class in board since the AI depends on the board
     */

    var grid = GridPane()
    var winner = 0
    var currentTurn = true
    var tempIndex = 0
    val ai = AI()

    override fun createGrid(){
        for(i in 0..size-1){
            for(j in 0..size-1){
                val b = Button("")
                b.style = "-fx-font-size: 48px; -fx-text-fill: #b22222"
                b.setPrefSize((size * 99).toDouble(), (size * 99).toDouble())
                grid.add(b, i, j)

                if(isUlt && main) {
                    b.setOnAction { e -> tempIndex = size * i + j; gameController(false)}
                }else if(isUlt && !main){
                    b.setOnAction { e ->  tempIndex = size * i + j; takeTurn(currentBoard)}
                }else if(!isUlt && main){
                    b.setOnAction { e -> tempIndex = size * i + j; takeTurn(mainBoard)}
                }
            }
        }
    }

    inner class AI{

        /**
         * currentMap(): creates a list of open tiles based on the current game setting
         * chooseSquare(): AI simply picks a random open tile on the board
         * chooseSquareHard(): smarter AI that chooses squares depending on what the player chooses
         * pick(): used to pick a tile
         */

        var open_squaresList: MutableList<Int> = mutableListOf()
        var closed_squaresList: MutableList<Int> = mutableListOf()
        var options = listOf<Int>()

        var chooseIndex = -1

        fun currentMap(){
            open_squaresList.clear()
            closed_squaresList.clear()
            for(i in 0..grid.children.size-1){
                val tempNode = grid.children.get(i) as Button
                if(tempNode.text == ""){
                    open_squaresList.add(i)
                }else{
                    closed_squaresList.add(i)
                }
            }
        }

        fun chooseSquare(){
            val tempRandom = open_squaresList.random()
            val tempNode = grid.children.get(tempRandom) as Button

            tempNode.text = "O"
        }

        fun chooseSquareHard(){
            if(closed_squaresList.size == 1){
                when(closed_squaresList.get(0)){
                    4 -> {options = listOf(0, 2, 6, 8)
                    pick()}
                    0, 2, 6, 8  -> {options = listOf(4)
                        pick()}
                    1  -> {options = listOf(0, 2, 4, 7)
                        pick()}
                    3  -> {options = listOf(0, 4, 5, 6)
                        pick()}
                    5  -> {options = listOf(2, 3, 4, 8)
                        pick()}
                    7  -> {options = listOf(1, 4, 6, 8)
                        pick()}
                }
            }else if(chooseIndex != -1) {
                options = listOf(chooseIndex)
                pick()
                chooseIndex = -1
            }else{
                val tempRandom = open_squaresList.random()
                val tempNode = grid.children.get(tempRandom) as Button

                tempNode.text = "O"
            }
        }

        fun pick(){
            val tempRandom = options.random()
            val tempNode = grid.children.get(tempRandom) as Button

            tempNode.text = "O"
        }
    }
}

//Activated on each button click
fun takeTurn(b: Board) {
    val tempNode = b.grid.children.get(b.tempIndex) as Button
    if(tempNode.text != ""){
        message("Square already taken, Choose again")
        b.currentTurn = !b.currentTurn
    }else{
        if(b.currentTurn){
            tempNode.text = "X"
        }else{
            tempNode.text = "O"
        }
    }

    check()

    b.currentTurn = !b.currentTurn

    /**
     * if the player is playing against an AI, this section of code will run every time the player chooses a tile
     * Also chooses based on the difficulty chosen
     * The Hard AI is designed for the traditional 3x3 board, so it can only be plaed on it
     */
    if(players == 1 && b.currentTurn == false && b.winner == 0){
        b.ai.currentMap()
        if(!b.ai.open_squaresList.isEmpty()){
            if(b.size == 3 && difficulty == true){
                b.ai.chooseSquareHard()
            }else{
                b.ai.chooseSquare()
            }

            check()

            b.currentTurn = !b.currentTurn
        }
    }
}

/**
 * Checks for a winner in each direction:
 *      each row
 *      each column
 *      each diagonal
 *
 * Each loop reads in the board and creates a code
 *      the code reads if there is a winner,
 *      if not it tells the AI where to place its move
 */
fun checkBoard(b: Board) {
    var code = ""

    //checks each row
    for (i in 0..b.size - 1) {
        code = ""
        for (j in 0..b.size - 1) {
            val tempNode = b.grid.children.get((j * b.size) + i) as Button

            if (tempNode.text == "X") {
                code += "1"
            } else if (tempNode.text == "O") {
                code += "0"
            } else {
                code += "x"
            }

            if (code == "1".repeat(b.size) || code == "0".repeat(b.size)) {
                b.winner = if (b.currentTurn) 1 else -1
            }

            if (b.size == 3) {
                if (code == "11x") {
                    b.ai.chooseIndex = i + 6
                } else if (code == "x11") {
                    b.ai.chooseIndex = i
                } else if (code == "1x1") {
                    b.ai.chooseIndex = i + 3
                }
            }
        }
    }

    code = ""
    //checks each column
    for (i in 0..b.grid.children.size - 1) {
        val tempNode = b.grid.children.get(i) as Button

        if (tempNode.text == "X") {
            code += "1"
        }else if(tempNode.text == "O"){
            code += "0"
        }else{
            code += "x"
        }

        if (code == "1".repeat(b.size) || code == "0".repeat(b.size)) {
            b.winner = if (b.currentTurn) 1 else -1
        }

        if(b.size == 3) {
            if (code == "11x") {
                b.ai.chooseIndex = i  // 2, 5, or 8
            } else if (code == "x11") {
                b.ai.chooseIndex = i - 2 // 0, 3, or 6
            } else if (code == "1x1") {
                b.ai.chooseIndex = i - 1 // 1, 4, or 7
            }
        }
        if ((i+1) % b.size == 0) {
            code = ""
        }
    }

    //checks diagonally bottom left to top right
    code = ""
    for (i in 1..b.size) {
        val tempNode = b.grid.children.get((b.size - 1) * i) as Button

        if (tempNode.text == "X") {
            code += "1"
        }else if(tempNode.text == "O"){
            code += "0"
        }else{
            code += "x"
        }

        if (code == "1".repeat(b.size) || code == "0".repeat(b.size)) {
            b.winner = if (b.currentTurn) 1 else -1
        }

        if(b.size == 3) {
            if (code == "11x") {
                b.ai.chooseIndex = 6
            } else if (code == "x11") {
                b.ai.chooseIndex = 2
            } else if (code == "1x1") {
                b.ai.chooseIndex = 4
            }
        }
    }

    //checks diagonally top left to bottom right
    code = ""
    for (i in 0..b.size - 1) {
        val tempNode = b.grid.children.get((b.size + 1) * i) as Button

        if (tempNode.text == "X") {
            code += "1"
        } else if (tempNode.text == "O") {
            code += "0"
        } else {
            code += "x"
        }

        if (code == "1".repeat(b.size) || code == "0".repeat(b.size)) {
            b.winner = if (b.currentTurn) 1 else -1
        }

        if (b.size == 3) {
            if (code == "11x") {
                b.ai.chooseIndex = 4
            } else if (code == "x11") {
                b.ai.chooseIndex = 0
            } else if (code == "1x1") {
                b.ai.chooseIndex = 8
            }
        }
    }

    //checks if board is full with no winner
    var count = 0
    for (i in 0..b.grid.children.size - 1) {
        val tempNode = b.grid.children.get(i) as Button
        if (tempNode.text != "" && b.winner == 0) {
            count++
        }
    }

    //if no winner
    if (count == b.size * b.size) {
        if (b.main) {
            message("No Winner")
            currentStage?.scene = menuScene
        } else {
            message("No Winner")
            b.winner = 0
            currentStage?.scene = mainScene
        }
    }
}

fun check(){
    if(isUlt){
        checkBoard(currentBoard)
        checkminiWinner()
        checkBoard(mainBoard)
        checkmainWinner()
    }else{
        checkBoard(mainBoard)
        checkmainWinner()
    }
}

//checks mini game if there is a winner (for ultimate)
fun checkminiWinner(){
    val tempNode = mainBoard.grid.children.get(mainBoard.tempIndex) as Button
    if(currentBoard.winner == 1){
        tempNode.text = "X"
        currentStage?.scene = mainScene
    }else if(currentBoard.winner == -1){
        tempNode.text = "O"
        currentStage?.scene = mainScene
    }
}

//checks the main board if there is a winner
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

//creates the board(s) based on the starting options
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
        mainBoard.createGrid()

        mainScene = Scene(mainBoard.grid, (boardSize * 210).toDouble(),(boardSize * 210)
                .toDouble())
        currentStage?.scene = mainScene
    }else{
        currentBoard = Board(3, isMain)
        currentBoard.createGrid()

        currentScene = Scene(currentBoard.grid, 310.toDouble(), 310.toDouble())
        currentStage?.scene = currentScene
    }

}

class MyForm: Application() {

    override fun start(primaryStage: Stage) {

        val vbox = VBox()
        vbox.setPrefSize(500.0,250.0)  //first screen - choose size and ultimate

        val vbox1 = VBox()
        vbox1.setPrefSize(425.0,175.0) //second screen - choose player

        menuScene = Scene(vbox, 300.0, 250.0)
        var chooseScene = Scene(vbox1, 425.0, 175.0)

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
        hbox4.setPadding(Insets(15.0, 20.0, 15.0, 100.0));
        hbox4.setSpacing(15.0);
        vbox1.children.add(hbox4)

        val labelPlayers = Label("How many players?")
        labelPlayers.style = "-fx-font-size: 24px"
        hbox4.children.add(labelPlayers)

        val hbox5 = HBox()
        hbox5.setPadding(Insets(20.0, 30.0, 15.0, 60.0));
        hbox5.setSpacing(15.0);
        vbox1.children.add(hbox5)

        val startb2 = Button("1 Player (Easy)")
        startb2.setOnAction { e -> gameController(true); players = 1; difficulty = false}
        hbox5.children.add(startb2)
        val startb2b = Button("1 Player (Hard)")
        startb2b.setOnAction { e -> gameController(true); players = 1; difficulty = true}
        hbox5.children.add(startb2b)
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

        val labelHard = Label("Hard Only works if playing on a 3x3 board")
        hbox6.children.add(labelHard)

        currentStage = primaryStage
        primaryStage.scene = menuScene
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    launch(MyForm::class.java)
}