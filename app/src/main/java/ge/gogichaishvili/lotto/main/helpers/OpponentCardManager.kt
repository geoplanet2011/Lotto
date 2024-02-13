package ge.gogichaishvili.lotto.main.helpers

object OpponentCardManager {

    private var oneList = (1..9).toMutableList()
    private var tenList = (10..19).toMutableList()
    private var twentyList = (20..29).toMutableList()
    private var thirtyList = (30..39).toMutableList()
    private var fortyList = (40..49).toMutableList()
    private var fiftyList = (50..59).toMutableList()
    private var sixtyList = (60..69).toMutableList()
    private var seventyList = (70..79).toMutableList()
    private var eightyList = (80..90).toMutableList()

    private var randomNumberList: MutableList<Int> = ArrayList()

    private var indexList = (0..8).toMutableList()
    private var deleteIndexList: MutableList<Int> = ArrayList()

    private val fullTicketNumberList: MutableList<Int> = ArrayList()

    private val opponentCards: MutableList<List<Int>> = ArrayList()

    private val drawnNumbers: MutableList<Int> = mutableListOf()

    private var isLineCompleted: Boolean = false
    private var isCardCompleted: Boolean = false

    private var onOpponentLineCompleteListener: (() -> Unit)? = null
    private var onOpponentCardCompleteListener: (() -> Unit)? = null

    fun setOnOpponentLineCompleteListener(listener: () -> Unit) {
        onOpponentLineCompleteListener = listener
    }

    fun setOnOpponentCardCompleteListener(listener: () -> Unit) {
        onOpponentCardCompleteListener = listener
    }

    private var counter: Int = 0

    fun generateBotCards() {
        while (counter < 3) {
            generateCard()
            if (isCardValid()) {
                counter++
                val newCard = ArrayList(fullTicketNumberList)
                opponentCards.add(newCard)
                printOpponentCards()
                println("Opponent card  created successfully!")
            } else {
                println("Opponent card not created")
            }
            resetCard()
        }

    }

    private fun generateCard() {
        for (i in 1..3) {
            shuffleLists()
            val randomNumberList = createRandomNumberList()
            deleteNumbersFromList(randomNumberList)
            resetIndexList()
            fullTicketNumberList.addAll(randomNumberList)
        }
    }

    private fun shuffleLists() {
        oneList.shuffle()
        tenList.shuffle()
        twentyList.shuffle()
        thirtyList.shuffle()
        fortyList.shuffle()
        fiftyList.shuffle()
        sixtyList.shuffle()
        seventyList.shuffle()
        eightyList.shuffle()
    }

    private fun getRandomNumberFromList(list: MutableList<Int>): Int {
        val number = list.random()
        list.remove(number)
        return number
    }

    private fun createRandomNumberList(): MutableList<Int> {
        val one = getRandomNumberFromList(oneList)
        val ten = getRandomNumberFromList(tenList)
        val twenty = getRandomNumberFromList(twentyList)
        val thirty = getRandomNumberFromList(thirtyList)
        val forty = getRandomNumberFromList(fortyList)
        val fifty = getRandomNumberFromList(fiftyList)
        val sixty = getRandomNumberFromList(sixtyList)
        val seventy = getRandomNumberFromList(seventyList)
        val eighty = getRandomNumberFromList(eightyList)
        return mutableListOf(one, ten, twenty, thirty, forty, fifty, sixty, seventy, eighty)
    }

    private fun deleteNumbersFromList(randomNumberList: MutableList<Int>) {
        for (index in 1..4) {
            indexList.shuffle()
            val result = indexList.random()
            deleteIndexList.add(result)
            indexList.remove(result)
            randomNumberList[result] = 0
        }
    }

    private fun resetIndexList() {
        deleteIndexList.clear()
        indexList.clear()
        indexList.addAll(0..8)
        indexList.shuffle()
    }

    private fun isCardValid(): Boolean {
        return !(((fullTicketNumberList[0] + fullTicketNumberList[9] + fullTicketNumberList[18]) == 0) ||
                ((fullTicketNumberList[1] + fullTicketNumberList[10] + fullTicketNumberList[19]) == 0) ||
                ((fullTicketNumberList[2] + fullTicketNumberList[11] + fullTicketNumberList[20]) == 0) ||
                ((fullTicketNumberList[3] + fullTicketNumberList[12] + fullTicketNumberList[21]) == 0) ||
                ((fullTicketNumberList[4] + fullTicketNumberList[13] + fullTicketNumberList[22]) == 0) ||
                ((fullTicketNumberList[5] + fullTicketNumberList[14] + fullTicketNumberList[23]) == 0) ||
                ((fullTicketNumberList[6] + fullTicketNumberList[15] + fullTicketNumberList[24]) == 0) ||
                ((fullTicketNumberList[7] + fullTicketNumberList[16] + fullTicketNumberList[25]) == 0) ||
                ((fullTicketNumberList[8] + fullTicketNumberList[17] + fullTicketNumberList[26]) == 0))
    }

    private fun resetCard() {
        oneList.clear()
        tenList.clear()
        twentyList.clear()
        thirtyList.clear()
        fortyList.clear()
        fiftyList.clear()
        sixtyList.clear()
        seventyList.clear()
        eightyList.clear()
        oneList = (1..9).toMutableList()
        tenList = (10..19).toMutableList()
        twentyList = (20..29).toMutableList()
        thirtyList = (30..39).toMutableList()
        fortyList = (40..49).toMutableList()
        fiftyList = (50..59).toMutableList()
        sixtyList = (60..69).toMutableList()
        seventyList = (70..79).toMutableList()
        eightyList = (80..90).toMutableList()
        indexList.clear()
        indexList = (0..8).toMutableList()
        deleteIndexList.clear()
        randomNumberList.clear()
        fullTicketNumberList.clear()
        //isLineCompleted = false
        //opponentCards.clear()
        //drawnNumbers.clear()
    }

    private fun printOpponentCards() {
        opponentCards.forEach { println(it) }
    }

    private fun checkOpponentCardsCompletion() {
        opponentCards.forEachIndexed { _, card ->
            val linesCompleted = card.chunked(9).count { row ->
                row.count { it != 0 && drawnNumbers.contains(it) } == row.count { it != 0 }
            }

            val cardCompleted = card.filter { it != 0 }.all { drawnNumbers.contains(it) }

            if (linesCompleted > 0) {
                if (!isLineCompleted) {
                    onOpponentLineCompleteListener?.invoke()
                    isLineCompleted = true
                }
            }

            if (cardCompleted) {
                if (!isCardCompleted) {
                    onOpponentCardCompleteListener?.invoke()
                    isCardCompleted = true
                    println("ბოტმა მოიგო ავოეეე!!!")
                }
            }

        }
    }

    fun checkOpponentGameCompletion(number: Int) {
        drawnNumbers.add(number)
        checkOpponentCardsCompletion()
        println(drawnNumbers.joinToString(", "))
    }

}