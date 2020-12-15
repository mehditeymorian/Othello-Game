package ai

import game.*
import java.lang.Integer.max
import java.lang.Integer.min

class AIPlayer {

    val boardCalculator = BoardCalculator()
    val boardManager = BoardManager(Game())

    // Initial values of
    // Alpha and Beta
    var MAX = 1000
    var MIN = -1000
    var depthCount : Int = 0

    fun search(state: Array<Array<Side?>>): Cell {

        val stateCopy = state
        val defaultDepth = 6
        var bestScore = MIN
        lateinit var bestMove : Cell

        val availableMoves =boardCalculator.availableCells(state , Side.BLACK)
        availableMoves.forEach {

            //??need new initial state?
            boardManager.putDisk(it.x , it.y , availableMoves)

            val moveSore = minValue(state , MIN , MAX , defaultDepth)
            if(bestScore<moveSore){
                bestScore = moveSore
                bestMove=it
            }


        }

        return bestMove
    }

    fun maxValue(state: Array<Array<Side?>>, a: Int, b: Int, depth: Int): Int {
        if (isInTerminalState(state) || depth == this.depthCount){
            return getUtility(state)
        }
        this.depthCount++

        var maxVal = MIN
        val availableMoves =boardCalculator.availableCells(state , Side.BLACK)
        availableMoves.forEach{
            boardManager.putDisk(it.x , it.y , availableMoves)
            val mn = minValue(state , a , b , depth)
            maxVal = max(maxVal , mn)
 //         a = max(a , mn)
            if (a <= b){
                return maxVal
            }
        }
        return maxVal
    }

    fun minValue(state: Array<Array<Side?>>, a: Int, b: Int, depth: Int): Int {

        if (isInTerminalState(state) || depth == depthCount){
            return getUtility(state)
        }
        depthCount++

        var minVal = MAX
        val availableMoves =boardCalculator.availableCells(state , Side.WHITE)
        availableMoves.forEach{
            boardManager.putDisk(it.x , it.y , availableMoves)

            val mx = maxValue(state , a , b , depth)
            minVal = min(minVal , mx)

//            if(b > mx){
//                b = mx
//            }
            if (b <= a){
                return minVal
            }
        }
        return minVal
    }

    fun isInTerminalState(state: Array<Array<Side?>>): Boolean {
        return true
    }

    fun getUtility(state: Array<Array<Side?>>): Int {
        return 0
    }
}