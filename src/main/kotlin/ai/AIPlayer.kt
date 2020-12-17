package ai

import game.*
import kotlin.math.max
import kotlin.math.min

class AIPlayer(turn: Side) : Player(turn) {
    private val boardCalculator = BoardCalculator()
    private val featureWeights = DoubleArray(FEATURES_COUNT) { 1.0 }
    private val utilityCalculator = UtilityCalculator(boardCalculator, featureWeights)
    private var searchDepth: Int = 10
    private var depthCount: Int = 0



    override fun move(state: Array<Array<Side?>>, availableCells: List<Cell>): Cell {

        var boundary: Array<Double> = arrayOf(Double.MAX_VALUE, Double.MIN_VALUE)
        var bestMove = availableCells[0]
        var bestPoint = boundary[1]
        availableCells.forEach {
            depthCount = 0 ;
            val currentPoint = minValue(boardCalculator.copy(state) , boundary , it , turn)
            if (currentPoint > bestPoint) {
                bestMove = it
                bestPoint = currentPoint
                println(" best move : ${bestMove.x} , ${bestMove.y}     || best point : $bestPoint")
            }
        }
        return bestMove
    }



    fun maxValue(
        state: Array<Array<Side?>>,
        boundary: Array<Double>,
        cell: Cell,
        turn_max: Side
    ): Double {

        var Opponent: Side = Side.BLACK
        if (turn_max == Side.BLACK) {
            Opponent = Side.WHITE
        }

        state[cell.x][cell.y] = turn_max
        if (isInTerminalState(state) || searchDepth == depthCount) {
            return utilityCalculator.calculate(state,cell,turn_max )
        }

        this.depthCount++
        var v = boundary[1]
        val availableMoves = boardCalculator.availableCells(boardCalculator.copy(state), turn_max)
        availableMoves.forEach {

            val minVal = minValue(boardCalculator.copy(state), boundary, it, Opponent)
            v = max(v, minVal)
            println("MaxValue => DepthCount : $depthCount    || minVal : $minVal    || v : $v")

            if (v >= boundary[1]) {
                return v
            }
            boundary[0] = max(boundary[0], v)
        }
        return v
    }

    fun minValue(
        state: Array<Array<Side?>>,
        boundary: Array<Double>,
        cell: Cell,
        turn_min: Side
    ): Double {

        var Opponent: Side = Side.BLACK
        if (turn_min == Side.BLACK) {
            Opponent = Side.WHITE
        }

        state[cell.x][cell.y] = turn_min
        if (isInTerminalState(state) || searchDepth == depthCount) {
            return utilityCalculator.calculate(state,cell,turn_min )
        }
        this.depthCount++

        var v = boundary[0]
        val availableMoves = boardCalculator.availableCells(boardCalculator.copy(state), turn_min)
        availableMoves.forEach {

            val maxVal = maxValue(boardCalculator.copy(state), boundary, it , Opponent)
            v = min(v, maxVal)
            println("MinValue => DepthCount : $depthCount    || maxVal : $maxVal    || v : $v")

            if (v <= boundary[0]) {
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
}