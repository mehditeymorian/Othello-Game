package ai

import game.*
import kotlin.math.max
import kotlin.math.min

private const val MAX_DEPTH = 10

class AIPlayer(turn: Side) : Player(turn) {
    private val boardCalculator = BoardCalculator()
    private val featureWeights = DoubleArray(FEATURES_COUNT) { 1.0 }
    private val utility = Utility(boardCalculator, featureWeights)

    init {
        featureWeights[4] = 10.0
    }

    override fun move(state: Array<Array<Side?>>, availableCells: List<Cell>): Cell {


        val boundary = doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)// alpha beta
        var bestPoint = Double.MIN_VALUE
        var bestMove = Cell(-1, -1)
        availableCells.forEach {
            val each = maxValue(state.copy(), boundary, it, playerTurn, 0)
            if (each > bestPoint) {
                bestPoint = each
                bestMove = it
            }
        }

        return bestMove
    }


    private fun maxValue(
        state: Array<Array<Side?>>,
        boundary: DoubleArray,
        cell: Cell,
        turn: Side,
        depth: Int
    ): Double {
        //play the move
        state.play(cell, turn, boardCalculator)

        if (isTerminalState(state, depth))
            return utility.calculate(state, turn)


        val opponent: Side = turn.flip()
        var bestPoint = Double.MIN_VALUE
        boardCalculator.availableCells(state, opponent).forEach {
            bestPoint = max(bestPoint, minValue(state.copy(), boundary, it, opponent, depth + 1))
            if (bestPoint >= boundary[1]) return bestPoint // if bestPoint >= beta return bestPoint
            boundary[0] = max(boundary[0], bestPoint) // alpha = max(alpha,bestPoint)
        }


        return bestPoint
    }

    private fun minValue(
        state: Array<Array<Side?>>,
        boundary: DoubleArray,
        cell: Cell,
        turn: Side,
        depth: Int
    ): Double {
        //play the move
        state.play(cell, turn, boardCalculator)

        if (isTerminalState(state, depth))
            return utility.calculate(state, turn)


        val opponent: Side = turn.flip()
        var bestPoint = Double.MAX_VALUE
        boardCalculator.availableCells(state, opponent).forEach {
            bestPoint = min(bestPoint, maxValue(state.copy(), boundary, it, opponent, depth + 1))
            if (bestPoint <= boundary[0]) return bestPoint // if bestPoint <= alpha return bestPoint
            boundary[1] = min(boundary[1], bestPoint) // alpha = min(beta,bestPoint)
        }

        return bestPoint
    }

    private fun isTerminalState(state: Array<Array<Side?>>, depth: Int): Boolean {
        return depth == MAX_DEPTH || state.isFull()
    }
}