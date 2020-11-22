package game

class Game : BoardEventListener {
    val boardManager: BoardManager = BoardManager(this)


    fun start() {
        boardManager.initBoard()
        boardManager.start()
    }

    override fun onGameFinish(winner: Side?) {
        boardManager.printBoard()
        val result = if (winner == null) "Draw" else "${winner.name} wins!"
        println(result)
    }


    override fun makeMove(turn: Side, availableCells: List<Cell>) {
        for (cell in availableCells) println("X:${cell.x} Y:${cell.y}")

        boardManager.printBoard()
        print("${turn.name} Move:")
        val line = readLine()?.split(" ")
        if (line == null) {
            println("Wrong Input, try again")
            makeMove(turn, availableCells)
        } else {
            val x = line[0].toInt()
            val y = line[1].toInt()
            boardManager.putDisk(x, y)

        }

    }
}