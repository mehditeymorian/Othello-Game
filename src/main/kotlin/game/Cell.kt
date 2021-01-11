package game

data class Cell(val x: Int, val y: Int) {


    fun isValid(): Boolean {
        return x in 0..7 && y in 0..7
    }
}