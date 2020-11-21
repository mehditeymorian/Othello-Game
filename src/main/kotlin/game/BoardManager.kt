package game

class BoardManager {
    var board: Array<Array<Side?>> = Array(8){ arrayOfNulls(8) }
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
    private fun flipCellsAfterMove(x: Int, y: Int): List<Cell> {
        TODO()
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