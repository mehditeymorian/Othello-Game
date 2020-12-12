package game

class Game : BoardEventListener {
    private val boardManager = BoardManager(this)


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
        print("${turn.name} Move:")
        val line = readLine()?.split(" ")
        if (line == null) {
            println("Wrong Input, try again")
            makeMove(turn, availableCells)
        } else {
            val x = line[0].toInt()
            val y = line[1].toInt()
            boardManager.putDisk(x, y, availableCells)

        }

    }
}