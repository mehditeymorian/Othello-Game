package train

import ai.*

var geneId = 0
var generationId = 0
const val GENE_SIZE = 12
const val MUTATION_PROB = 0.7

// create initial generation
fun initialGeneration(size: Int): ArrayList<Gene> {
    val generation = ArrayList<Gene>()
    repeat(size) {
        generation.add(if (it % 2 == 0) heuristicGene() else randomGene())
    }
    return generation
}

/* for all parents choose 2 random parent and do crossover
*  if probability is under 0.7 apply mutation to child too*/
fun generateChildren(genes: ArrayList<Gene>): ArrayList<Gene> {
    val children = ArrayList<Gene>()
    val copy = ArrayList(genes)

    while (copy.isNotEmpty()) {
        var index = randNumber(copy.size)
        val gene1 = copy.removeAt(index)

        index = randNumber(copy.size)
        val gen2 = copy.removeAt(index)

        val child = applyCrossover(gene1, gen2)

        applyMutation(child)

        children.add(child)

    }
    return children
}

/*  generate child for given parent using below formula
    refer to document for more information
    AVG gene: Ci = αiP1 + (1-αi)P2 , αi ϵ (0,1)
* */
fun applyCrossover(gene1: Gene, gene2: Gene): Gene {
    val a = randNumber()
    val ci = DoubleArray(GENE_SIZE) { (a * gene1.weights[it]) + ((1 - a) * gene2.weights[it]) }
    return Gene(geneId++, generationId, ci)
}

/* add a positive or negative random value to every weight
   should consider min(0.1) or max(15)
* */
fun applyMutation(gene: Gene) {
    val rand = randNumber()
    if (rand > MUTATION_PROB) return
    for (i in gene.weights.indices) {
        gene.weights[i] += gaussian()
        if (gene.weights[i] > 15.0) gene.weights[i] = 15.0
        else if (gene.weights[i] < 0.0) gene.weights[i] = 0.1
    }
}

// create a gene with respect to the defined heuristic period for each feature
fun heuristicGene(): Gene {
    val weights = DoubleArray(GENE_SIZE) {
        when (it) {
            //Utility
            FEATURE_DISK_DIFFERENCE -> randNumber(4, 8) //disksDifferenceFeature
            FEATURE_OPPONENT_MOBILITY -> randNumber(7, 12) //mobilityFeature
            FEATURE_POTENTIAL_MOBILITY -> randNumber(4, 8) //potentialMobilityFeature
            FEATURE_CORNER -> randNumber(12, 15) //cornerFeature
            FEATURE_PARITY -> randNumber(2, 7) //parityFeature
            FEATURE_STABLE_DISKS -> randNumber(10, 15) //stableDisksFeature
            //MoveReducer Features
            FEATURES_COUNT + REDUCER_CORNER -> randNumber(10, 15) //cornerFeature
            FEATURES_COUNT + REDUCER_PLAYING_MIDDLE -> randNumber(1, 8) //edgeFeature
            FEATURES_COUNT + REDUCER_FLIP -> randNumber(5, 10) //flipFeature
            FEATURES_COUNT + REDUCER_GIVING_CORNER -> randNumber(8, 13) // giving corner
            FEATURES_COUNT + REDUCER_OPPONENT_MOBILITY -> randNumber(4, 8) // mobilityFeature
            FEATURES_COUNT + REDUCER_CORNER_NEIGHBOR -> randNumber(3, 7) //cornerNeighbors
            else -> 0.0
        }
    }
    return Gene(geneId++, generationId, weights)
}

// create a totally random gene
fun randomGene(): Gene {
    val weights = DoubleArray(GENE_SIZE) { randNumber(1, 15) }
    return Gene(geneId++, generationId, weights)
}