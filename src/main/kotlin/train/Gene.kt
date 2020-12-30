package train

import java.util.concurrent.atomic.AtomicInteger

@Suppress("ArrayInDataClass")
data class Gene(val id: Int, val generation: Int, val weights: DoubleArray) {
    var fitness: AtomicInteger = AtomicInteger(0)
    var selected: Boolean = false
}