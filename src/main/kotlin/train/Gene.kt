package train

data class Gene(val id: Int,val generation: Int, val weights: FloatArray) {
    var fitness: Int = 0
}