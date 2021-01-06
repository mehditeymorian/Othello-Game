package train

import java.util.concurrent.atomic.AtomicReference

@Suppress("ArrayInDataClass")
data class Gene(val id: Int, val generation: Int, val weights: DoubleArray) {
    var fitness = AtomicReference(0.0)
    var selected: Boolean = false

    override fun toString(): String {
        return "Gene($id)-> ${weights.joinToString(" , ") { String.format("%.3f", it) }}"
    }
}