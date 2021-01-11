package game

const val BOARD_SIZE = 8

class BoardCalculator {
    var blackDisks: Int = 0
    var whiteDisks: Int = 0

    // returns available cells to make a move with respect to turn
    fun availableCells(state: Array<Array<Side?>>, turn: Side): ArrayList<Cell> {
        val moveList: ArrayList<Cell> = ArrayList()

        for (i in 0..7) {
            for (j in 0..7) {
                if (state[i][j] != null && turn == state[i][j]) {   // identify homes that have same color as turn color

                    //---------------------------------------- gooshe ha
                    if (i == 0 && j == 0) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_down")
                    }
                    if (i == 0 && j == 7) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_down")
                    }
                    if (i == 7 && j == 0) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_up")
                    }
                    if (i == 7 && j == 7) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_up")
                    }
                    //--------------------------------------- divare ha
                    if (j == 0 && 0 < i && i < 7) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "down")
                    }
                    if (j == 7 && 0 < i && i < 7) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "down")
                    }
                    if (i == 0 && 0 < j && j < 7) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right")
                    }
                    if (i == 7 && 0 < j && j < 7) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right")
                    }
                    //---------------------------------------- khoone haie dakheli
                    if (0 < i && i < 7 && 0 < j && j < 7) {
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_up")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "right_down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "down")
                        identifyOpponent(state, turn, moveList, Cell(i, j), "left_down")
                    }
                }
            }
        }
        return moveList
    }

    private fun identifyOpponent(
        board: Array<Array<Side?>>,
        turn: Side,
        moveList: ArrayList<Cell>,
        cell: Cell,
        direction: String
    ) {

        var oponent: Side = Side.BLACK
        if (turn == Side.BLACK) {
            oponent = Side.WHITE
        }

        when (direction) {

            "left" -> {
                var j_tmp = cell.y
                var j = cell.y

                while (j > 0) {
                    if (board[cell.x][j - 1] == null) {
                        j_tmp = j - 1
                        break
                    }
                    if (board[cell.x][j - 1] == turn) {
                        j_tmp = cell.y
                        break
                    }
                    if (board[cell.x][j - 1] != null && board[cell.x][j - 1] == oponent) {
                        j--
                    }
                }
                if (j_tmp < cell.y - 1) {
                    val cell = Cell(cell.x, j_tmp)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return

            }


            "left_up" -> {
                var j_tmp = cell.y
                var i_tmp = cell.x
                var j = cell.y
                var i = cell.x

                while (j > 0 && i > 0) {
                    if (board[i - 1][j - 1] == null) {
                        j_tmp = j - 1
                        i_tmp = i - 1
                        break
                    }
                    if (board[i - 1][j - 1] == turn) {
                        j_tmp = cell.y
                        i_tmp = cell.x
                        break
                    }
                    if (board[i - 1][j - 1] != null && board[i - 1][j - 1] == oponent) {
                        j--
                        i--
                    }
                }
                if (j_tmp < cell.y - 1 && i_tmp < cell.x - 1) {
                    val cell = Cell(i_tmp, j_tmp)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return

            }


            "up" -> {
                var i_tmp = cell.x
                var i = cell.x
                while (i > 0) {
                    if (board[i - 1][cell.y] == null) {
                        i_tmp = i - 1
                        break
                    }
                    if (board[i - 1][cell.y] == turn) {
                        i_tmp = cell.x
                        break
                    }
                    if (board[i - 1][cell.y] != null && board[i - 1][cell.y] == oponent) {
                        i--
                    }
                }
                if (i_tmp < cell.x - 1) {
                    val cell = Cell(i_tmp, cell.y)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return
            }


            "right_up" -> {
                var j_tmp = cell.y
                var i_tmp = cell.x
                var j = cell.y
                var i = cell.x

                while (j < 7 && i > 0) {
                    if (board[i - 1][j + 1] == null) {
                        j_tmp = j + 1
                        i_tmp = i - 1
                        break
                    }
                    if (board[i - 1][j + 1] == turn) {
                        j_tmp = cell.y
                        i_tmp = cell.x
                        break
                    }
                    if (board[i - 1][j + 1] != null && board[i - 1][j + 1] == oponent) {
                        j++
                        i--
                    }
                }
                if (j_tmp > cell.y + 1 && i_tmp < cell.x - 1) {
                    val cell = Cell(i_tmp, j_tmp)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return
            }


            "right" -> {
                var j_tmp = cell.y
                var j = cell.y
                while (j < 7) {
                    if (board[cell.x][j + 1] == null) {
                        j_tmp = j + 1
                        break
                    }
                    if (board[cell.x][j + 1] == turn) {
                        j_tmp = cell.y
                        break
                    }
                    if (board[cell.x][j + 1] != null && board[cell.x][j + 1] == oponent) {
                        j++
                    }
                }
                if (j_tmp > cell.y + 1) {
                    val cell = Cell(cell.x, j_tmp)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return
            }


            "right_down" -> {
                var j_tmp = cell.y
                var i_tmp = cell.x
                var j = cell.y
                var i = cell.x

                while (j < 7 && i < 7) {
                    if (board[i + 1][j + 1] == null) {
                        j_tmp = j + 1
                        i_tmp = i + 1
                        break
                    }
                    if (board[i + 1][j + 1] == turn) {
                        j_tmp = cell.y
                        i_tmp = cell.x
                        break
                    }
                    if (board[i + 1][j + 1] != null && board[i + 1][j + 1] == oponent) {
                        j++
                        i++
                    }
                }
                if (j_tmp > cell.y + 1 && i_tmp > cell.x + 1) {
                    val cell = Cell(i_tmp, j_tmp)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return
            }


            "down" -> {
                var i_tmp = cell.x
                var i = cell.x
                while (i < 7) {
                    if (board[i + 1][cell.y] == null) {
                        i_tmp = i + 1
                        break
                    }
                    if (board[i + 1][cell.y] == turn) {
                        i_tmp = cell.x
                        break
                    }
                    if (board[i + 1][cell.y] != null && board[i + 1][cell.y] == oponent) {
                        i++
                    }
                }
                if (i_tmp > cell.x + 1) {
                    val cell = Cell(i_tmp, cell.y)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return
            }


            "left_down" -> {
                var j_tmp = cell.y
                var i_tmp = cell.x
                var j = cell.y
                var i = cell.x

                while (j > 0 && i < 7) {
                    if (board[i + 1][j - 1] == null) {
                        j_tmp = j - 1
                        i_tmp = i + 1
                        break
                    }
                    if (board[i + 1][j - 1] == turn) {
                        j_tmp = cell.y
                        i_tmp = cell.x
                        break
                    }
                    if (board[i + 1][j - 1] != null && board[i + 1][j - 1] == oponent) {
                        j--
                        i++
                    }
                }
                if (j_tmp < cell.y - 1 && i_tmp > cell.x + 1) {
                    val cell = Cell(i_tmp, j_tmp)
                    if (!moveList.contains(cell))
                        moveList.add(cell)
                }
                return
            }


            else -> {
                return
            }

        }
    }


    // returns cells to be flipped out if a move happen with respect to turn
    fun flipCellsAfterMove(state: Array<Array<Side?>>, cell: Cell): List<Cell> {
        val list: ArrayList<Cell> = ArrayList()

        // check top
        if (cell.x != 0 && state[cell.x - 1][cell.y] != null) { // check if item is not on edge and next cell in that direction is not empty
            var xOffset = cell.x // first disk with same color in top direction
            for (i in cell.x - 1 downTo 0)
                if (!indicesOkay(i, cell.y) || state[i][cell.y] == null) break
                else if (state[cell.x][cell.y] == state[i][cell.y]) { // check for the first cell with same side
                    xOffset = i
                    break
                }

            if (xOffset != cell.x) for (newX in cell.x - 1 downTo xOffset + 1) list.add(Cell(newX, cell.y))

        }


        // check top right
        if (cell.x != 0 && cell.y != BOARD_SIZE - 1 && state[cell.x - 1][cell.y + 1] != null) {
            var xOffset = cell.x
            for (i in cell.x - 1 downTo 0)
                if (!indicesOkay(i, cell.y + (cell.x - i)) || state[i][cell.y + (cell.x - i)] == null) break
                else if (state[cell.x][cell.y] == state[i][cell.y + (cell.x - i)]) {// row decrement, column increment
                    xOffset = i
                    break
                }

            if (xOffset != cell.x) for (newX in cell.x - 1 downTo xOffset + 1) list.add(
                Cell(
                    newX,
                    cell.y + (cell.x - newX)
                )
            )
        }

        // check right
        if (cell.y != BOARD_SIZE - 1 && state[cell.x][cell.y + 1] != null) {
            var yOffset = cell.y
            for (i in cell.y + 1 until BOARD_SIZE)
                if (!indicesOkay(cell.x, i) || state[cell.x][i] == null) break
                else if (state[cell.x][cell.y] == state[cell.x][i]) {// row no change, column increment
                    yOffset = i
                    break
                }

            if (yOffset != cell.y) for (newY in cell.y + 1 until yOffset) list.add(Cell(cell.x, newY))
        }

        // check bottom right
        if (cell.x != BOARD_SIZE - 1 && cell.y != BOARD_SIZE - 1 && state[cell.x + 1][cell.y + 1] != null) {
            var xOffset = cell.x
            for (i in cell.x + 1 until BOARD_SIZE)
                if (!indicesOkay(i, cell.y + (i - cell.x)) || state[i][cell.y + (i - cell.x)] == null) break
                else if (state[cell.x][cell.y] == state[i][cell.y + (i - cell.x)]) {// row increment, column increment
                    xOffset = i
                    break
                }

            if (xOffset != cell.x) for (newX in cell.x + 1 until xOffset) list.add(Cell(newX, cell.y + (newX - cell.x)))
        }

        // check bottom
        if (cell.x != BOARD_SIZE - 1 && state[cell.x + 1][cell.y] != null) {
            var xOffset = cell.x
            for (i in cell.x + 1 until BOARD_SIZE)
                if (!indicesOkay(i, cell.y) || state[i][cell.y] == null) break
                else if (state[cell.x][cell.y] == state[i][cell.y]) { // check for the first cell with same side
                    xOffset = i
                    break
                }

            if (xOffset != cell.x) for (newX in cell.x + 1 until xOffset) list.add(Cell(newX, cell.y))

        }

        // check bottom left
        if (cell.x != BOARD_SIZE - 1 && cell.y != 0 && state[cell.x + 1][cell.y - 1] != null) {
            var xOffset = cell.x
            for (i in cell.x + 1 until BOARD_SIZE)
                if (!indicesOkay(i, cell.y - (i - cell.x)) || state[i][cell.y - (i - cell.x)] == null) break
                else if (state[cell.x][cell.y] == state[i][cell.y - (i - cell.x)]) {// row increment, column decrement
                    xOffset = i
                    break
                }

            if (xOffset != cell.x) for (newX in cell.x + 1 until xOffset) list.add(Cell(newX, cell.y - (newX - cell.x)))
        }

        // check left
        if (cell.y != 0 && state[cell.x][cell.y - 1] != null) {
            var yOffset = cell.y
            for (i in cell.y - 1 downTo 0)
                if (!indicesOkay(cell.x, i) || state[cell.x][i] == null) break
                else if (state[cell.x][cell.y] == state[cell.x][i]) {// row no change, column decrement
                    yOffset = i
                    break
                }

            if (yOffset != cell.y) for (newY in cell.y - 1 downTo yOffset + 1) list.add(Cell(cell.x, newY))
        }

        // check top left
        if (cell.x != 0 && cell.y != 0 && state[cell.x - 1][cell.y - 1] != null) {
            var xOffset = cell.x
            for (i in cell.x - 1 downTo 0)
                if (!indicesOkay(i, cell.y - (cell.x - i)) || state[i][cell.y - (cell.x - i)] == null) break
                else if (state[cell.x][cell.y] == state[i][cell.y - (cell.x - i)]) {// row increment, column increment
                    xOffset = i
                    break
                }

            if (xOffset != cell.x) for (newX in cell.x - 1 downTo xOffset + 1) list.add(
                Cell(
                    newX,
                    cell.y - (cell.x - newX)
                )
            )
        }

        return list
    }

    private fun indicesOkay(x: Int, y: Int): Boolean {
        return x in 0 until BOARD_SIZE && y in 0 until BOARD_SIZE
    }


    fun isGameFinished(board: Array<Array<Side?>>): Boolean {
        var black = 0
        var white = 0

        for (row in board)
            for (each in row)
                if (each == Side.BLACK) black++
                else if (each == Side.WHITE) white++


        this.blackDisks = black
        this.whiteDisks = white
        return (black + white) == (BOARD_SIZE * BOARD_SIZE)
    }

    fun getWinner(): Side? {
        return if (blackDisks > whiteDisks) Side.BLACK else if (whiteDisks > blackDisks) Side.WHITE else null
    }

    fun getDisksCount(): Pair<Int, Int> {
        return Pair(blackDisks, whiteDisks)
    }

}