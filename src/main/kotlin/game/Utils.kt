package game

fun Array<Array<Side?>>.copy(): Array<Array<Side?>> {
    val board: Array<Array<Side?>> = Array(BOARD_SIZE) { arrayOfNulls(BOARD_SIZE) }
    for (x in this.indices) for (y in this.indices) {
        val side = this[x][y]
        board[x][y] = side
    }
    return board
}

fun Array<Array<Side?>>.play(cell: Cell, turn: Side, calculator: BoardCalculator) {
    this[cell.x][cell.y] = turn // put disk
    calculator.flipCellsAfterMove(this,cell).forEach {
        this[it.x][it.y] = this[it.x][it.y]?.flip()
    } // flip disks
}

fun Array<Array<Side?>>.isFull(): Boolean {
    for (x in this)
        for (each in x)
            if (each == null)
                return false

    return true
}