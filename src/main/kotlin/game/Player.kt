package game

abstract class Player(val turn: Side) {

    abstract fun move(state: Array<Array<Side?>>, availableCells: List<Cell>): Cell
}