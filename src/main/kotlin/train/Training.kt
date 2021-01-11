package train

import ai.AIPlayer
import game.Game
import game.Side
import kotlinx.coroutines.*
import kotlin.math.abs

const val GENERATION_SIZE = 12
const val VARIANCE_CHECK = 4
const val VARIANCE_THRESHOLD = 0.05
const val MIN_GENERATION_PRODUCTION = 5

fun main() = runBlocking {
    initLogger()
    val variances = ArrayList<Double>() // variances of generations
    var lastGeneration: ArrayList<Gene>? = null

    while (produceNextGeneration(variances, lastGeneration)) {
        generationId++
        val generation = selectNextGeneration(lastGeneration)
        logGeneration(generation)
        val gamePool = createGamePool(generation.size)
        // create a coroutine for each game
        gamePool.map { pair->
            launch(context = Dispatchers.Default) {
                val g1 = generation[pair.first]
                val g2 = generation[pair.second]
                // play the game
                playWithGeneration(g1,g2)
            }
        }.joinAll()

        lastGeneration = generation
    }


}

// create a list of gene pair to play with each other
fun createGamePool(size: Int): ArrayList<Pair<Int, Int>> {
    val pool = ArrayList<Pair<Int, Int>>()

    for (i in 0 until size)
        for (j in i+1 until size)
            pool.add(Pair(i,j))

    return pool
}

fun playWithGeneration(current: Gene,opponent: Gene) {

        // create player for current gene
        val (currentFeatures, currentReducer) = separateWeights(current)
        val ai1 = AIPlayer(Side.BLACK,currentFeatures,currentReducer)

        // create player for other gene
        val (iFeatures, iReducer) = separateWeights(opponent)
        val ai2 = AIPlayer(Side.WHITE,iFeatures,iReducer)

        // start the game
        val game = Game(ai1,ai2)
        game.start()

        // add to each gene fitness
        val disksCount = game.boardManager.getDisksCount()
        val (blackDisks, whiteDisks) = disksCount
        val difference = (abs(blackDisks-whiteDisks)/64.0) * 3
        when {
            blackDisks > whiteDisks -> current.fitness.getAndUpdate{it+3+difference}
            blackDisks < whiteDisks -> opponent.fitness.getAndUpdate{it+3+difference}
            else -> {
                current.fitness.getAndUpdate{it+1}
                opponent.fitness.getAndUpdate{it+1}
            }
        }
        logGame(current, opponent, disksCount, game.boardManager.toString())
}

/* if first generation -> create new generation
   else if abs(new variance - avg variance) < 5% of avg variance -> don't create new generation
   otherwise create new generation
* */
fun produceNextGeneration(variances: ArrayList<Double>, genes: ArrayList<Gene>?): Boolean {
    if (genes == null) return true

    val newVariance = variance(genes)
    val avg = variances.takeLast(VARIANCE_CHECK).sum() / VARIANCE_CHECK
    val threshold = avg * VARIANCE_THRESHOLD

    variances.add(newVariance)
    logVariance(newVariance,avg)

    return variances.size < MIN_GENERATION_PRODUCTION || abs(avg-newVariance) > threshold
}

fun selectNextGeneration(genes: ArrayList<Gene>?): ArrayList<Gene> {
    return if (genes == null) {
        val generation = initialGeneration(GENERATION_SIZE)
        generation.addAll(generateChildren(generation))
        generation
    }else {
        val rewardSelection = rewardSelection(genes)
        rewardSelection.addAll(generateChildren(rewardSelection))
        rewardSelection
    } // produce from lastGeneration
}

// select survivors from last generation using reward selection
fun rewardSelection(genes: ArrayList<Gene>):ArrayList<Gene> {
    val selection = ArrayList<Gene>()
    val sorted = genes.sortedBy {it.fitness.get() }

    logRewardSelection(sorted)

    val sum = (sorted.size * (sorted.size+1))/2.0

    while (selection.size != GENERATION_SIZE) {
        val rand = randNumber()
        for (i in sorted.size downTo 1) {
            val prob = (i*(i-1))/(2*sum)
            if (rand > prob){
                if (genes[i-1].selected) break
                genes[i-1].selected = true
                selection.add(genes[i-1])
                break
            }
        }
    }

    selection.forEach {
        it.selected = false
        it.fitness.set(0.0)
    }

    return selection
}

fun separateWeights(gene: Gene): Pair<DoubleArray,DoubleArray> {
    val featuresWeights = gene.weights.sliceArray(0 until 6)
    val reducerWeights = gene.weights.sliceArray(6 until 12)
    return Pair(featuresWeights,reducerWeights)
}

fun variance(genes: ArrayList<Gene>): Double {
    val sum_x2 = genes.stream().mapToDouble { it.fitness.get() }.reduce{ acc, fit -> acc +fit*fit }.asDouble
    var sumx_2 = genes.stream().mapToDouble { it.fitness.get() }.reduce{ acc, fit -> acc +fit }.asDouble
    sumx_2 *= sumx_2


    return (sum_x2 - (sumx_2/genes.size))/(genes.size-1)
}