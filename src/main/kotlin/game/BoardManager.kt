package game

import java.lang.StringBuilder

class BoardManager(private val eventListener: BoardEventListener) {
    private val calculator: BoardCalculator = BoardCalculator()
    var state: Array<Array<Side?>> = Array(BOARD_SIZE) { arrayOfNulls(BOARD_SIZE) }
    var turn: Side = Side.BLACK


    fun initBoard() {


        // init board
        // 2 white 2 black
        state[3][3] = Side.WHITE
        state[3][4] = Side.BLACK
        state[4][3] = Side.BLACK
        state[4][4] = Side.WHITE
        calculator.blackDisks = 2
        calculator.whiteDisks = 2

    }

    // all initialization
    fun start() {
        eventListener.makeMove(turn, calculator.availableCells(state, turn))
    }


    // make a move on board with respect to turn
    fun putDisk(x: Int, y: Int, availableCells: List<Cell>) {
        if (availableCells.none { cell -> cell.x == x && cell.y == y }) {
            println("You can select only from available cells")
            eventListener.makeMove(turn, availableCells)
            return
        }


        // play the move
        state.play(Cell(x, y), turn, calculator)
        if (calculator.isGameFinished(state)) { // show the result
            eventListener.onGameFinish(calculator.getWinner())
            return
        }

        turn = turn.flip()
        var list = calculator.availableCells(state, turn)
        if (list.isEmpty()) { // no move possible for turn
            println("No legal move for $turn")
            turn = turn.flip()
            list = calculator.availableCells(state, turn)
        }

        // no other player doesn't have move either, the game is finished
        if (list.isEmpty()) {
            eventListener.onGameFinish(calculator.getWinner())
            return
        }

        eventListener.makeMove(turn, list)

    }

    fun printBoard() {
        println(toString())
    }


    fun printBoardWithAvailableCells(availableCells: List<Cell>) {
        val copy: Array<Array<String?>> = Array(BOARD_SIZE) { arrayOfNulls(BOARD_SIZE) }

        state.forEachIndexed { x, row ->
            row.forEachIndexed { y, side ->
                copy[x][y] = if (side == null) "   " else if (side == Side.BLACK) " B " else " W "
            }
        }
        availableCells.forEach { copy[it.x][it.y] = " . " }

        println("===========================")
        print("   ")
        for (i in 0..7) print(" ${'A' + i} ")
        println()
        var i = 0
        copy.forEach {
            print(" ${i + 1} ")
            it.forEach(::print)
            println()
            i++
        }
        println("===========================")
    }

    fun printScores() {
        println("Black : ${calculator.blackDisks}     White : ${calculator.whiteDisks}")
    }

    fun getDisksCount(): Pair<Int, Int> {
        return calculator.getDisksCount()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("===========================\n")
        builder.append("   ")
        for (i in 0..7) builder.append(" ${'A' + i} ")
        builder.append("\n")
        var i = 0
        state.forEach {
            builder.append(" ${i + 1} ")
            it.forEach { side: Side? -> builder.append(if (side == null) "   " else if (side == Side.BLACK) " B " else " W ") }
            builder.append("\n")
            i++
        }
        builder.append("===========================\n")
        return builder.toString()
    }
}