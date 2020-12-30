package train

import kotlin.random.Random

fun randNumber(): Double {
    return Random.nextDouble()
}

fun randNumber(from: Int, to: Int): Double {
    return Random.nextInt(from,to) + randNumber()
}

fun randNumber(to: Int): Int {
    return Random.nextInt(to)
}

fun gaussian(): Double {
    val randNumber = randNumber(1, 5)
    return if (Random.nextBoolean()) randNumber else -randNumber
}