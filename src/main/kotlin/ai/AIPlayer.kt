package ai

import game.*
import kotlin.math.max
import kotlin.math.min

private const val MAX_DEPTH = 15

class AIPlayer(turn: Side, featureWeights: DoubleArray, moveReducerWeights: DoubleArray) : Player(turn) {
    private val boardCalculator = BoardCalculator()
    private val utility = Utility(boardCalculator, featureWeights)
    private val moveReducer = MoveReducer(boardCalculator, moveReducerWeights)

    override fun move(state: Array<Array<Side?>>, availableCells: List<Cell>): Cell {


        val boundary = doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)// alpha beta
        var bestPoint = Double.MIN_VALUE
        var bestMove = Cell(-1, -1)
        moveReducer
            .reduce(availableCells as ArrayList<Cell>, state, 0, playerTurn)
            .forEach {
                val each = maxValue(state, boundary, it, playerTurn, 1)
                if (each >= bestPoint) {
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
        val flippedCells = state.play(cell, turn, boardCalculator)

        if (isTerminalState(state, depth)) {
            val utility = utility.calculate(state, turn)
            state.undoMove(cell, flippedCells)
            return utility
        }

        val opponent: Side = turn.flip()
        var bestPoint: Double
        var availableCells = moveReducer.reduce(boardCalculator.availableCells(state, opponent), state, depth, opponent)
        // if opponent has moves -> minimization
        if (availableCells.isNotEmpty()) {
            bestPoint = Double.MIN_VALUE
            availableCells.forEach {
                bestPoint = max(bestPoint, minValue(state, boundary, it, opponent, depth + 1))
                if (bestPoint >= boundary[1]) { // if bestPoint >= beta return bestPoint
                    state.undoMove(cell, flippedCells)
                    return bestPoint
                }
                boundary[0] = max(boundary[0], bestPoint) // alpha = max(alpha,bestPoint)
            }
        }
        else { // player doesnt have any move -> current player play again -> maximizing again
            bestPoint = Double.MAX_VALUE
            availableCells = moveReducer.reduce(boardCalculator.availableCells(state, turn),state, depth, turn)
            availableCells.forEach {
                bestPoint = min(bestPoint, maxValue(state, boundary, it, turn, depth + 1))
                if (bestPoint <= boundary[0]) { // if bestPoint <= alpha return bestPoint
                    state.undoMove(cell, flippedCells)
                    return bestPoint
                }
                boundary[1] = min(boundary[1], bestPoint) // alpha = min(beta,bestPoint)
            }
        }


        state.undoMove(cell, flippedCells)

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
        val flippedCells = state.play(cell, turn, boardCalculator)

        if (isTerminalState(state, depth)) {
            val utility = utility.calculate(state, turn)
            state.undoMove(cell, flippedCells)
            return utility
        }


        val opponent: Side = turn.flip()
        var bestPoint: Double
        var availableCells = moveReducer.reduce(boardCalculator.availableCells(state, opponent), state, depth, opponent)
        // if opponent has move -> maximizing
        if (availableCells.isNotEmpty()) {
            bestPoint = Double.MAX_VALUE
            availableCells.forEach {
                bestPoint = min(bestPoint, maxValue(state, boundary, it, opponent, depth + 1))
                if (bestPoint <= boundary[0]) { // if bestPoint <= alpha return bestPoint
                    state.undoMove(cell, flippedCells)
                    return bestPoint
                }
                boundary[1] = min(boundary[1], bestPoint) // alpha = min(beta,bestPoint)
            }
        }
        else { // no move for opponent -> current player play again -> minimizing again
            bestPoint = Double.MIN_VALUE
            availableCells = moveReducer.reduce(boardCalculator.availableCells(state, turn),state, depth, turn)
            availableCells.forEach {
                bestPoint = max(bestPoint, minValue(state, boundary, it, turn, depth + 1))
                if (bestPoint >= boundary[1]) { // if bestPoint >= beta return bestPoint
                    state.undoMove(cell, flippedCells)
                    return bestPoint
                }
                boundary[0] = max(boundary[0], bestPoint) // alpha = max(alpha,bestPoint)
            }
        }


        state.undoMove(cell, flippedCells)

        return bestPoint
    }

    // check whether we should stop going deeper in tree
    private fun isTerminalState(state: Array<Array<Side?>>, depth: Int): Boolean {
        return depth == MAX_DEPTH || state.isFull()
    }
}