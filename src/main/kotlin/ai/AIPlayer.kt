package ai

import game.*
import java.lang.Integer.max
import java.lang.Integer.min

class AIPlayer(turn: Side) : Player(turn) {
    private val boardCalculator = BoardCalculator()
    private val featureWeights = DoubleArray(FEATURES_COUNT) { 1.0 }
    private val utilityCalculator = UtilityCalculator(boardCalculator, featureWeights)
    private lateinit var boardManager: BoardManager
    private var defaultDepth: Int = 0
    private var depthCount: Int = 0
    private var MAX = 1000
    private var MIN = -1000


    override fun move(state: Array<Array<Side?>>, availableCells: List<Cell>): Cell {
        var bestMove = availableCells[0]
        var bestPoint = Double.MIN_VALUE
        availableCells.forEach {
            val currentPoint = utilityCalculator.calculate(state, it, turn)
            if (currentPoint > bestPoint) {
                bestMove = it
                bestPoint = currentPoint
            }
        }
        return bestMove
    }

    fun search(state: Array<Array<Side?>>): Cell {
        var bestScore = MIN
        lateinit var bestMove: Cell
        var boundary: Array<Int> = arrayOf(MAX, MIN)

        val availableMoves = boardCalculator.availableCells(state, turn)
        availableMoves.forEach {

            var boardCopy = copyBoard(state)
            boardCopy[it.x][it.y] = turn
            val moveScore = minValue(boardCopy, boundary, defaultDepth)
            if (bestScore < moveScore) {
                bestScore = moveScore
                bestMove = it
            }
        }

        return bestMove
    }

    fun maxValue(state: Array<Array<Side?>>, boundary: Array<Int>, depth: Int): Int {

        if (isInTerminalState(state) || depth == this.depthCount) {
            return getUtility(state)
        }

        this.depthCount++
        var v = MIN
        val availableMoves = boardCalculator.availableCells(state, Side.BLACK)
        availableMoves.forEach {

            var boardCopy = copyBoard(state)
            boardCopy[it.x][it.y] = turn

            boardManager.putDisk(it.x, it.y, availableMoves)
            val minVal = minValue(boardCopy, boundary, depth)
            v = max(v, minVal)
            boundary[0] = max(boundary[0], v)//????
            if (boundary[0] >= boundary[1]) {
                return v
            }
        }
        return v
    }

    fun minValue(state: Array<Array<Side?>>, boundary: Array<Int>, depth: Int): Int {
        var Opponent: Side = Side.BLACK
        if (turn == Side.BLACK) {
            Opponent = Side.WHITE
        }
        if (isInTerminalState(state) || depth == depthCount) {
            return getUtility(state)
        }
        depthCount++

        var v = MAX
        val availableMoves = boardCalculator.availableCells(state, Opponent)
        availableMoves.forEach {


            var boardCopy = copyBoard(state)
            boardCopy[it.x][it.y] = Opponent

            val maxVal = maxValue(boardCopy, boundary, depth)
            v = min(v, maxVal)
            if (boundary[1] <= boundary[0]) {
                return v
            }
            boundary[1] = min(boundary[1], v)
        }
        return v
    }

    fun isInTerminalState(state: Array<Array<Side?>>): Boolean {
        var counter = 0
        state.forEach { i ->
            i.forEach { j ->
                if (j != null)
                    counter++
            }
        }
        return counter >= 63
    }

    fun getUtility(state: Array<Array<Side?>>): Int {
        return 0
    }


    fun copyBoard(state: Array<Array<Side?>>): Array<Array<Side?>> {
        var board: Array<Array<Side?>> = Array(boardCalculator.boardSize) { arrayOfNulls(boardCalculator.boardSize) }
        for (x in board.indices) for (y in board.indices) {
            val side = state[x][y]
            board[x][y] = if (side == null) null else if (side == Side.BLACK) Side.BLACK else Side.WHITE
        }
        return board
    }
}