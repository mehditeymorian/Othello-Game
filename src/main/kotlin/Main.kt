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
            player2 = AIPlayer(Side.WHITE,DoubleArray(10) { 1.0 },DoubleArray(10) { 1.0 })
        }
        else -> { // human vs human
            player1 = HumanPlayer(Side.BLACK)
            player2 = HumanPlayer(Side.WHITE)
        }
    }
    val game = Game(player1,player2)
    game.start()
}
