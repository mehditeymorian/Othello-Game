package ai

import game.*
import java.util.stream.Collectors
import kotlin.math.ceil


const val MAX_DISTANCE_TO_CORNER = 5.0
const val MAX_DISTANCE_TO_EDGE = 7.0
const val MAX_FLIP = 18.0
const val MOVE_EVALUATE_FEATURES = 7
const val MOVE_LIMIT = 3L

const val REDUCER_CORNER = 0
const val REDUCER_PLAYING_MIDDLE = 1
const val REDUCER_FLIP = 2
const val REDUCER_GIVING_CORNER = 3
const val REDUCER_OPPONENT_MOBILITY = 4
const val REDUCER_CORNER_NEIGHBOR = 5


const val NOT_EDGE = -1
const val VERTICAL_EDGE = 0
const val HORIZONTAL_EDGE = 1

class MoveReducer(private val calculator: BoardCalculator, private val weights: DoubleArray) {
    private val corners = cornerCells()


    fun reduce(moves: ArrayList<Cell>, state: Array<Array<Side?>>, depth: Int, turn: Side): List<Cell> {
        val list = moves.stream()
            .sorted { c1, c2 ->
                val c2Points = calculate(state, c2, turn)
                val c1Points = calculate(state, c1, turn)
                val sortValue = c2Points - c1Points
                if (sortValue > 0) 1 else if (sortValue < 0) -1 else 0
            }
            .limit(getMovesLimit(moves.size, depth))
            .collect(Collectors.toList())
        list.shuffle()
        return list

    }

    private fun getMovesLimit(size: Int, depth: Int): Long {
        return if (depth == 0) ceil((size * 3.0) / 4.0).toLong()
        else MOVE_LIMIT
    }


    private fun calculate(state: Array<Array<Side?>>, cell: Cell, side: Side): Double {
        return weights[REDUCER_CORNER] * takeCornerFeature(cell) +
                weights[REDUCER_PLAYING_MIDDLE] * playingMiddleFeature(state, cell) +
                weights[REDUCER_FLIP] * flipFeature(state, cell, side) +
                weights[REDUCER_GIVING_CORNER] * givingCornerFeature(state, cell, side) +
                weights[REDUCER_OPPONENT_MOBILITY] * opponentMobilityFeature(state, cell, side) +
                weights[REDUCER_CORNER_NEIGHBOR] * cornerNeighborFeature(state, cell, side)
    }

    /* if cell is corner it's really good
    */
    private fun takeCornerFeature(cell: Cell): Double { // range 10 to 15
        return if (cell in cornerCells()) 1.0 else 0.0
    }

    /* in the beginning of the game, cells in the middle are better
    */
    private fun playingMiddleFeature(state: Array<Array<Side?>>, cell: Cell): Double { // range 1 to 8
        // for last 30 disk return 0
        if (state.leftMoves() < 30) return 0.0

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

        return minDistance / MAX_DISTANCE_TO_EDGE
    }

    /* choose cell that flip less in beginning of the game
       and choose cell that flip more in the end of game
    */
    private fun flipFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double { // range 5 to 10
        state[cell.x][cell.y] = turn
        val flips = calculator.flipCellsAfterMove(state, cell).size
        state[cell.x][cell.y] = null // reset board to start value
        return if (state.leftMoves() < 20) flips / MAX_FLIP
        else (MAX_FLIP - flips) / MAX_FLIP
    }

    /* if choosing a move cause to give corner to opoonent in next turn
    * */
    private fun givingCornerFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double { // range 8 to 13
        val flippedCells = state.play(cell, turn, calculator)
        val opponentAvailableCell = calculator.availableCells(state, turn.flip())
        state.undoMove(cell, flippedCells) // undo move
        for (corner in cornerCells())
            if (corner in opponentAvailableCell)
                return -1.0
        return 0.0
    }

    /* if we choose this cell how many moves does opponent get
    * the less moves gives to opponent the better current move it is
    */
    private fun opponentMobilityFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double { // range 4 to 8
        val flippedCells = state.play(cell, turn, calculator)
        val opponentMoves = calculator.availableCells(state, turn.flip()).size
        state.undoMove(cell, flippedCells) // undo the move played
        return (MAX_MOBILITY - opponentMoves) / MAX_MOBILITY
    }

    /* check moves on the border of game surface
    * */
    private fun cornerNeighborFeature(state: Array<Array<Side?>>, cell: Cell, turn: Side): Double { // 3 to 7
        val (corner, _) = nearestCornerTo(cell)
        val dangerCells = dangerCellOf(corner)
        val cornerDisk = state.getOrNull(corner.x, corner.y)
        return if (cell in dangerCells) when (cornerDisk) {
            turn -> 1.0 // same color
            turn.flip() -> 0.0 // opposite color
            else -> cornerWedge(corner, cell, turn, state) // corner is null
        } else return when (isInEdges(cell)) {
            HORIZONTAL_EDGE -> {
                val neighbors = listOf(state.getOrNull(cell.x, cell.y - 1), state.getOrNull(cell.x, cell.y + 1))
                return if (isWedge(neighbors, turn.flip())) 1.0 else 0.0
            }
            VERTICAL_EDGE -> {
                val neighbors = listOf(state.getOrNull(cell.x - 1, cell.y), state.getOrNull(cell.x + 1, cell.y))
                return if (isWedge(neighbors, turn.flip())) 1.0 else 0.0
            }
            else -> 0.0
        }
    }

    private fun cornerWedge(corner: Cell, cell: Cell, turn: Side, state: Array<Array<Side?>>): Double {
        val opponentSide = turn.flip()
        return when (corner) {
            Cell(0, 0) -> when (cell) {
                // top left corner
                Cell(0, 1) -> if (state.getOrNull(0, 2) == opponentSide) -1.0 else 0.0
                Cell(1, 1) -> if (state.getOrNull(2, 2) == opponentSide) -1.0 else 0.0
                Cell(1, 0) -> if (state.getOrNull(2, 0) == opponentSide) -1.0 else 0.0
                else -> 0.0
            }
            Cell(0, 7) -> when (cell) {
                // top right corner
                Cell(0, 6) -> if (state.getOrNull(0, 5) == opponentSide) -1.0 else 0.0
                Cell(1, 6) -> if (state.getOrNull(2, 5) == opponentSide) -1.0 else 0.0
                Cell(1, 7) -> if (state.getOrNull(2, 7) == opponentSide) -1.0 else 0.0
                else -> 0.0
            }
            Cell(7, 0) -> when (cell) {
                // bottom left corner
                Cell(6, 0) -> if (state.getOrNull(5, 0) == opponentSide) -1.0 else 0.0
                Cell(6, 1) -> if (state.getOrNull(5, 2) == opponentSide) -1.0 else 0.0
                Cell(7, 1) -> if (state.getOrNull(7, 2) == opponentSide) -1.0 else 0.0
                else -> 0.0
            }
            Cell(7, 7) -> when (cell) {
                // bottom right corner
                Cell(6, 6) -> if (state.getOrNull(5, 5) == opponentSide) -1.0 else 0.0
                Cell(6, 7) -> if (state.getOrNull(5, 7) == opponentSide) -1.0 else 0.0
                Cell(7, 6) -> if (state.getOrNull(7, 5) == opponentSide) -1.0 else 0.0
                else -> 0.0
            }
            else -> 0.0
        }
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

    private fun isInEdges(cell: Cell): Int {
        return when {
            cell.x == 0 || cell.x == 7 -> HORIZONTAL_EDGE
            cell.y == 0 || cell.y == 7 -> VERTICAL_EDGE
            else -> NOT_EDGE
        }
    }


}