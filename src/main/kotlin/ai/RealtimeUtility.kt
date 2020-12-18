package ai

import game.*
import kotlin.math.abs



class UtilityCalculator(private val calculator: BoardCalculator, private val weights: DoubleArray) {
    private val corners = cornerCells()
    val MAX_DISTANCE_TO_CORNER = 5.0
    val MAX_DISTANCE_TO_EDGE = 7.0
    val MAX_FLIP = 18.0



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
            val copy = state.copy()
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
        val copy = state.copy()
        copy.play(cell,turn,calculator)
        val opponentMoves = calculator.availableCells(copy, turn.flip()).size
        return (MAX_MOBILITY - opponentMoves) / MAX_MOBILITY
    }

    /* being in the C cells
    */
    private fun dangerCellsFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Int {
        val (corner, _) = nearestCornerTo(cell)
        val dangerCells = dangerCellOf(corner)
        // if in dangerCells:
        //      if has corner 1
        //      else -1
        // not in dangerCell 0
        return if (cell in dangerCells) if (turn == state.getOrNull(corner.x,corner.y)) 1 else -1 else 0
    }

    private fun wedgingFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double {
        val opponentSide = turn.flip()

        when (cell) {
            // top left corner
            Cell(0, 1) -> {
                if (state.getOrNull(0,0) == null && state.getOrNull(0,2) == opponentSide)
                    return -1.0
            }
            Cell(1, 1) -> {
                if (state.getOrNull(0,0) == null && state.getOrNull(2,2) == opponentSide)
                    return -1.0
            }
            Cell(1, 0) -> {
                if (state.getOrNull(0,0) == null && state.getOrNull(2,0) == opponentSide)
                    return -1.0
            }
            // top right corner
            Cell(0, 6)->{
                if (state.getOrNull(0,7) == null && state.getOrNull(0,5) == opponentSide)
                    return -1.0
            }
            Cell(1, 6)->{
                if (state.getOrNull(0,7) == null && state.getOrNull(2,5) == opponentSide)
                    return -1.0
            }
            Cell(1, 7)->{
                if (state.getOrNull(0,7) == null && state.getOrNull(2,7) == opponentSide)
                    return -1.0
            }
            // bottom left corner
            Cell(6, 0)->{
                if (state.getOrNull(7,0) == null && state.getOrNull(5,0) == opponentSide)
                    return -1.0
            }
            Cell(6, 1)->{
                if (state.getOrNull(7,0) == null && state.getOrNull(5,2) == opponentSide)
                    return -1.0
            }
            Cell(7, 1)->{
                if (state.getOrNull(7,0) == null && state.getOrNull(7,2) == opponentSide)
                    return -1.0
            }
            // bottom right corner
            Cell(6, 6)->{
                if (state.getOrNull(7,7) == null && state.getOrNull(5,5) == opponentSide)
                    return -1.0
            }
            Cell(6, 7)->{
                if (state.getOrNull(7,7) == null && state.getOrNull(5,7) == opponentSide)
                    return -1.0
            }
            Cell(7, 6)->{
                if (state.getOrNull(7,7) == null && state.getOrNull(7,5) == opponentSide)
                    return -1.0
            }
        }



        var neighbors: List<Side?>? = null
        if (cell.x == 0 || cell.x == BOARD_SIZE - 1) {// in top or bottom edge
            neighbors = listOf(state.getOrNull(cell.x, cell.y - 1), state.getOrNull(cell.x, cell.y + 1))
        } else if (cell.y == 0 || cell.y == BOARD_SIZE - 1) { // in left or right edge
            neighbors = listOf(state.getOrNull(cell.x - 1, cell.y), state.getOrNull(cell.x + 1, cell.y))
        }


        return if (isWedge(neighbors, opponentSide)) 1.0 else 0.0
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

    private fun isWedge(neighbors: List<Side?>?, side: Side): Boolean {
        if (neighbors == null) return false
        return neighbors.filterNotNull().filter { it == side }.size == 2
    }


}