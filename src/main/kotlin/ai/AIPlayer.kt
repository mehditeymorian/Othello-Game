package ai

import game.*

class AIPlayer {


    // Initial values of
    // Aplha and Beta
    var MAX = 1000
    var MIN = -1000


    fun search(state: Array<Array<Side>>): Cell {
        return Cell(0, 0)
    }

    fun maxValue(state: Array<Array<Side?>>, a: Int, b: Int, depth: Int): Int {
        var boardCalculator = BoardCalculator()
        var boardManager = BoardManager(Game())

        if (isInTerminalState(state) ){
            return getUtility(state)
        }
        var maxValue = MIN
        var maxMove : Cell
        val availableMoves =boardCalculator.availableCells(state , Side.BLACK)
        availableMoves.forEach{
            boardManager.putDisk(it.x , it.y , availableMoves)
            if (maxValue < minValue(state , a , b , depth)){
                maxValue = minValue(state, a , b , depth)
                maxMove = it
            }
        }
        return maxValue
    }

    fun minValue(state: Array<Array<Side?>>, a: Int, b: Int, depth: Int): Int {
        return 0
    }

    fun isInTerminalState(state: Array<Array<Side?>>): Boolean {
        return true
    }

    fun getUtility(state: Array<Array<Side?>>): Int {
        return 0
    }

    

}