package game

interface BoardEventListener {

    fun onGameFinish(winner: Side?)

    fun makeMove(turn: Side, availableCells: List<Cell>)

}