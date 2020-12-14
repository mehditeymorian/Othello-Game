package ai

import game.*

class AIPlayer {


    // Initial values of
    // Aplha and Beta
    var MAX = 1000
    var MIN = -1000
    var depthCount : Int = 0

    fun search(state: Array<Array<Side>>): Cell {
        return Cell(0, 0)
    }

    fun maxValue(state: Array<Array<Side?>>, a: Int, b: Int, depth: Int): Int {
        if (isInTerminalState(state) || depth == depthCount){
            return getUtility(state)
        }
        depthCount++
        var boardCalculator = BoardCalculator()
        var boardManager = BoardManager(Game())

        var maxValue = MIN
        val availableMoves =boardCalculator.availableCells(state , Side.BLACK)
        availableMoves.forEach{
            boardManager.putDisk(it.x , it.y , availableMoves)
            if (maxValue < minValue(state , a , b , depth)){
                maxValue = minValue(state, a , b , depth)
            }
        }
        return maxValue
    }

    fun minValue(state: Array<Array<Side?>>, a: Int, b: Int, depth: Int): Int {

        if (isInTerminalState(state) || depth == depthCount){
            return getUtility(state)
        }
        depthCount++
        var boardCalculator = BoardCalculator()
        var boardManager = BoardManager(Game())

        var minValue = MAX
        val availableMoves =boardCalculator.availableCells(state , Side.WHITE)
        availableMoves.forEach{
            boardManager.putDisk(it.x , it.y , availableMoves)
            if (minValue > maxValue(state , a , b , depth)){
                minValue = maxValue(state, a , b , depth)
            }
        }
        return minValue
    }

    fun isInTerminalState(state: Array<Array<Side?>>): Boolean {
        return true
    }

    fun getUtility(state: Array<Array<Side?>>): Int {
        return 0
    }

    

}