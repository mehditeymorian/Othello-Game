package ai

import game.*

const val MAX_POTENTIAL_MOBILITY = 20.0
const val MAX_CAPTURED_CORNERS = 4.0
const val MAX_DISKS_DIFFERENCE = 60.0
const val FEATURES_COUNT = 6
const val MAX_MOBILITY = 20.0

const val FEATURE_DISK_DIFFERENCE = 0
const val FEATURE_OPPONENT_MOBILITY = 1
const val FEATURE_POTENTIAL_MOBILITY = 2
const val FEATURE_CORNER = 3
const val FEATURE_PARITY = 4
const val FEATURE_STABLE_DISKS = 5

class Utility(private val calculator: BoardCalculator, private val weights: DoubleArray) {


    fun calculate(state: Array<Array<Side?>>, side: Side): Double {
        return weights[FEATURE_DISK_DIFFERENCE] * disksDifferenceFeature(state, side) +
                weights[FEATURE_OPPONENT_MOBILITY] * opponentMobilityFeature(state, side) +
                weights[FEATURE_POTENTIAL_MOBILITY] * potentialMobilityFeature(state, side) +
                weights[FEATURE_CORNER] * cornerFeature(state, side) +
                weights[FEATURE_PARITY] * parityFeature(state, side) +
                weights[FEATURE_STABLE_DISKS] * stableDisksFeature(state, side)
    }

    private fun disksDifferenceFeature(state: Array<Array<Side?>>, side: Side): Double { // range 4 to 8
        val (whiteDisks, blackDisks) = state.countDisks()
        return if (side == Side.WHITE) (whiteDisks - blackDisks) / MAX_DISKS_DIFFERENCE
        else (blackDisks - whiteDisks) / MAX_DISKS_DIFFERENCE
    }


    // the less move opponent has the better it is
    private fun opponentMobilityFeature(state: Array<Array<Side?>>, side: Side): Double { // range 7 to 12
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

    // more corners we have the better it is
    private fun cornerFeature(state: Array<Array<Side?>>, side: Side): Double { // range 12 to 15
        var capturedCorners = 0
        cornerCells().forEach {
            if (state.getOrNull(it.x, it.y) == side) capturedCorners++
        }

        return capturedCorners / MAX_CAPTURED_CORNERS
    }

    // which player play the last move
    private fun parityFeature(state: Array<Array<Side?>>, side: Side): Double { // range 2 to 7
        val leftMoves = state.leftMoves()
        return if (leftMoves % 2 == 0) 1.0 else 0.0
    }


    // disks that don't change throughout the game
    private fun stableDisksFeature(state: Array<Array<Side?>>, side: Side): Double { // range 10 to 15

        val visitedCells = Array(state.size){BooleanArray(state.size)}
        val stableCells = arrayListOf<Cell>()

        //leftUp
        for (i in state.indices) {
            if (state[i][0] == side) {
                for (j in state.indices) {
                    if ((state[i][j] == side && !visitedCells[i][j]) &&
                            ((i == 0) || (j == 0) )) {
                        visitedCells[i][j] = true
                        stableCells.add(Cell(i, j))
                        continue
                    }else if (state[i][j] == side && !visitedCells[i][j]){
                        if (checkUp(state , side , i , j ) &&
                                checkLeftUp(state , side , i , j) &&
                                checkLeft(state , side , i , j) &&
                                (checkLeftDown(state , side , i , j) || checkRightUp(state , side , i , j))) {

                            visitedCells[i][j] = true
                            stableCells.add(Cell(i, j))
                            continue
                        }
                    } else {
                        visitedCells[i][j] = true
                        break
                    }
                }
            } else {
                visitedCells[i][0] = true
                break
            }
        }

        //rightUp
        for (i in state.indices) {
            if (state[i][state.size-1] == side) {
                for (j in state.size-1 downTo 0) {
                    if ((state[i][j] == side && !visitedCells[i][j]) &&
                            ((i == 0) || (j == state.size-1) )) {
                        visitedCells[i][j] = true
                        stableCells.add(Cell(i, j))
                        continue
                    }else if (state[i][j] == side && !visitedCells[i][j]){
                        if (checkUp(state , side , i , j ) &&
                                checkRightUp(state , side , i , j) &&
                                checkRight(state , side , i , j) &&
                                (checkLeftUp(state , side , i , j) || checkRightDown(state , side , i , j))) {

                            visitedCells[i][j] = true
                            stableCells.add(Cell(i, j))
                            continue
                        }
                    } else {
                        visitedCells[i][j] = true
                        break
                    }
                }
            } else {
                visitedCells[i][state.size-1] = true
                break
            }
        }
        //leftDown
        for (i in state.size-1 downTo 0) {
            if (state[i][0] == side) {
                for (j in state.indices) {
                    if ((state[i][j] == side && !visitedCells[i][j]) &&
                            ((i == state.size-1) || (j == 0) )) {
                        visitedCells[i][j] = true
                        stableCells.add(Cell(i, j))
                        continue
                    }else if (state[i][j] == side && !visitedCells[i][j]){
                        if (checkDown(state , side , i , j ) &&
                                checkLeftDown(state , side , i , j) &&
                                checkLeft(state , side , i , j) &&
                                (checkLeftUp(state , side , i , j) || checkRightDown(state , side , i , j))) {

                            visitedCells[i][j] = true
                            stableCells.add(Cell(i, j))
                            continue
                        }
                    } else {
                        visitedCells[i][j] = true
                        break
                    }
                }
            } else {
                visitedCells[i][0] = true
                break
            }
        }
        //rightDown
        for (i in state.size-1 downTo 0) {
            if (state[i][state.size-1] == side) {
                for (j in state.size-1 downTo 0) {
                    if ((state[i][j] == side && !visitedCells[i][j]) &&
                            ((i == state.size-1) || (j == state.size-1) )) {
                        visitedCells[i][j] = true
                        stableCells.add(Cell(i, j))
                        continue
                    }else if (state[i][j] == side && !visitedCells[i][j]){
                        if (checkDown(state , side , i , j ) &&
                                checkRightDown(state , side , i , j) &&
                                checkRight(state , side , i , j) &&
                                (checkRightUp(state , side , i , j) || checkLeftDown(state , side , i , j))) {

                            visitedCells[i][j] = true
                            stableCells.add(Cell(i, j))
                            continue
                        }
                    } else {
                        visitedCells[i][j] = true
                        break
                    }
                }
            } else {
                visitedCells[i][state.size-1] = true
                break
            }
        }

        //handle many turn side between opposite sides -->fixed
        for( i in visitedCells.indices){
            for( j in visitedCells.indices){
                if (!visitedCells[i][j] && state[i][j]==side){
                    if (i==0 || i==visitedCells.size-1){
                        if (isFillLeft(state , i , j) && isFillRight(state , i , j)){
                            stableCells.add(Cell(i,j))
                        }
                    }
                    else if ( j==0 || j==visitedCells.size-1 ){
                        if (isFillDown(state, i , j) && isFillUp(state , i , j)){
                            stableCells.add(Cell(i,j))
                        }
                    }else{
                        if (isFillUp(state,i,j)&&isFillDown(state,i,j)&&isFillRight(state,i,j)&&
                                isFillLeft(state,i,j)&& isFillRightUp(state,i,j)&& isFillLeftUp(state,i,j)&&
                                isFillLeftDown(state,i,j)&& isFillRightDown(state,i,j)){

                            stableCells.add(Cell(i,j))
                        }
                    }
                }
            }
        }

        return stableCells.size.toDouble()/64
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