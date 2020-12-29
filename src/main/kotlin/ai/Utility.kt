package ai

import game.*

const val MAX_POTENTIAL_MOBILITY = 20.0
const val MAX_CAPTURED_CORNERS = 4.0
const val MAX_DISKS_DIFFERENCE = 60.0
const val FEATURES_COUNT = 6
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

    private fun disksDifferenceFeature(state: Array<Array<Side?>>, side: Side): Double { // range 4 to 8
        val (whiteDisks, blackDisks) = state.countDisks()
        return if (side == Side.WHITE) (whiteDisks - blackDisks) / MAX_DISKS_DIFFERENCE
        else (blackDisks - whiteDisks) / MAX_DISKS_DIFFERENCE
    }


    // the less move opponent has the better it is
    private fun mobilityFeature(state: Array<Array<Side?>>, side: Side): Double { // range 7 to 12
        val opponentMoves = calculator.availableCells(state, side.flip()).size
        return (MAX_MOBILITY - opponentMoves) / MAX_MOBILITY
    }

    // the less potential mobility current player has the better it is
    private fun potentialMobilityFeature(state: Array<Array<Side?>>, side: Side): Double { // range 4 to 8
        var frontiersCount = 0
        for (x in state.indices)
            for (y in state.indices)
                if (state.getOrNull(x, y) == side && state.isFrontier(Cell(x, y)))
                    frontiersCount++ // current player disk

        return (MAX_POTENTIAL_MOBILITY - frontiersCount) / MAX_POTENTIAL_MOBILITY
    }

    private fun cornerFeature(state: Array<Array<Side?>>, side: Side): Double { // range 12 to 15
        var capturedCorners = 0
        cornerCells().forEach {
            if (state.getOrNull(it.x, it.y) == side) capturedCorners++
        }

        return capturedCorners / MAX_CAPTURED_CORNERS
    }

    // current player played
    private fun parityFeature(state: Array<Array<Side?>>, side: Side): Double { // range 2 to 7
        val leftMoves = state.leftMoves()
        return if (leftMoves % 2 == 0) 1.0 else 0.0
    }

    private fun stableDisksFeature(state: Array<Array<Side?>>, side: Side): Double { // range 10 to 15
//        val turnCells = arrayListOf<Cell>()
//        val stableCells = arrayListOf<Cell>()
//        val unstableCells = arrayListOf<Cell>()
//        val opponentMoveCells = calculator.availableCells(state , side.flip())
//
//        for ( row in state.indices){
//            for (col in state.indices){
//                if(state[row][col] == side ) {
//                    turnCells.add(Cell(row, col))
//                }
//            }
//        }
//
//        for (i in opponentMoveCells.indices){
//            var copyBoard =state.copy()
//            copyBoard.play(opponentMoveCells[i] , side.flip() , calculator)
//            for (j in turnCells.indices){
//                if (copyBoard[turnCells[j].x][turnCells[j].y] == side
//                    && copyBoard[turnCells[j].x][turnCells[j].y] != null){
//                    if (!stableCells.contains(turnCells[j])) {
//                        stableCells.add(turnCells[j])
//                    }
//                }else if (copyBoard[turnCells[j].x][turnCells[j].y] == side.flip()
//                    && copyBoard[turnCells[j].x][turnCells[j].y] != null){
//                    unstableCells.add(turnCells[j])
//                }
//            }
//        }
        //return stableCells
        return 0.0
    }

    private fun cornerCells(): Array<Cell> {
        return arrayOf(
            Cell(0, 0),
            Cell(0, 7),
            Cell(7, 0),
            Cell(7, 7)
        )
    }

    private fun check_stability_in8direction(s: Array<Array<Side?>>, row: Int, col: Int):Boolean {
        var i = row
        var j = col
        while ( j-- >= 0 && col != 0 ){         //left
            if ( s[row][j]==null)
                return false
        }
        i = row
        j = col
        while ( j++ <= 7 && col != 7 ){         //right
            if ( s[row][j]==null)
                return false
        }
        i = row
        j = col
        while ( i-- >= 0 && row != 0 ){         //up
            if ( s[row][j]==null)
                return false
        }
        i = row
        j = col
        while ( i-- >= 0 && row != 7 ){         //down
            if ( s[row][j]==null)
                return false
        }
        i = row
        j = col
        while ( j-- >= 0 && col != 0 && i-- >= 0 && row != 0 ){         //left-up
            if ( s[row][j]==null)
                return false
        }
        i = row
        j = col
        while ( j++ <= 7 && col != 7 && i-- >= 0 && row != 0  ){         //right-up
            if ( s[row][j]==null)
                return false
        }
        i = row
        j = col
        while ( j-- >= 0 && col != 0 &&  i++ >= 0 && row != 7 ){         //left-down
            if ( s[row][j]==null)
                return false
        }
        i = row
        j = col
        while ( j++ <= 7 && col != 7 &&  i++ >= 0 && row != 7){         //right-down
            if ( s[row][j]==null)
                return false
        }
        return true
    }



}