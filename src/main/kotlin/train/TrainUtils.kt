package train

import kotlin.random.Random

fun randNumber(): Float {
    return Random.nextFloat()
}

fun randNumber(from: Int, to: Int): Float {
    return Random.nextInt(from,to) + randNumber()
}

fun randNumber(to: Int): Int {
    return Random.nextInt(to)
}

fun gaussian(): Float {
    val randNumber = randNumber(1, 5)
    return if (Random.nextBoolean()) randNumber else -randNumber
}