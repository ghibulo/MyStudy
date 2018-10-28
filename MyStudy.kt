//compile... kotlinc  MyStudy.kt -include-runtime studywords/WordsCollection.kt -d MyStudy.jar

import java.io.File
import java.time.Instant
import studywords.WordsCollection
import studywords.Word

object Global {
    const val DEBUG: Boolean = false
}



fun getFileListLines(fileName: String): MutableList<String>  {
    val result: MutableList<String>  = mutableListOf<String>()
    File(fileName).useLines {lines -> result.addAll(lines) }
    return result
}

fun showQuestion(which: Word):Int {
    println (which.question)
    readLine()
    println (which.answer)
    println (which.pronunciation)
    var answerRating: Int? = null
    while (answerRating == null) {
        try {
            answerRating = readLine()?.toInt()
        } catch (e: Exception ) {
            println ("Sory, try to input rating in Int form 1-> easy, 5->wrong")
            answerRating = null
        }
    }

    //val result:Word = which.copy(rating=answerRating,  timeStamp = Instant.now().getEpochSecond())
    return answerRating
}




fun main(args: Array<String>) {
    println("Hello, World!")
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

    val a: WordsCollection = WordsCollection()
    a.loadFromCsv("data.csv")
    a.showAll()

    for (i in 0..5) {
        var test: Word = a.getWord()
        a.evaluateWord(test, showQuestion(test))
        a.showAll()
    }
    a.saveIntoCsv("data.csv")


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
