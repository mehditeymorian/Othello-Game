package ai

import game.*
import java.lang.Integer.max
import java.lang.Integer.min

class AIPlayer {

    private val boardCalculator = BoardCalculator()
    private lateinit var boardManager :  BoardManager
    private lateinit var boardCopy : Array<Array<Side?>>
    private lateinit var AITurn :Side
    private var defaultDepth : Int = 0
    private var depthCount : Int = 0
    private var MAX = 1000
    private var MIN = -1000


    fun search(state: Array<Array<Side?>>): Cell {
        AITurn = Side.BLACK
        //defaultDepth = 6
        var bestScore = MIN
        lateinit var bestMove : Cell

        val availableMoves =boardCalculator.availableCells(state , AITurn)
        availableMoves.forEach {
            copyBoard(state)
            boardManager = BoardManager(Game())
            boardManager.board = getCopyBoard()
            boardManager.turn = AITurn
            boardManager.putDisk(it.x , it.y , availableMoves)

            val moveScore = minValue(state , MIN , MAX , defaultDepth)
            if(bestScore<moveScore){
                bestScore = moveScore
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
        var v = MIN
        val availableMoves =boardCalculator.availableCells(state , Side.BLACK)
        availableMoves.forEach{
            copyBoard(state)
            boardManager = BoardManager(Game())
            boardManager.board = getCopyBoard()
            boardManager.turn = AITurn
            boardManager.putDisk(it.x , it.y , availableMoves)
            val minVal = minValue(state , a , b , depth)
            v = max(v , minVal)
            a = max(a , v)//????
            if (a >= b){
                return v
            }
        }
        return v
    }

    fun minValue(state: Array<Array<Side?>>, a: Int, b: Int, depth: Int): Int {
        var Opponent : Side = Side.BLACK
        if (AITurn == Side.BLACK){
            Opponent = Side.WHITE
        }
        if (isInTerminalState(state) || depth == depthCount){
            return getUtility(state)
        }
        depthCount++

        var v = MAX
        val availableMoves =boardCalculator.availableCells(state , Opponent)
        availableMoves.forEach{

            copyBoard(state)
            boardManager = BoardManager(Game())
            boardManager.board = getCopyBoard()
            boardManager.turn = AITurn
            boardManager.putDisk(it.x , it.y , availableMoves)
            val maxVal = maxValue(state , a , b , depth)
            v = min(v , maxVal)
            if (b <= a){
                return v
            }
            b = min(b , v)//????
        }
        return v
    }

    fun isInTerminalState(state: Array<Array<Side?>>): Boolean {
        var counter = 0
        state.forEach { i ->
            i.forEach { j ->
                if (j != null)
                    counter++
            }
        }
        return counter >=63
    }

    fun getUtility(state: Array<Array<Side?>>): Int {
        return 0
    }

    fun getCopyBoard(): Array<Array<Side?>>{
        return boardCopy
    }

    fun copyBoard( state :  Array<Array<Side?>>){
        boardCopy = state
    }
}