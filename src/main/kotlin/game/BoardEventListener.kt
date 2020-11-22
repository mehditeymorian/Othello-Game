package game

interface BoardEventListener {

    fun onGameFinish(winner: Side)

    fun disksToBeFlipped(list: List<Cell>)

    fun putDiskOnBoard(cell: Cell)

    fun makeMove(turn: Side, availableCells: List<Cell>)

}