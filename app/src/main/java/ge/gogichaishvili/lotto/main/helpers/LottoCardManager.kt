package ge.gogichaishvili.lotto.main.helpers

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.SingleLiveEvent
import ge.gogichaishvili.lotto.app.tools.Utils
import ge.gogichaishvili.lotto.main.models.LottoCardModel
import ge.gogichaishvili.lotto.main.models.LottoDrawResult

object LottoCardManager {

    private var oneList = (1..9).toMutableList()
    private var tenList = (10..19).toMutableList()
    private var twentyList = (20..29).toMutableList()
    private var thirtyList = (30..39).toMutableList()
    private var fortyList = (40..49).toMutableList()
    private var fiftyList = (50..59).toMutableList()
    private var sixtyList = (60..69).toMutableList()
    private var seventyList = (70..79).toMutableList()
    private var eightyList = (80..90).toMutableList()

    private var randomNumberList: MutableList<Int> = ArrayList() //random number list for one line

    private var indexList = (0..8).toMutableList()
    private var deleteIndexList: MutableList<Int> = ArrayList() //index list for delete numbers

    private val fullTicketNumberList: MutableList<Int> = ArrayList()

    private var clickedNumberList: MutableList<Int> = ArrayList()  //choose lotto numbers in cards

    var allTickets: MutableList<Int> = ArrayList() //all ticket

    private var cardModel = LottoCardModel(fullTicketNumberList)

    private var counter: Int = 0

    private var onLineCompleteListener: (() -> Unit)? = null
    private var onCardCompleteListener: (() -> Unit)? = null

    fun setOnLineCompleteListener(listener: () -> Unit) {
        onLineCompleteListener = listener
    }

    fun setOnCardCompleteListener(listener: () -> Unit) {
        onCardCompleteListener = listener
    }

    fun generateCard(
        context: Context,
        linearLayout: LinearLayout,
        lottoStones: SingleLiveEvent<LottoDrawResult>?
    ): LottoCardModel {
        while (counter < 3) {
            val table = createTable(context)
            for (i in 1..3) {
                val row = createRow(context, lottoStones)
                table.addView(row)
            }
            if (isCardValid()) {
                linearLayout.addView(table)
                allTickets.addAll(fullTicketNumberList)
                counter++
                println("card  created successfully!")
            } else {
                println("card not created")
            }
            cardModel.fullTicketNumberList = fullTicketNumberList
            resetCard()
        }
        return cardModel
    }

    private fun createTable(context: Context): TableLayout {
        val table = TableLayout(context)
        val params = TableLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 20, 20, 20)
        table.layoutParams = params
        table.isShrinkAllColumns = true
        table.isStretchAllColumns = true
        val tableBorder = ShapeDrawable(RectShape())
        tableBorder.paint.style = Paint.Style.STROKE
        tableBorder.paint.strokeWidth = 5f
        tableBorder.paint.color = Color.BLACK
        table.background = tableBorder
        return table
    }

    private fun createRow(
        context: Context,
        lottoStones: SingleLiveEvent<LottoDrawResult>?
    ): TableRow {
        val row = TableRow(context)
        shuffleLists()
        val randomNumberList = createRandomNumberList()
        deleteNumbersFromList(randomNumberList)
        resetIndexList()
        fullTicketNumberList.addAll(randomNumberList) // Create full ticket
        for (j in 1..9) {
            val value: Int = randomNumberList[j - 1]
            val tv = TextView(context)
            tv.gravity = Gravity.CENTER
            if (value != 0) {
                tv.text = value.toString()
                tv.isClickable = true
            }
            tv.textSize = 24f
            tv.setTextColor(Color.BLACK)
            val border = ShapeDrawable(RectShape())
            border.paint.style = Paint.Style.STROKE
            border.paint.strokeWidth = 2f
            border.paint.color = Color.BLACK
            tv.background = border
            val iv = ImageView(context)
            val frameLayout = FrameLayout(context)
            frameLayout.addView(tv)
            frameLayout.addView(iv)
            val defaultLayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            defaultLayoutParams.setMargins(10, 10, 10, 10)
            defaultLayoutParams.gravity = Gravity.CENTER
            iv.layoutParams = defaultLayoutParams

            tv.setOnClickListener {
                if (tv.text.isNotEmpty() || tv.text.isNotBlank()) {
                    Utils.playAudio(context, R.raw.lotto)
                    if (lottoStones != null) {
                        if (tv.text.toString()
                                .toInt() in lottoStones.value!!.numbers
                        ) {

                            /* iv.layoutParams = FrameLayout.LayoutParams(
                                 tv.width - 10,
                                 tv.height - 10
                             ).apply { gravity = Gravity.CENTER }*/

                            iv.setBackgroundResource(R.drawable.chip)

                            tv.isClickable = false
                        }
                    }
                }

            }
            if (tv.text.isEmpty()) {
                tv.isClickable = false
            }
            row.addView(frameLayout)
            val params: ViewGroup.LayoutParams = frameLayout.layoutParams
            params.width = 100
            params.height = 100
            frameLayout.layoutParams = params
        }
        return row
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
    }

    fun setHints(numbers: List<Int>?, viewGroup: ViewGroup) {
        val parentLayout: ViewGroup = viewGroup
        for (i in 0 until parentLayout.childCount) {
            val childView: View = parentLayout.getChildAt(i)
            if (childView is TableLayout) {
                for (j in 0 until childView.childCount) {
                    val tableRow: View = childView.getChildAt(j)
                    if (tableRow is TableRow) {
                        for (k in 0 until tableRow.childCount) {
                            val frameLayout: View = tableRow.getChildAt(k)
                            if (frameLayout is FrameLayout) {
                                for (m in 0 until frameLayout.childCount) {
                                    val textView: View = frameLayout.getChildAt(m)
                                    if (textView is TextView) {
                                        val text = textView.text.toString()
                                        val textNumber = text.toIntOrNull()
                                        if (numbers != null) {
                                            if (textNumber != null && textNumber in numbers) {

                                                if (frameLayout.getChildAt(m + 1) is ImageView) {
                                                    val imageView =
                                                        frameLayout.getChildAt(m + 1) as ImageView

                                                    if (textView.isClickable) {
                                                        imageView.setBackgroundResource(R.drawable.light)
                                                    }
                                                }

                                            } else {
                                                if (frameLayout.getChildAt(m + 1) is ImageView) {
                                                    val imageView =
                                                        frameLayout.getChildAt(m + 1) as ImageView
                                                    if (textView.isClickable) {
                                                        imageView.background = null
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}

