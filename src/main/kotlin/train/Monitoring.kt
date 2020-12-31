package train

import java.io.File
import java.lang.StringBuilder

const val BASE_ADDRESS = "./generations/"


fun initLogger() {
    val generationFolder = File(BASE_ADDRESS)
    if (generationFolder.exists().not()) generationFolder.mkdir()
}

fun logGeneration(genes: ArrayList<Gene>) {
    repeat(genes.size) {
        log("!generation","${genes[it]}\n")
    }
}

fun logRewardSelection(genes: List<Gene>) {
    repeat(genes.size) {
        log("!rewardSelection","F=${genes[it].fitness} ${genes[it]}\n")
    }
}

fun logGame(gene1: Gene, gene2: Gene, disks: Pair<Int, Int>) {
    val result = StringBuilder()
    result.append("Black $gene1\n")
    result.append("White $gene2\n")
    result.append("Black: ${disks.first} - White: ${disks.second}\n")
    result.append("${"=".repeat(40)}\n")
    log("Game ${gene1.id} - ${gene2.id}",result.toString())
}

private fun log(fileName: String, line: String) {
    synchronized(fileName){
        val folder = File("$BASE_ADDRESS$generationId/")
        if (folder.exists().not()) folder.mkdir()
        val file = File("$BASE_ADDRESS$generationId/$fileName.txt")
        if (file.exists().not()) file.createNewFile()

        file.appendText(line)
    }

}