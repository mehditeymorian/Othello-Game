package game

import kotlin.math.abs


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

fun manhattanDistance(c1: Cell, c2: Cell): Int {
    return abs(c1.x - c2.x) + abs(c1.y - c2.y)
}


// use to find stable disks in board
fun checkUp(state: Array<Array<Side?>>, side: Side , x : Int, y :Int): Boolean {
    var i = x
    while (i >= 0) {
        if (i == 0 && state[i][y]==side) {
            return true
        }
        if (state[i][y]!=side) {
            break
        }
        i--
    }
    return false
}
fun checkDown(state: Array<Array<Side?>>, side: Side , x : Int, y :Int): Boolean {
    var i = x
    while (i < state.size) {
        if (i == state.size-1 && state[i][y]==side)
            return true
        if (state[i][y]!=side)
            break
        i++
    }
    return false
}
fun checkLeft(state: Array<Array<Side?>>, side: Side , x : Int, y :Int): Boolean {
    var j = y
    while (j >= 0) {
        if (j == 0 && state[x][j]==side){
            return true
        }
        if (state[x][j]!=side) {
            break
        }
        j--
    }
    return false
}
fun checkRight(state: Array<Array<Side?>>, side: Side , x : Int, y :Int): Boolean {
    var j = y
    while (j < state.size) {
        if (j == state.size-1 && state[x][j]==side)
            return true
        if (state[x][j]!=side)
            break
        j++
    }
    return false
}
fun checkRightUp(state: Array<Array<Side?>>, side: Side , x : Int, y :Int): Boolean {
    var j = y
    var i = x
    while (i >= 0 && j<state.size) {
        if ((j == state.size-1 && state[i][j]==side) || (i == 0 && state[i][j]==side) ) {
            return true
        }
        if (state[i][j]!=side) {
            break
        }
        j++
        i--
    }
    return false
}

fun checkRightDown(state: Array<Array<Side?>>, side: Side , x : Int, y :Int): Boolean {
    var j = y
    var i = x
    while (i <state.size && j<state.size) {
        if ((j == state.size-1 && state[i][j]==side) || (i == state.size-1 && state[i][j]==side) )
            return true
        if (state[i][j]!=side)
            break
        j++
        i++
    }
    return false
}

fun checkLeftUp(state: Array<Array<Side?>>, side: Side , x : Int, y :Int): Boolean {
    var j = y
    var i = x
    while (i >= 0 && j >= 0 ) {
        if ((j == 0 && state[i][j]==side) || (i == 0 && state[i][j]==side) ){
            return true
        }
        if (state[i][j]!=side){
            break
        }
        j--
        i--
    }
    return false
}
fun checkLeftDown(state: Array<Array<Side?>>, side: Side, x : Int, y :Int): Boolean {
    var j = y
    var i = x
    while (i < state.size && j >= 0) {
        if ((i == state.size-1 && state[i][j]==side) || (j == 0 && state[i][j]==side) ) {
            return true
        }
        if (state[i][j]!=side) {
            break
        }
        j--
        i++
    }
    return false
}

fun isFillLeft(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (j in y downTo 0){
        if (state[x][j]==null)
            return false
    }
    return true
}
fun isFillRight(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (j in y until state.size){
        if (state[x][j]==null)
            return false
    }
    return true
}
fun isFillUp(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (i in x downTo 0){
        if (state[i][y]==null)
            return false
    }
    return true
}
fun isFillDown(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (i in x until state.size){
        if (state[i][y]==null)
            return false
    }
    return true
}
fun isFillLeftUp(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (i in x downTo 0){
        for (j in y downTo 0){
            if (state[i][j]==null)
                return false
        }
    }
    return true
}
fun isFillLeftDown(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (i in x until state.size){
        for (j in y downTo 0){
            if (state[i][j]==null)
                return false
        }
    }
    return true
}
fun isFillRightUp(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (i in x downTo 0){
        for (j in y until state.size){
            if (state[i][j]==null)
                return false
        }
    }
    return true
}
fun isFillRightDown(state: Array<Array<Side?>> , x : Int, y :Int): Boolean {
    for (i in x until state.size){
        for (j in y until state.size){
            if (state[i][j]==null)
                return false
        }
    }
    return true
}
