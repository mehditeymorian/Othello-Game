package game

import kotlin.math.abs

fun Array<Array<Side?>>.copy(): Array<Array<Side?>> {
    val board: Array<Array<Side?>> = Array(BOARD_SIZE) { arrayOfNulls(BOARD_SIZE) }
    for (x in this.indices) for (y in this.indices) {
        val side = this[x][y]
        board[x][y] = side
    }
    return board
}

fun Array<Array<Side?>>.play(cell: Cell, turn: Side, calculator: BoardCalculator): List<Cell> {
    this[cell.x][cell.y] = turn // put disk
    val flipCellsAfterMove = calculator.flipCellsAfterMove(this, cell)
    flipCellsAfterMove.forEach {
        this[it.x][it.y] = this[it.x][it.y]?.flip()
    } // flip disks

    return flipCellsAfterMove
}

fun Array<Array<Side?>>.undoMove(cell: Cell, flippedCells: List<Cell>) {
    this[cell.x][cell.y] = null // remove disk
    flippedCells.forEach {
        this[it.x][it.y] = this[it.x][it.y]?.flip()
    }
}

fun Array<Array<Side?>>.isFull(): Boolean {
    for (x in this)
        for (each in x)
            if (each == null)
                return false

    return true
}

fun Array<Array<Side?>>.isFrontier(cell: Cell): Boolean {
    val (x, y) = cell

    listOf(
        Cell(x - 1, y - 1),
        Cell(x - 1, y),
        Cell(x - 1, y + 1),
        Cell(x, y - 1),
        Cell(x, y + 1),
        Cell(x + 1, y - 1),
        Cell(x + 1, y),
        Cell(x + 1, y + 1),
    ).forEach {
        if (it.isValid() && getOrNull(it.x, it.y) == null) return true
    }




    return false
}

fun Array<Array<Side?>>.getOrNull(x: Int, y: Int): Side? {
    return this.getOrNull(x)?.getOrNull(y)
}

fun Array<Array<Side?>>.leftMoves(): Int {
    var leftMoves = 0
    for (x in this)
        for (y in x)
            if (y == null) leftMoves++

    return leftMoves
}

fun Array<Array<Side?>>.countDisks(): Pair<Int, Int> {
    var whiteDisks = 0
    var blackDisks = 0
    for (x in this)
        for (y in x)
            if (y == Side.WHITE) whiteDisks++
            else if (y == Side.BLACK) blackDisks++

    return Pair(whiteDisks, blackDisks)
}

fun Array<Array<Side?>>.isStable(cell: Cell): Boolean {
    return false
}

fun manhattanDistance(c1: Cell, c2: Cell): Int {
    return abs(c1.x - c2.x) + abs(c1.y - c2.y)
}

