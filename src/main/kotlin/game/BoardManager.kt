package game

class BoardManager {
    private val boardSize = 8
    var board: Array<Array<Side?>> = Array(boardSize) { arrayOfNulls(boardSize) }
    var turn: Side = Side.BLACK


    // all initialization
    fun start() {

    }

    fun initBoard() {
        // init board
        // 2 white 2 black

    }

    // make a move on board with respect to turn
    fun move(x: Int, y: Int) {
        board[x][y] = turn
        turn = if (turn == Side.BLACK) Side.WHITE else Side.BLACK
    }


    // returns available cells to make a move with respect to turn
    private fun availableCells(): List<Cell> {
        TODO()
    }

    // returns cells to be flipped out if a move happen with respect to turn
    fun flipCellsAfterMove(x: Int, y: Int): List<Cell> {
        val list: ArrayList<Cell> = ArrayList()

        // check top
        if (x != 0 && board[x - 1][y] != null) { // check if item is not on edge and next cell in that direction is not empty
            var xOffset = x // first disk with same color in top direction
            for (i in x - 1 downTo 0) if (board[x][y] == board[i][y]) { // check for the first cell with same side
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x - 1 downTo xOffset + 1) list.add(Cell(newX, y))

        }


        // check top right
        if (x != 0 && y != boardSize - 1 && board[x - 1][y + 1] != null) {
            var xOffset = x
            for (i in x - 1 downTo 0) if (board[x][y] == board[i][y + (x - i)]) {// row decrement, column increment
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x - 1 downTo xOffset + 1) list.add(Cell(newX, y + (x - newX)))
        }

        // check right
        if (y != 0 && board[x][y + 1] != null) {
            var yOffset = y
            for (i in y + 1 until boardSize) if (board[x][y] == board[x][i]) {// row no change, column increment
                yOffset = i
                break
            }

            if (yOffset != y) for (newY in y + 1 until yOffset) list.add(Cell(x, newY))
        }

        // check bottom right
        if (x != boardSize - 1 && y != boardSize - 1 && board[x + 1][y + 1] != null) {
            var xOffset = x
            for (i in x + 1 until boardSize) if (board[x][y] == board[i][y + (i - x)]) {// row increment, column increment
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x + 1 until xOffset) list.add(Cell(newX, y + (newX - x)))
        }

        // check bottom
        if (x != boardSize - 1 && board[x + 1][y] != null) {
            var xOffset = x
            for (i in x + 1 until boardSize) if (board[x][y] == board[i][y]) { // check for the first cell with same side
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x + 1 until xOffset) list.add(Cell(newX, y))

        }

        // check bottom left
        if (x != 0 && y != 0 && board[x + 1][y - 1] != null) {
            var xOffset = x
            for (i in x + 1 until boardSize) if (board[x][y] == board[i][y - (i - x)]) {// row increment, column decrement
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x + 1 until xOffset) list.add(Cell(newX, y - (newX - x)))
        }

        // check left
        if (y != 0 && board[x][y - 1] != null) {
            var yOffset = y
            for (i in y - 1 downTo 0) if (board[x][y] == board[x][i]) {// row no change, column decrement
                yOffset = i
                break
            }

            if (yOffset != y) for (newY in y - 1 downTo yOffset + 1) list.add(Cell(x, newY))
        }

        // check top left
        if (x != 0 && y != 0 && board[x - 1][y - 1] != null) {
            var xOffset = x
            for (i in x - 1 downTo 0) if (board[x][y] == board[i][y - (x - i)]) {// row increment, column increment
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x - 1 downTo  xOffset + 1) list.add(Cell(newX, y - (x - newX)))
        }

        return list
    }

    // return a boolean, indicating whether current player can make a move
    private fun canPlayerMakeMove(): Boolean {
        TODO()
    }

    // return a boolean indicating whether game is finished
    private fun isGameFinished(): Boolean {
        TODO()
    }

}