package ai

import game.*

const val MAX_POTENTIAL_MOBILITY = 20.0
const val MAX_CAPTURED_CORNERS = 4.0
const val MAX_DISKS_DIFFERENCE = 60.0
const val FEATURES_COUNT = 7
const val MAX_MOBILITY = 20.0

class Utility(private val calculator: BoardCalculator, private val weights: DoubleArray) {


    fun calculate(state: Array<Array<Side?>>, side: Side): Double {
        return weights[0] * disksDifferenceFeature(state, side) +
                weights[1] * stableDisksFeature(state, side) +
                weights[2] * mobilityFeature(state,side) +
                weights[3] * potentialMobilityFeature(state, side) +
                weights[4] * cornerFeature(state, side) +
                weights[5] * parityFeature(state, side)
    }

    private fun disksDifferenceFeature(state: Array<Array<Side?>>, side: Side): Double {
        val (whiteDisks, blackDisks) = state.countDisks()
        return if (side == Side.WHITE) (whiteDisks - blackDisks) / MAX_DISKS_DIFFERENCE
        else (blackDisks - whiteDisks) / MAX_DISKS_DIFFERENCE
    }

    private fun stableDisksFeature(state: Array<Array<Side?>>, side: Side): Double {
        return 0.0
    }

    // the less move opponent has the better it is
    private fun mobilityFeature(state: Array<Array<Side?>>, side: Side): Double {
        val opponentMoves = calculator.availableCells(state, side.flip()).size
        return (MAX_MOBILITY - opponentMoves) / MAX_MOBILITY
    }

    // the less potential mobility current player has the better it is
    private fun potentialMobilityFeature(state: Array<Array<Side?>>, side: Side): Double {
        var frontiersCount = 0
        for (x in state.indices)
            for (y in state.indices)
                if (state.getOrNull(x, y) == side && state.isFrontier(
                        Cell(
                            x,
                            y
                        )
                    )
                ) frontiersCount++ // current player disk

        return (MAX_POTENTIAL_MOBILITY - frontiersCount) / MAX_POTENTIAL_MOBILITY
    }

    private fun cornerFeature(state: Array<Array<Side?>>, side: Side): Double {
        var capturedCorners = 0
        cornerCells().forEach {
            if (state.getOrNull(it.x, it.y) == side) capturedCorners++
        }

        return capturedCorners / MAX_CAPTURED_CORNERS
    }

    // current player played
    private fun parityFeature(state: Array<Array<Side?>>, side: Side): Double {
        val leftMoves = state.leftMoves()
        return if (leftMoves % 2 == 0) 1.0 else 0.0
    }

    private fun cornerCells(): Array<Cell> {
        return arrayOf(
            Cell(0, 0),
            Cell(0, 7),
            Cell(7, 0),
            Cell(7, 7)
        )
    }

}