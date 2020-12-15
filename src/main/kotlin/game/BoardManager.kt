package game

class BoardManager(private val eventListener: BoardEventListener) {
    val calculator : BoardCalculator = BoardCalculator()
    var board: Array<Array<Side?>> = Array(calculator.boardSize) { arrayOfNulls(calculator.boardSize) }
    var turn: Side = Side.BLACK



    fun initBoard() {


        // init board
        // 2 white 2 black
        board[3][3] = Side.WHITE
        board[3][4] = Side.BLACK
        board[4][3] = Side.BLACK
        board[4][4] = Side.WHITE
        calculator.blackDisks = 2
        calculator.whiteDisks = 2

    }

    // all initialization
    fun start() {
        eventListener.makeMove(turn, calculator.availableCells(board,turn))
    }


    // make a move on board with respect to turn
    fun putDisk(x: Int, y: Int, availableCells: List<Cell>) {
        if (availableCells.none { cell -> cell.x == x && cell.y == y }) {
            println("You can select only from available cells")
            eventListener.makeMove(turn, availableCells)
            return
        }


        board[x][y] = turn // put disk
        calculator.flipCellsAfterMove(board,Cell(x, y)).forEach {
            board[it.x][it.y] = board[it.x][it.y]?.flip()
        } // flip disks
        if (calculator.isGameFinished(board)) {
            eventListener.onGameFinish(calculator.getWinner())
            return
        }

        turn = turn.flip()
        var list = calculator.availableCells(board, turn)
        if (list.isEmpty()) { // no move possible
            println("No legal move for $turn")
            turn = turn.flip()
            list = calculator.availableCells(board, turn)
        }

        if (list.isEmpty()) {
            eventListener.onGameFinish(calculator.getWinner())
            return
        }

        eventListener.makeMove(turn, list)

    }

    fun printBoard() {
        println("===========================")
        print("   ")
        for (i in 0..7) print(" $i ")
        println()
        var i = 0
        board.forEach {
            print(" $i ")
            it.forEach { side: Side? -> print(if (side == null) "   " else if (side == Side.BLACK) " B " else " W ") }
            println()
            i++
        }
        println("===========================")
    }


    fun printBoardWithAvailableCells(availableCells: List<Cell>) {
        val copy: Array<Array<String?>> = Array(calculator.boardSize) { arrayOfNulls(calculator.boardSize) }

        board.forEachIndexed { x, row ->
            row.forEachIndexed { y, side ->
                copy[x][y] = if (side == null) "   " else if (side == Side.BLACK) " B " else " W "
            }
        }
        availableCells.forEach { copy[it.x][it.y] = " . " }

        println("===========================")
        print("   ")
        for (i in 0..7) print(" $i ")
        println()
        var i = 0
        copy.forEach {
            print(" $i ")
            it.forEach(::print)
            println()
            i++
        }
        println("===========================")
    }

    fun printScores() {
        println("Black : ${calculator.blackDisks}     White : ${calculator.whiteDisks}")
    }
}