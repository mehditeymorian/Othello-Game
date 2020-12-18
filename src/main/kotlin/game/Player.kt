package game

abstract class Player(val playerTurn: Side) {

    abstract fun move(state: Array<Array<Side?>>, availableCells: List<Cell>): Cell
}