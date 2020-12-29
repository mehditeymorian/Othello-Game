package train

var geneId = 0

fun initialGeneration(size: Int): ArrayList<Gene> {
    TODO()
}

fun avgGene(genes: ArrayList<Gene>): Gene {
    TODO()
}

/* select parent randomly
*  set true for genes after crossover in applied array*/
fun generateChildren(genes: ArrayList<Gene>): ArrayList<Gene> {
    // crossover is applied for gene or not
    val children = ArrayList<Gene>()
    val crossoverApplied = BooleanArray(genes.size)


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