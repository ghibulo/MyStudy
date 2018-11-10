//compile... kotlinc  MyStudy.kt -include-runtime studywords/WordsCollection.kt -d MyStudy.jar

import java.io.File
import java.time.Instant
import studywords.WordsCollection
import studywords.Word

object Global {
    const val DEBUG: Boolean = false
}

data class Parameters (val csvFile: String, val importFile: String? )


fun getParameters(args: Array<String>): Parameters {
    var csvFile: String = "data.csv"
    var importFile: String? = null
    if (args.size == 0) return Parameters(csvFile, importFile)

    for (index in 0 until args.size step 2) {
        if (args[index].equals("-csv")) {
            csvFile = args[index+1]
            continue
        }
        if (args[index].equals("-imp")) {
            importFile = args[index+1]
            continue
        }
        
    }
    return Parameters(csvFile, importFile)
}


fun getFileListLines(fileName: String): MutableList<String>  {
    val result: MutableList<String>  = mutableListOf<String>()
    File(fileName).useLines {lines -> result.addAll(lines) }
    return result
}

fun showQuestion(which: Word, leftRating: Int = 1, rightRating: Int = 5):Int? {
    println (which.question)
    readLine()
    println (which.answer)
    println (which.pronunciation)
    var answerRating: Int? = null
    var inputString: String?
    while (answerRating == null) {
        try {
            inputString = readLine()
            if (inputString.equals("---")||inputString.equals("===")) {
                return null
            }
            answerRating = inputString!!.toInt()
            if ((answerRating < leftRating) || (answerRating > rightRating)) answerRating = null
        } catch (e: Exception ) {
            if (leftRating == 1) {
                println ("Sory, try to input rating in Int form 1-> easy, 5->wrong")
            } else {
                println ("Sory, try to input rating in Int form 1-> easy, 5->wrong or 0->remove")
            }
            answerRating = null
        }
    }

    //val result:Word = which.copy(rating=answerRating,  timeStamp = Instant.now().getEpochSecond())
    return answerRating
}






fun main(args: Array<String>) {
    val parameters: Parameters = getParameters(args)

    val words: WordsCollection = WordsCollection(parameters.csvFile, parameters.importFile)

    while(true) {
        var test: Word = words.getWord()!!
        val newRating: Int? = showQuestion(test)
        if (newRating == null) {
            break
        }
        words.evaluateWord(test,  newRating)
    }
    //rewise of words with rating > 3
    while(true) {
        var test: Word? = words.trainWords.getWord()
        if (test == null) break
        val newRating: Int? = showQuestion(test, leftRating = 0) //0 -> removing from training
        if (newRating == null) {
            break
        }
        if (newRating == 0) {
            words.trainWords.deleteCurrentWord()
        } else {
            words.trainWords.evaluateWord(test,  newRating)
        }
    }
    words.saveIntoCsv(parameters.csvFile)


}
