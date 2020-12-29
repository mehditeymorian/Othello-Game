package train

import ai.AIPlayer
import game.Game
import game.Side
import kotlinx.coroutines.*
import kotlin.math.abs

const val GENERATION_SIZE = 20
const val VARIANCE_CHECK = 4
const val VARIANCE_THRESHOLD = 0.3

fun main() = runBlocking {
    val variances = DoubleArray(VARIANCE_CHECK)
    var lastGeneration: ArrayList<Gene>? = null

    while (produceNextGeneration(variances, lastGeneration)) {
        val generation = selectNextGeneration(lastGeneration)
        generation.mapIndexed { index, gene->
            launch(context = Dispatchers.Default) {
                // play the game
                playWithGeneration(gene,index,generation)
            }
        }.joinAll()

        lastGeneration = generation
    }


}

fun playWithGeneration(current: Gene, index: Int, generation: ArrayList<Gene>) {
    for (i in index until generation.size) {
        // gene doesn't play with itself
        if (current.id == generation[i].id) continue

        // create player for current gene
        val (currentFeatures, currentReducer) = separateWeights(current)
        val ai1 = AIPlayer(Side.BLACK,currentFeatures,currentReducer)

        // create player for other gene
        val (iFeatures, iReducer) = separateWeights(generation[i])
        val ai2 = AIPlayer(Side.WHITE,iFeatures,iReducer)

        // start the game
        val game = Game(ai1,ai2)
        game.start()

        // add to each gene fitness
        val (blackDisks, whiteDisks) = game.boardManager.getDisksCount()
        when {
            blackDisks > whiteDisks -> current.fitness += 3
            blackDisks < whiteDisks -> generation[i].fitness += 3
            else -> {
                current.fitness++
                generation[i].fitness++
            }
        }
    }
}

fun produceNextGeneration(variances: DoubleArray, genes: ArrayList<Gene>?): Boolean {
    if (genes == null) return true

    val newVariance = variance(genes)
    val sum = variances.sum() / VARIANCE_CHECK
    if (abs(sum-newVariance) < VARIANCE_THRESHOLD) return false

    for (i in 1 until variances.size) {
        variances[i-1] = variances[i]
    }

    variances[variances.size-1] = newVariance

    return true
}

fun selectNextGeneration(genes: ArrayList<Gene>?): ArrayList<Gene> {
    return if (genes == null) {
        val generation = initialGeneration(GENERATION_SIZE)
        generation.addAll(generateChildren(generation))
        generation
    }else rewardSelection(genes) // produce from lastGeneration
}

fun rewardSelection(genes: ArrayList<Gene>):ArrayList<Gene> {
    var count = 0
    val selection = ArrayList<Gene>()
    val sorted = genes.sortedBy {it.fitness }
    val sum = (sorted.size * (sorted.size+1))/2

    while (count++ != GENERATION_SIZE) {
        val rand = randNumber()
        for (i in sorted.size downTo 1) {
            val prob = (i*(i-1))/(2*sum)
            if (rand > prob){
                if (genes[i].selected) break
                genes[i].selected = true
                selection.add(genes[i])
                break
            }
        }
    }

    selection.forEach {
        it.selected = false
        it.fitness = 0
    }

    return selection
}

fun separateWeights(gene: Gene): Pair<DoubleArray,DoubleArray> {
    val featuresWeights = gene.weights.sliceArray(0 until 6)
    val reducerWeights = gene.weights.sliceArray(6 until 11)
    return Pair(featuresWeights,reducerWeights)
}

fun variance(genes: ArrayList<Gene>): Double {
    val fits = genes.stream().mapToInt { it.fitness }
    val sum_x2 = fits.reduce{ acc, fit -> acc +fit*fit }.asInt * 1.0
    var sumx_2 = fits.reduce{ acc, fit -> acc +fit }.asInt * 1.0
    sumx_2 *= sumx_2


    return (sum_x2 - (sumx_2/genes.size))/(genes.size-1)
}