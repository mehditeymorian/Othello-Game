package app

import ai.AIPlayer
import game.*

fun main() {

    println("Welcome to Shakespeare Tragedy, The Othello")
    println("Choose Game Mode")
    println("1> Human vs Human")
    println("2> Human vs AI")
    val choice = readLine()?.toInt() ?: 1
    val player1: Player
    val player2: Player
    when (choice) {
        2 ->{ // human vs AI
            player1 = HumanPlayer(Side.BLACK)
            val weights = doubleArrayOf(7.289 , 6.864 , 8.128 , 12.567 , 8.297 , 8.277)
            val secondWeights = doubleArrayOf(12.188 , 7.926 , 5.992 , 12.648 , 4.596 , 6.761)
            player2 = AIPlayer(Side.WHITE,weights,secondWeights)
        }
        else -> { // human vs human
            player1 = HumanPlayer(Side.BLACK)
            player2 = HumanPlayer(Side.WHITE)
        }
    }
    val game = Game(player1,player2)
    game.start()
}
