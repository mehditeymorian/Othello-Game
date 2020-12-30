package train

var geneId = 0
var geneSize = 11

fun initialGeneration(size: Int): ArrayList<Gene> {

    val parentList: ArrayList<Gene> = ArrayList()

    for(i in 0..size){

        val weightArr = DoubleArray(geneSize){0.0}
        for (j in 0..weightArr.size){
            if (i % 2 == 0) {
                when (j) {
                    //Utility
                    0 -> weightArr[j] = randNumber(4, 8) //disksDifferenceFeature
                    1 -> weightArr[j] = randNumber(7, 12) //mobilityFeature
                    2 -> weightArr[j] = randNumber(4, 8) //potentialMobilityFeature
                    3 -> weightArr[j] = randNumber(12, 15) //cornerFeature
                    4 -> weightArr[j] = randNumber(2, 7) //parityFeature
                    5 -> weightArr[j] = randNumber(10, 15) //stableDisksFeature
                    //MoveReducer Features
                    6 -> weightArr[j] = randNumber(10, 15) //cornerFeature
                    7 -> weightArr[j] = randNumber(1, 8) //edgeFeature
                    8 -> weightArr[j] = randNumber(5, 10) //flipFeature
                    9 -> weightArr[j] = randNumber(4, 8) // mobilityFeature
                    10 -> weightArr[j] = randNumber(3, 7) //cornerNeighbors
                }
            }else if (i % 2 == 1){
                weightArr[j] = randNumber(0 , 15)
            }
        }
        parentList.add(Gene(geneId++ , 0 , weightArr ))
    }
    return parentList
}

fun avgGene(genes: ArrayList<Gene>): Gene {
    val avgWeight = DoubleArray(geneSize){0.0}

    for (j in 0..geneSize){
        avgWeight[j] = 0.0
        for (i in 0..genes.size){
            avgWeight[j] += genes[i].weights[j]
        }
        avgWeight[j] /= geneSize.toDouble()
    }
    return Gene( geneId++ , genes[0].generation , avgWeight)
}

/* select parent randomly
*  set true for genes after crossover in applied array*/
fun generateChildren(genes: ArrayList<Gene>): ArrayList<Gene> {
    // mutation is applied for gene or not
    // crossover will be applied always
    val children = ArrayList<Gene>()
    val copy = ArrayList(genes)

    while (copy.isNotEmpty()){
        var rnd = randNumber(copy.size)
        val gen1 = copy[rnd]
        copy.removeAt(rnd)

        rnd = randNumber(copy.size)
        val gen2 = copy[rnd]
        copy.removeAt(rnd)

        val child = applyCrossover(gen1 , gen2)

        if (randNumber() <= 0.7 ){
            applyMutation(child)
        }
        children.add(child)

    }
    return children
}

/* AVG gene: Ci = αiP1 + (1-αi)P2 , αi ϵ (0,1)

* */
fun applyCrossover(gene1: Gene, gene2: Gene): Gene {
    val ci = DoubleArray(geneSize){0.0}
    val a = randNumber()
    for (i in 0..geneSize){
        ci[i] = a* gene1.weights[i] + (1-a)* gene2.weights[i]
    }
    return Gene(geneId++ , gene1.generation+1 , ci )
}

/* add a positive or negative random value to every weight
   should consider min(1) or max(15)
* */
fun applyMutation(gene: Gene) {
    for(i in 0..geneSize){
        gene.weights[i] += gaussian()
        if ( gene.weights[i] > 15.0 )
            gene.weights[i]= 15.0
        else if ( gene.weights[i] < 0.0)
            gene.weights[i]= 0.0
    }
}