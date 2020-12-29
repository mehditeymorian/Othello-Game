package train

data class Gene(val generation: Int, val weights: FloatArray){
    var fitness: Int = 0
}