package train

import ai.AIPlayer
import game.Game
import game.Side
import kotlinx.coroutines.*
import kotlin.math.abs

const val GENERATION_SIZE = 20
const val VARIANCE_CHECK = 4
const val VARIANCE_THRESHOLD = 0.3
const val MIN_GENERATION_PRODUCTION = 5

fun main() = runBlocking {
    val variances = ArrayList<Double>()
    var lastGeneration: ArrayList<Gene>? = null

    while (produceNextGeneration(variances, lastGeneration)) {
        val generation = selectNextGeneration(lastGeneration)
        generationId++
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
    for (i in index+1 until generation.size) {

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
            blackDisks > whiteDisks -> current.fitness.getAndAdd(3)
            blackDisks < whiteDisks -> generation[i].fitness.getAndAdd(3)
            else -> {
                current.fitness.incrementAndGet()
                generation[i].fitness.incrementAndGet()
            }
        }
    }
}

fun produceNextGeneration(variances: ArrayList<Double>, genes: ArrayList<Gene>?): Boolean {
    if (genes == null || variances.size < MIN_GENERATION_PRODUCTION) return true

    val newVariance = variance(genes)
    val sum = variances.takeLast(VARIANCE_CHECK).sum() / VARIANCE_CHECK
    if (abs(sum-newVariance) < VARIANCE_THRESHOLD) return false

    variances.add(newVariance)

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
    val selection = ArrayList<Gene>()
    val sorted = genes.sortedBy {it.fitness.get() }
    val sum = (sorted.size * (sorted.size+1))/2

    while (selection.size != GENERATION_SIZE) {
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
        it.fitness.set(0)
    }

    return selection
}

fun separateWeights(gene: Gene): Pair<DoubleArray,DoubleArray> {
    val featuresWeights = gene.weights.sliceArray(0 until 6)
    val reducerWeights = gene.weights.sliceArray(6 until 12)
    return Pair(featuresWeights,reducerWeights)
}

fun variance(genes: ArrayList<Gene>): Double {
    val fits = genes.stream().mapToInt { it.fitness.get() }
    val sum_x2 = fits.reduce{ acc, fit -> acc +fit*fit }.asInt * 1.0
    var sumx_2 = fits.reduce{ acc, fit -> acc +fit }.asInt * 1.0
    sumx_2 *= sumx_2


    return (sum_x2 - (sumx_2/genes.size))/(genes.size-1)
}