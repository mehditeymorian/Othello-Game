package ai

import game.Cell
import game.Side

class AIPlayer {


    fun search(state: Array<Array<Side>>): Cell {
        return Cell(0, 0)
    }

    fun maxValue(state: Array<Array<Side>>, a: Int, b: Int, depth: Int): Int {
        return 0
    }

    fun minValue(state: Array<Array<Side>>, a: Int, b: Int, depth: Int): Int {
        return 0
    }

    fun isInTerminalState(state: Array<Array<Side>>): Boolean {
        return true
    }

    fun getUtility(state: Array<Array<Side>>): Int {
        return 0
    }

    

}