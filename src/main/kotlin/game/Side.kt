package game

enum class Side {
    WHITE, BLACK;


    fun flip(): Side {
        return if (this == BLACK) WHITE else BLACK
    }
}