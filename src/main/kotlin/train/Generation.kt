package train

var geneId = 0
var geneSize = 13
fun initialGeneration(size: Int): ArrayList<Gene> {

    val parentList: ArrayList<Gene> = ArrayList()

    for(i in 0..size){

        val weightArr = DoubleArray(geneSize){0.0}
        randArray(weightArr , geneSize/2 )

        for (j in 0..weightArr.size){
            if ( weightArr[j]==null){
                when(j){
                    //MoveReducer Features
                    0 -> weightArr[j] = randNumber(10 , 15) //cornerFeature
                    1 -> weightArr[j] = randNumber(1 , 8) //edgeFeature
                    2 -> weightArr[j] = randNumber(5 , 10) //flipFeature
                    3 -> weightArr[j] = randNumber(8 , 13) //givingCornerFeature
                    4 -> weightArr[j] = randNumber(4 , 8) // mobilityFeature
                    5 -> weightArr[j] = randNumber(3 , 7) //dangerCellsFeature
                    6 -> weightArr[j] = randNumber(3 , 7) //wedgingFeature
                    //Utility
                    7 -> weightArr[j] = randNumber(4 , 8) //disksDifferenceFeature
                    8 -> weightArr[j] = randNumber(7 , 12) //mobilityFeature
                    9 -> weightArr[j] = randNumber(4 , 8) //potentialMobilityFeature
                    10 -> weightArr[j] = randNumber(12 , 15) //cornerFeature
                    11 -> weightArr[j] = randNumber(2 , 7) //parityFeature
                    12 -> weightArr[j] = randNumber(10 , 15) //stableDisksFeature

                }
            }
        }
        parentList.add(Gene(geneId++ , 0 , weightArr!! ))
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
    return Gene(-1 , genes[0].generation , avgWeight)
}

/* select parent randomly
*  set true for genes after crossover in applied array*/
fun generateChildren(genes: ArrayList<Gene>): ArrayList<Gene> {
    // crossover is applied for gene or not
    val children = ArrayList<Gene>()
    val copy = ArrayList(genes)

    TODO()
}

/* AVG gene: Ci = αiP1 + (1-αi)P2 , αi ϵ (0,1)

* */
fun applyCrossover(gene1: Gene, gene2: Gene): Gene {
    TODO()
}

/* add a positive or negative random value to every weight
   should consider min(1) or max(15)
* */
fun applyMutation(gene: Gene) {
}