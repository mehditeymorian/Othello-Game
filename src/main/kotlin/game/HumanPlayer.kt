package game

class HumanPlayer(turn: Side) : Player(turn) {



    override fun move(state: Array<Array<Side?>>, availableCells: List<Cell>): Cell {
        val line = readLine()?.split(" ")
        return if (line == null) {
            println("Wrong Input, try again")
            move(state, availableCells)
        } else {
            val x = line[0].toInt()
            val y = line[1].toInt()
            Cell(x,y)
        }
    }
}