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

fun randArray(arr: DoubleArray, rand_num: Int ) {
    var count = 0 ;
    while (count < rand_num){
        val tmp = randNumber(geneSize) // cell number
        if (arr[tmp] == 0.0){
            arr[tmp] = randNumber(0 , 15)
            count++
        }
    }
}

fun gaussian(): Double {
    val randNumber = randNumber(1, 5)
    return if (Random.nextBoolean()) randNumber else -randNumber
}