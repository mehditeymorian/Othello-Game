package game

class BoardManager(val eventListener: BoardEventListener) {
    private val boardSize = 8
    var board: Array<Array<Side?>> = Array(boardSize) { arrayOfNulls(boardSize) }
    var turn: Side = Side.BLACK
    var moveList: ArrayList<Cell> = ArrayList()


    fun initBoard() {
        // init board
        // 2 white 2 black
        board[3][3] = Side.WHITE
        board[3][4] = Side.BLACK
        board[4][3] = Side.BLACK
        board[4][4] = Side.WHITE
    }

    // all initialization
    fun start() {
        eventListener.makeMove(turn, availableCells())
    }


    // make a move on board with respect to turn
    fun putDisk(x: Int, y: Int, availableCells: List<Cell>) {
        if (availableCells.none { cell -> cell.x == x && cell.y == y }) {
            println("You can select only from available cells")
            eventListener.makeMove(turn,availableCells)
            return
        }


        board[x][y] = turn // put disk
        flipCellsAfterMove(x,y).forEach { board[it.x][it.y] = board[it.x][it.y]?.flip() } // flip disks
        if (isGameFinished()) {
            eventListener.onGameFinish(getWinner())
            return
        }

        turn = turn.flip()
        val list = availableCells()
        if (list.isEmpty()) { // no move possible
            turn = turn.flip()
            eventListener.makeMove(turn, availableCells())
        } else eventListener.makeMove(turn, list)



    }


    // returns available cells to make a move with respect to turn
    fun availableCells(): ArrayList<Cell> {
        moveList.clear()

        for (i in 0..7) {
            for (j in 0..7) {
                if (board[i][j] != null && turn == board[i][j]) {   // identify homes that have same color as turn color

                    //---------------------------------------- gooshe ha
                    if (i == 0 && j == 0) {
                        identifyOponent(board, i, j, "right")
                        identifyOponent(board, i, j, "down")
                        identifyOponent(board, i, j, "right_down")
                    }
                    if (i == 0 && j == 7) {
                        identifyOponent(board, i, j, "left")
                        identifyOponent(board, i, j, "down")
                        identifyOponent(board, i, j, "left_down")
                    }
                    if (i == 7 && j == 0) {
                        identifyOponent(board, i, j, "right")
                        identifyOponent(board, i, j, "up")
                        identifyOponent(board, i, j, "right_up")
                    }
                    if (i == 7 && j == 7) {
                        identifyOponent(board, i, j, "left")
                        identifyOponent(board, i, j, "up")
                        identifyOponent(board, i, j, "left_up")
                    }
                    //--------------------------------------- divare ha
                    if (j == 0 && 0 < i && i < 7) {
                        identifyOponent(board, i, j, "up")
                        identifyOponent(board, i, j, "right_up")
                        identifyOponent(board, i, j, "right")
                        identifyOponent(board, i, j, "right_down")
                        identifyOponent(board, i, j, "down")
                    }
                    if (j == 7 && 0 < i && i < 7) {
                        identifyOponent(board, i, j, "up")
                        identifyOponent(board, i, j, "leftt_up")
                        identifyOponent(board, i, j, "left")
                        identifyOponent(board, i, j, "left_down")
                        identifyOponent(board, i, j, "down")
                    }
                    if (i == 0 && 0 < j && j < 7) {
                        identifyOponent(board, i, j, "left")
                        identifyOponent(board, i, j, "left_down")
                        identifyOponent(board, i, j, "down")
                        identifyOponent(board, i, j, "right_down")
                        identifyOponent(board, i, j, "right")
                    }
                    if (i == 7 && 0 < j && j < 7) {
                        identifyOponent(board, i, j, "left")
                        identifyOponent(board, i, j, "left_up")
                        identifyOponent(board, i, j, "up")
                        identifyOponent(board, i, j, "right_up")
                        identifyOponent(board, i, j, "right")
                    }
                    //---------------------------------------- khoone haie dakheli
                    if (0 < i && i < 7 && 0 < j && j < 7) {
                        identifyOponent(board, i, j, "left")
                        identifyOponent(board, i, j, "left_up")
                        identifyOponent(board, i, j, "up")
                        identifyOponent(board, i, j, "right_up")
                        identifyOponent(board, i, j, "right")
                        identifyOponent(board, i, j, "right_down")
                        identifyOponent(board, i, j, "down")
                        identifyOponent(board, i, j, "left_down")
                    }
                }
            }
        }



        return moveList.distinct() as ArrayList<Cell>
    }

    fun identifyOponent(board: Array<Array<Side?>>, row: Int, column: Int, direction: String) {

        var oponent: Side = Side.BLACK
        if (turn == Side.BLACK) {
            oponent = Side.WHITE
        }

        when (direction) {

            "left" -> {
                var j_tmp = column
                var j = column

                while (j > 0) {
                    if (board[row][j - 1] == null) {
                        j_tmp = j - 1
                        break
                    }
                    if (board[row][j - 1] == turn) {
                        j_tmp = column
                        break
                    }
                    if (board[row][j - 1] != null && board[row][j - 1] == oponent) {
                        j--
                    }
                }
                if (j_tmp < column - 1) {
                    moveList.add(Cell(row, j_tmp))
                }
                return

            }


            "left_up" -> {
                var j_tmp = column
                var i_tmp = row
                var j = column
                var i = row

                while (j > 0 && i > 0) {
                    if (board[i - 1][j - 1] == null) {
                        j_tmp = j - 1
                        i_tmp = i - 1
                        break
                    }
                    if (board[i - 1][j - 1] == turn) {
                        j_tmp = column
                        i_tmp = row
                        break
                    }
                    if (board[i - 1][j - 1] != null && board[i - 1][j - 1] == oponent) {
                        j--
                        i--
                    }
                }
                if (j_tmp < column - 1 && i_tmp < row - 1) {
                    moveList.add(Cell(i_tmp, j_tmp))
                }
                return

            }


            "up" -> {
                var i_tmp = row
                var i = row
                while (i > 0) {
                    if (board[i - 1][column] == null) {
                        i_tmp = i - 1
                        break
                    }
                    if (board[i - 1][column] == turn) {
                        i_tmp = row
                        break
                    }
                    if (board[i - 1][column] != null && board[i - 1][column] == oponent) {
                        i--
                    }
                }
                if (i_tmp < row - 1) {
                    moveList.add(Cell(i_tmp, column))
                }
                return
            }


            "right_up" -> {
                var j_tmp = column
                var i_tmp = row
                var j = column
                var i = row

                while (j < 7 && i > 0) {
                    if (board[i - 1][j + 1] == null) {
                        j_tmp = j + 1
                        i_tmp = i - 1
                        break
                    }
                    if (board[i - 1][j + 1] == turn) {
                        j_tmp = column
                        i_tmp = row
                        break
                    }
                    if (board[i - 1][j + 1] != null && board[i - 1][j + 1] == oponent) {
                        j++
                        i--
                    }
                }
                if (j_tmp > column + 1 && i_tmp < row - 1) {
                    moveList.add(Cell(i_tmp, j_tmp))
                }
                return
            }


            "right" -> {
                var j_tmp = column
                var j = column
                while (j < 7) {
                    if (board[row][j + 1] == null) {
                        j_tmp = j + 1
                        break
                    }
                    if (board[row][j + 1] == turn) {
                        j_tmp = column
                        break
                    }
                    if (board[row][j + 1] != null && board[row][j + 1] == oponent) {
                        j++
                    }
                }
                if (j_tmp > column + 1) {
                    moveList.add(Cell(row, j_tmp))
                }
                return
            }


            "right_down" -> {
                var j_tmp = column
                var i_tmp = row
                var j = column
                var i = row

                while (j < 7 && i > 0) {
                    if (board[i + 1][j + 1] == null) {
                        j_tmp = j + 1
                        i_tmp = i + 1
                        break
                    }
                    if (board[i + 1][j + 1] == turn) {
                        j_tmp = column
                        i_tmp = row
                        break
                    }
                    if (board[i + 1][j + 1] != null && board[i + 1][j + 1] == oponent) {
                        j++
                        i++
                    }
                }
                if (j_tmp > column + 1 && i_tmp > row + 1) {
                    moveList.add(Cell(i_tmp, j_tmp))
                }
                return
            }


            "down" -> {
                var i_tmp = row
                var i = row
                while (i < 7) {
                    if (board[i + 1][column] == null) {
                        i_tmp = i + 1
                        break
                    }
                    if (board[i + 1][column] == turn) {
                        i_tmp = row
                        break
                    }
                    if (board[i + 1][column] != null && board[i + 1][column] == oponent) {
                        i++
                    }
                }
                if (i_tmp > row + 1) {
                    moveList.add(Cell(i_tmp, column))
                }
                return
            }


            "left_down" -> {
                var j_tmp = column
                var i_tmp = row
                var j = column
                var i = row

                while (j > 0 && i < 7) {
                    if (board[i + 1][j - 1] == null) {
                        j_tmp = j - 1
                        i_tmp = i + 1
                        break
                    }
                    if (board[i + 1][j - 1] == turn) {
                        j_tmp = column
                        i_tmp = row
                        break
                    }
                    if (board[i + 1][j - 1] != null && board[i + 1][j - 1] == oponent) {
                        j--
                        i++
                    }
                }
                if (j_tmp < column - 1 && i_tmp > row + 1) {
                    moveList.add(Cell(i_tmp, j_tmp))
                }
                return
            }


            else -> {
                println("No thing")
                moveList.add(Cell(-1, -1))
            }

        }
    }


    // returns cells to be flipped out if a move happen with respect to turn
    fun flipCellsAfterMove(x: Int, y: Int): List<Cell> {
        val list: ArrayList<Cell> = ArrayList()

        // check top
        if (x != 0 && board[x - 1][y] != null) { // check if item is not on edge and next cell in that direction is not empty
            var xOffset = x // first disk with same color in top direction
            for (i in x - 1 downTo 0)
                if (!indicesOkay(i,y) || board[i][y] == null) break
                else if (board[x][y] == board[i][y]) { // check for the first cell with same side
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x - 1 downTo xOffset + 1) list.add(Cell(newX, y))

        }


        // check top right
        if (x != 0 && y != boardSize - 1 && board[x - 1][y + 1] != null) {
            var xOffset = x
            for (i in x - 1 downTo 0)
                if (!indicesOkay(i,y + (x - i)) || board[i][y + (x - i)] == null) break
                else if (board[x][y] == board[i][y + (x - i)]) {// row decrement, column increment
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x - 1 downTo xOffset + 1) list.add(Cell(newX, y + (x - newX)))
        }

        // check right
        if (y != boardSize-1 && board[x][y + 1] != null) {
            var yOffset = y
            for (i in y + 1 until boardSize)
                if (!indicesOkay(x,i) || board[x][i] == null) break
                else if (board[x][y] == board[x][i]) {// row no change, column increment
                yOffset = i
                break
            }

            if (yOffset != y) for (newY in y + 1 until yOffset) list.add(Cell(x, newY))
        }

        // check bottom right
        if (x != boardSize - 1 && y != boardSize - 1 && board[x + 1][y + 1] != null) {
            var xOffset = x
            for (i in x + 1 until boardSize)
                if (!indicesOkay(i,y + (i-x)) || board[i][y + (i - x)] == null) break
                else if (board[x][y] == board[i][y + (i - x)]) {// row increment, column increment
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x + 1 until xOffset) list.add(Cell(newX, y + (newX - x)))
        }

        // check bottom
        if (x != boardSize - 1 && board[x + 1][y] != null) {
            var xOffset = x
            for (i in x + 1 until boardSize)
                if (!indicesOkay(i,y) || board[i][y] == null) break
                else if (board[x][y] == board[i][y]) { // check for the first cell with same side
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x + 1 until xOffset) list.add(Cell(newX, y))

        }

        // check bottom left
        if (x != 0 && y != 0 && board[x + 1][y - 1] != null) {
            var xOffset = x
            for (i in x + 1 until boardSize)
                if (!indicesOkay(i,y - (i - x)) || board[i][y - (i - x)] == null) break
                else if (board[x][y] == board[i][y - (i - x)]) {// row increment, column decrement
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x + 1 until xOffset) list.add(Cell(newX, y - (newX - x)))
        }

        // check left
        if (y != 0 && board[x][y - 1] != null) {
            var yOffset = y
            for (i in y - 1 downTo 0)
                if (!indicesOkay(x,i) || board[x][i] == null) break
                else if (board[x][y] == board[x][i]) {// row no change, column decrement
                yOffset = i
                break
            }

            if (yOffset != y) for (newY in y - 1 downTo yOffset + 1) list.add(Cell(x, newY))
        }

        // check top left
        if (x != 0 && y != 0 && board[x - 1][y - 1] != null) {
            var xOffset = x
            for (i in x - 1 downTo 0)
                if (!indicesOkay(i,y - (x - i)) || board[i][y - (x - i)] == null) break
                else if (board[x][y] == board[i][y - (x - i)]) {// row increment, column increment
                xOffset = i
                break
            }

            if (xOffset != x) for (newX in x - 1 downTo xOffset + 1) list.add(Cell(newX, y - (x - newX)))
        }

        return list
    }

    // return a boolean indicating whether game is finished
    fun isGameFinished(): Boolean {
        for (row in board)
            for (each in row)
                if (each == null)
                    return false

        return true
    }

    fun printBoard() {
        println("===========================")
        print("   ")
        for (i in 0..7) print(" $i ")
        println()
        var i = 0
        board.forEach {
            print(" $i ")
            it.forEach { side: Side? -> print(if (side == null) "   " else if (side == Side.BLACK) " B " else " W ") }
            println()
            i++
        }
        println("===========================")
    }

    fun getWinner(): Side? {
        var black = 0
        var white = 0

        for (row in board)
            for (each in row)
                if (each == Side.BLACK) black++
                else if (each == Side.WHITE) white++
        return if (black > white) Side.BLACK else if (white > black) Side.WHITE else null
    }

    fun indicesOkay(x: Int, y: Int): Boolean {
        return x < boardSize && y < boardSize
    }

    fun printBoardWithAvailableCells(availableCells: List<Cell>) {
        val copy: Array<Array<String?>> = Array(boardSize) { arrayOfNulls(boardSize) }

        board.forEachIndexed {x, row ->
            row.forEachIndexed{y, side ->
                copy[x][y] = if (side == null) "   " else if (side == Side.BLACK) " B " else " W "
            }
        }

        availableCells.forEach { copy[it.x][it.y] =  " . "}

        println("===========================")
        print("   ")
        for (i in 0..7) print(" $i ")
        println()
        var i = 0
        copy.forEach {
            print(" $i ")
            it.forEach(::print)
            println()
            i++
        }
        println("===========================")
    }
}