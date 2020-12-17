package ai

import game.BOARD_SIZE
import game.BoardCalculator
import game.Cell
import game.Side
import kotlin.math.abs

const val MAX_MOBILITY = 20.0
const val MAX_DISTANCE_TO_CORNER = 5.0
const val MAX_DISTANCE_TO_EDGE = 7.0
const val MAX_FLIP = 18.0
const val FEATURES_COUNT = 7

class UtilityCalculator(private val calculator: BoardCalculator, private val weights: DoubleArray) {
    private val corners = cornerCells()


    fun calculate(state: Array<Array<Side?>>, cell: Cell, side: Side): Double {
//        return arrayOf(
//            cornerFeature(cell),
//            edgeFeature(cell),
//            maxFlipFeature(state, cell, side),
//            unflipableDisksFeature(state),
//            mobilityFeature(state, cell, side),
//            dangerCellsFeature(state, cell, side)
//        )
        return weights[0] * cornerFeature(cell) +
                weights[1] * edgeFeature(cell) +
                weights[2] * maxFlipFeature(state, cell, side) +
                weights[3] * unflipableDisksFeature(state) +
                weights[4] * mobilityFeature(state, cell, side) +
                weights[5] * dangerCellsFeature(state, cell, side) +
                weights[6] * wedgingFeature(state,cell,side)
    }

    /* calculate distance to nearest corner
       and return Max-distance - minDistance
    */
    private fun cornerFeature(cell: Cell): Double {
        val (_, distance) = nearestCornerTo(cell)
        return (MAX_DISTANCE_TO_CORNER - distance) / MAX_DISTANCE_TO_CORNER
    }

    /* calculate distance to nearest cell
       and return Max-distance - minDistance
    */
    private fun edgeFeature(cell: Cell): Double {
        val edges = arrayOf(
            Cell(0, cell.y), // top edge
            Cell(BOARD_SIZE - 1, cell.y), // bottom edge
            Cell(cell.x, 0), // left edge
            Cell(cell.x, BOARD_SIZE - 1)
        ) // right edge

        var minDistance = Int.MAX_VALUE
        for (each in edges) {
            val manhattanDistance = manhattanDistance(cell, each)
            if (manhattanDistance < minDistance) minDistance = manhattanDistance
        }

        return (MAX_DISTANCE_TO_EDGE - minDistance) / MAX_DISTANCE_TO_EDGE
    }

    /* if has a corner calculate number of cell that flips after this move
        otherwise return 0
    */
    private fun maxFlipFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double {
        return if (hasCorner(state, turn)) {
            val copy = calculator.copy(state)
            copy[cell.x][cell.y] = turn
            calculator.flipCellsAfterMove(copy, cell).size / MAX_FLIP
        } else 0.0
    }

    private fun unflipableDisksFeature(state: Array<Array<Side?>>): Int {
        return 0
    }

    /* if we choose this cell how many moves does opponent get
    * the less moves gives to opponent the better current move it is
    */
    private fun mobilityFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double {
        val copy = calculator.copy(state)
        copy[cell.x][cell.y] = turn
        calculator.flipCellsAfterMove(copy,cell).forEach {
            copy[it.x][it.y] = copy[it.x][it.y]?.flip()
        }
        val opponentMoves = calculator.availableCells(copy, turn.flip()).size
        return (MAX_MOBILITY - opponentMoves) / MAX_MOBILITY
    }

    /* being in the C cells
    */
    private fun dangerCellsFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Int {
        val (corner, _) = nearestCornerTo(cell)
        val dangerCells = dangerCellOf(corner)
        // todo: correct these weights
        // if in dangerCells:
        //      if has corner 1
        //      else -1
        // not in dangerCell 0
        return if (cell in dangerCells) if (hasCorner(state, turn)) 1 else -1 else 0
    }

    private fun wedgingFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double {
        val opponentSide = turn.flip()

        when (cell) {
            Cell(0, 1) -> { // top left corner
                if (state.getOrNull(0,0) == null && state.getOrNull(0,2) == opponentSide)
                    return -1.0
            }
            Cell(1, 1) -> { // top left corner
                if (state.getOrNull(0,0) == null && state.getOrNull(2,2) == opponentSide)
                    return -1.0
            }
            Cell(1, 0) -> { // top left corner
                if (state.getOrNull(0,0) == null && state.getOrNull(2,0) == opponentSide)
                    return -1.0
            }
            Cell(0, 6)->{ // top right corner
                if (state.getOrNull(BOARD_SIZE-1,BOARD_SIZE-1) == null && state.getOrNull(0,5) == opponentSide)
                    return -1.0
            }
            Cell(1, 6)->{ // top right corner
                if (state.getOrNull(BOARD_SIZE-1,BOARD_SIZE-1) == null && state.getOrNull(2,5) == opponentSide)
                    return -1.0
            }
            Cell(1, 7)->{ // top right corner
                if (state.getOrNull(BOARD_SIZE-1,BOARD_SIZE-1) == null && state.getOrNull(2,7) == opponentSide)
                    return -1.0
            }
            Cell(6, 0)->{ // bottom left corner
                if (state.getOrNull(7,0) == null && state.getOrNull(5,0) == opponentSide)
                    return -1.0
            }
            Cell(6, 1)->{ // bottom left corner
                if (state.getOrNull(7,0) == null && state.getOrNull(5,2) == opponentSide)
                    return -1.0
            }
            Cell(7, 1)->{ // bottom left corner
                if (state.getOrNull(7,0) == null && state.getOrNull(7,2) == opponentSide)
                    return -1.0
            }
            Cell(6, 6)->{ // bottom right corner
                if (state.getOrNull(7,7) == null && state.getOrNull(5,5) == opponentSide)
                    return -1.0
            }
            Cell(6, 7)->{ // bottom right corner
                if (state.getOrNull(7,7) == null && state.getOrNull(5,7) == opponentSide)
                    return -1.0
            }
            Cell(7, 6)->{ // bottom right corner
                if (state.getOrNull(7,7) == null && state.getOrNull(7,5) == opponentSide)
                    return -1.0
            }
        }



        if (cell.x == 0 || cell.x == BOARD_SIZE - 1) {// in top or bottom edge
            val neighbors = listOf(state.getOrNull(cell.x, cell.y - 1), state.getOrNull(cell.x, cell.y + 1))
            if (isWedge(neighbors, opponentSide)) return 1.0

        } else if (cell.y == 0 || cell.y == BOARD_SIZE - 1) { // in left or right edge
            val neighbors = listOf(state.getOrNull(cell.x - 1, cell.y), state.getOrNull(cell.x + 1, cell.y))
            if (isWedge(neighbors, opponentSide)) return 1.0
        }

        return 0.0
    }

    private fun manhattanDistance(c1: Cell, c2: Cell): Int {
        return abs(c1.x - c2.x) + abs(c1.y - c2.y)
    }

    private fun hasCorner(state: Array<Array<Side?>>, turn: Side): Boolean {
        for (each in corners) {
            val corner = state[each.x][each.y]
            if (turn == corner) return true
        }
        return false
    }

    private fun nearestCornerTo(cell: Cell): Pair<Cell, Int> {
        var minDistance = Int.MAX_VALUE
        // init value doesn't matter because every distance is lower than Int.MAX_VALUE
        // so there is always a cell in the board
        var nearestCorner = Cell(0, 0)
        for (each in corners) {
            val manhattanDistance = manhattanDistance(cell, each)
            if (manhattanDistance < minDistance) {
                minDistance = manhattanDistance
                nearestCorner = each
            }
        }
        return Pair(nearestCorner, minDistance)

    }

    private fun dangerCellOf(corner: Cell): Array<Cell> {
        if (corner.x == 0 && corner.y == 0) // top left corner
            return arrayOf(
                Cell(0, 1),
                Cell(1, 0),
                Cell(1, 1)
            )
        else if (corner.x == 0 && corner.y == (BOARD_SIZE - 1)) // top right corner
            return arrayOf(
                Cell(0, 6),
                Cell(1, 6),
                Cell(1, 7)
            )
        else if (corner.x == (BOARD_SIZE - 1) && corner.y == 0) // bottom left
            return arrayOf(
                Cell(6, 0),
                Cell(6, 1),
                Cell(7, 1)
            )
        else // bottom right corner
            return arrayOf(
                Cell(6, 6),
                Cell(6, 7),
                Cell(7, 6)
            )
    }

    private fun cornerCells(): Array<Cell> {
        return arrayOf(
            Cell(0, 0),
            Cell(0, 7),
            Cell(7, 0),
            Cell(7, 7)
        )
    }

    private fun isWedge(neighbors: List<Side?>, side: Side): Boolean {
        return neighbors.filterNotNull().filter { it == side }.size == 2
    }

    private fun Array<Array<Side?>>.getOrNull(x: Int, y: Int): Side? {
        return this.getOrNull(x)?.get(y)
    }

}