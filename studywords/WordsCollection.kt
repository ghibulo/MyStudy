package studywords

import java.io.File
import java.time.Instant

data class Word (var question: String = "", 
                 var answer: String = "", 
                 var pronunciation: String = "", 
                 var rating: Int = 0, 
                 var timeStamp: Long = 0,
                 var futureTimeStamp: Long = 0): Comparable<Word> {

    override fun compareTo(other: Word) = when {
        //rating != other.rating -> rating - other.rating
        futureTimeStamp > other.futureTimeStamp -> 1
        futureTimeStamp < other.futureTimeStamp -> -1
        else -> 0
	}



}


open class TrainWordsCollection {

    var data: MutableList<Word> = mutableListOf<Word>()

    val ratingTimeExtension: DoubleArray = doubleArrayOf(1.4, 1.2, 1.0, 0.8, 0.6)
    val ratingStartTime: LongArray = longArrayOf(5*24*3600, 3*24*3600, 24*3600, 12*3600, 4*3600)
    //number of seconds after which the same word can be used
    val minDuration: Int = 2*60
    var index : Int = -1


    fun add(word: Word) {
        data.add(word)
        data = data.sorted().toMutableList()
    }

    fun showAll() {
        data = data.sorted().toMutableList()
        data.forEachIndexed { i, line -> println("${i}: " + line) }
    }

    fun getAtIndex(i: Int): Word {
        return data[i]
    }


    open fun evaluateWord(which: Word, nextRating: Int) {

        val nowStamp: Long = Instant.now().getEpochSecond()
        //println ("nowStamp = ${nowStamp}")
        //println ("which... ${which}")
        //println ("which.timeStamp... ${which.timeStamp}")
        val duration: Long = if (nowStamp>which.futureTimeStamp)  (nowStamp - which.timeStamp) else (which.futureTimeStamp - which.timeStamp)

        //println ("duration = ${duration}")
        which.rating = nextRating
        //println ("which.rating = ${which.rating}")

        if (which.timeStamp.equals(0L)) {

            //println ("which.timeStamp equals 0, so it's set to ${nowStamp}")
            which.timeStamp =  nowStamp
            which.futureTimeStamp = which.timeStamp + ratingStartTime[nextRating-1]

            //println ("which.futureTimeStamp set to ${which.futureTimeStamp}")
        } else {

            //println ("which.timeStamp is not 0, so it's set to ${nowStamp}")
            which.timeStamp = nowStamp

            //println ("duration is multiply by coef ${ratingTimeExtension[nextRating-1]}")
            which.futureTimeStamp = (nowStamp + duration*ratingTimeExtension[nextRating-1]).toLong()
        }
    }

    //simple go through all the items
    open fun getWord(): Word? {
        if (data.lastIndex == -1) return null
        if (++index > data.lastIndex) index = 0
        if (index == 0) data = data.sorted().toMutableList()
        return data[index]
    }

    fun deleteCurrentWord() {
        if ((data.lastIndex >= index) && (index >=0)) {
            data.removeAt(index)
        }
    }

}


class WordsCollection(val csvFile: String, val importFile: String? = null) : TrainWordsCollection() {

    val maxNewRepeatedWords = 10
    var newRepeatedWords = 0
    var trainWords : TrainWordsCollection = TrainWordsCollection() 

    init {
        loadFromCsv(csvFile)
        if (importFile != null) {
            importFileListWords(importFile)
        }
    }

    class BufferItems {
        private var items: Word = Word()
        private var ind:Int = 0
        fun saveItem(item: String): Word? {
            //if (Global.DEBUG) {
                println ("called saveItem... items = ${items}")
            //}
            if (item.substring(0..2) == "---") {
                val result: Word? = items
                if (ind>0) {
                    ind = 0
                    items = Word()
                } 
                return result
            } else {
                when (ind++) {
                    0 -> items.question = item
                    1 -> items.answer = item
                    2 -> items.pronunciation = item
                    else -> print("Error in the data file!")
                }
            }
            return null
        }//fun
    }




    fun importFileListWords(fileName: String)  {
        var buffer: BufferItems = BufferItems()
        File(fileName).useLines {lines -> 
                                    lines.forEach { line ->
                                                        val tempLine: Word? = buffer.saveItem(line)
                                                        if (tempLine != null) data.add(tempLine) 
                                    }
                                 }
        data = data.sorted().toMutableList()
    }

    override fun getWord(): Word? {
        data = data.sorted().toMutableList()
        val nowStamp: Long = Instant.now().getEpochSecond()
        var i:Int = 0
        while (i < data.size) {
            if ( (newRepeatedWords > maxNewRepeatedWords) && (data[i].timeStamp.equals(0L)) ) {i++;continue}
            if ((nowStamp - data[i].timeStamp) > minDuration) break
            i++
        }
        println ("We test the ${i}.word from the end")
        if (data.size <= i) i = data.size-1
        if (data[i].timeStamp.equals(0L)) newRepeatedWords++
        return data[i]
    }


    override fun evaluateWord(which: Word, nextRating: Int) {
        super.evaluateWord(which, nextRating)
        if (nextRating > 3) {
            trainWords.data.add(which.copy())
        }
    }



    fun loadFromCsv(fileName: String ) {

        data = mutableListOf<Word>()

        File(fileName).useLines {lines -> 
                                    lines.forEach { line ->
                                                        val item: List<String> = line.split("\",\"")
                                                        //println (item)
                                                        if (item.size != 6) {println ("error during read csv - not 5 items!")}
                                                        data.add(Word(item[0].drop(1), item[1], item[2], 
                                                        item[3].toInt(), item[4].toLong(), item[5].dropLast(1).toLong()))
                                    }
                                 }
        data = data.sorted().toMutableList()
    }


    fun saveIntoCsv(fileName: String) {
        File(fileName).bufferedWriter().use { out -> 
            for (word in data) {
                out.write("\"" + word.question + "\"," +
                          "\"" + word.answer + "\"," +
                          "\"" + word.pronunciation + "\"," +
                          "\"" + word.rating + "\"," +
                          "\"" + word.timeStamp + "\"," +
                          "\"" + word.futureTimeStamp + "\"\n"
 
                         )
            }
        }
    }


   
}
