package train

data class Gene(val id: Int, val generation: Int, val weights: Array<Double>) {
    var fitness: Int = 0
}