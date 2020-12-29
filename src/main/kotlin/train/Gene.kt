package train

data class Gene(val id: Int, val generation: Int, val weights: DoubleArray) {
    var fitness: Int = 0
}