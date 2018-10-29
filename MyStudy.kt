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

fun showQuestion(which: Word):Int? {
    println (which.question)
    readLine()
    println (which.answer)
    println (which.pronunciation)
    var answerRating: Int? = null
    var inputString: String?
    while (answerRating == null) {
        try {
            inputString = readLine()
            if (inputString.equals("---")) {
                return null
            }
            answerRating = inputString!!.toInt()
        } catch (e: Exception ) {
            println ("Sory, try to input rating in Int form 1-> easy, 5->wrong")
            answerRating = null
        }
    }

    //val result:Word = which.copy(rating=answerRating,  timeStamp = Instant.now().getEpochSecond())
    return answerRating
}






fun main(args: Array<String>) {
    /*
    val a: WordsCollection = WordsCollection()
    a.add(Word("ahoj", "hello", "helou",2,10))
    a.add(Word("stůl", "table", "tejbl",2,20))
    a.showAll()
    a.importFileListWords("slovicka.txt")
    a.showAll()
    var w: Word = a.getAtIndex(8)
    println (w)
    w.futureTimeStamp +=45
    a.showAll()
*/
    val parameters: Parameters = getParameters(args)

    val words: WordsCollection = WordsCollection()
    words.loadFromCsv(parameters.csvFile)
    if (parameters.importFile != null) {
        words.importFileListWords(parameters.importFile)
    }
    words.showAll()

    //a.saveIntoCsv("data3.csv")
    while(true) {
        var test: Word = words.getWord()
        val newRating: Int? = showQuestion(test)
        if (newRating == null) {
            break
        }
        words.evaluateWord(test,  newRating)
        //a.showAll()
    }
    words.saveIntoCsv(parameters.csvFile)

    /*

    val myList =  getFileListLines("slovicka.txt")
    myList.forEachIndexed { i, line -> println("${i}: " + line) }
    val myWords =  getFileListWords("slovicka.txt")
    myWords.forEachIndexed { i, line -> println("${i}: " + line) }
    println ("export...")
    saveIntoCsv("data.csv", myWords)
    var loadedList = loadFromCsv("data.csv")
    loadedList.forEachIndexed { i, line -> println("${i}: " + line) }
    loadedList[0] =  showQuestion(loadedList[0])
    loadedList[1] =  showQuestion(loadedList[1])
    saveIntoCsv("data2.csv", loadedList)
    */

}
