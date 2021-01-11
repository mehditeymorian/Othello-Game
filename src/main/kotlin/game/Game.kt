package game

class Game(private val player1: Player, private val player2: Player) : BoardEventListener {
    val boardManager = BoardManager(this)


    fun start() {
        boardManager.initBoard()
        boardManager.start()
    }

    override fun onGameFinish(winner: Side?) {
        boardManager.printBoard()
        val result = if (winner == null) "Draw" else "${winner.name} wins!"
        boardManager.printScores()
        println(result)

    }


    override fun makeMove(turn: Side, availableCells: List<Cell>) {

        boardManager.printBoardWithAvailableCells(availableCells)
        boardManager.printScores()
        println("${turn.name} Move:")
        val move = if (turn == player1.playerTurn) player1.move(boardManager.state, availableCells)
        else player2.move(boardManager.state, availableCells)
        boardManager.putDisk(move.x, move.y, availableCells)


    }
}