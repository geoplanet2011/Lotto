package ge.gogichaishvili.lotto.main.helpers

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.main.models.LottoCardModel


object LottoCardManager {

    //create lists
    private var oneList = (1..9).toMutableList()
    private var tenList = (10..19).toMutableList()
    private var twentyList = (20..29).toMutableList()
    private var thirtyList = (30..39).toMutableList()
    private var fortyList = (40..49).toMutableList()
    private var fiftyList = (50..59).toMutableList()
    private var sixtyList = (60..69).toMutableList()
    private var seventyList = (70..79).toMutableList()
    private var eightyList = (80..90).toMutableList()

    private val randomNumberList: MutableList<Int> = ArrayList() //random number list for one line

    private val indexList = (0..8).toMutableList()
    private val deleteIndexList: MutableList<Int> = ArrayList() //index list for delete numbers

    private val fullTicketNumberList: MutableList<Int> = ArrayList()

    private var isDraw = false

    private var cardModel = LottoCardModel(false, fullTicketNumberList)

    fun generateCard(context: Context, linearLayout: LinearLayout) : LottoCardModel {

      isDraw = false

        //------------------------------------------- create table programmatically
        val table = TableLayout(context)
        table.layoutParams =
            TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        table.isShrinkAllColumns = true
        table.isStretchAllColumns = true
        table.setPadding(15, 15, 15, 15)


        for (i in 1..3) {
            val row = TableRow(context)

            //shuffle lists
            oneList.shuffle()
            tenList.shuffle()
            twentyList.shuffle()
            thirtyList.shuffle()
            fortyList.shuffle()
            fiftyList.shuffle()
            sixtyList.shuffle()
            seventyList.shuffle()
            eightyList.shuffle()

            //get and remove random numbers from lists
            val one = oneList.random()
            oneList.remove(one)

            val ten = tenList.random()
            tenList.remove(ten)

            val twenty = twentyList.random()
            twentyList.remove(twenty)

            val thirty = thirtyList.random()
            thirtyList.remove(thirty)

            val forty = fortyList.random()
            fortyList.remove(forty)

            val fifty = fiftyList.random()
            fiftyList.remove(fifty)

            val sixty = sixtyList.random()
            sixtyList.remove(sixty)

            val seventy = seventyList.random()
            seventyList.remove(seventy)

            val eighty = eightyList.random()
            eightyList.remove(eighty)

            //create one ine number list
            randomNumberList.clear()
            randomNumberList.add(one)
            randomNumberList.add(ten)
            randomNumberList.add(twenty)
            randomNumberList.add(thirty)
            randomNumberList.add(forty)
            randomNumberList.add(fifty)
            randomNumberList.add(sixty)
            randomNumberList.add(seventy)
            randomNumberList.add(eighty)

            //delete 4 numbers from 9 (update number to 0 )
            for (index in 1..4) {
                indexList.shuffle()
                val result = indexList.random()

                deleteIndexList.add(result)
                indexList.remove(result)

                randomNumberList[result] = 0
            }

            deleteIndexList.clear()
            indexList.clear()
            indexList.add(0)
            indexList.add(1)
            indexList.add(2)
            indexList.add(3)
            indexList.add(4)
            indexList.add(5)
            indexList.add(6)
            indexList.add(7)
            indexList.add(8)
            indexList.shuffle()

            fullTicketNumberList.addAll(randomNumberList) //create full ticket

            for (j in 1..9) {

                val value: Int = randomNumberList[j - 1]


                val tv = TextView(context)
                tv.gravity = Gravity.CENTER

                if (value != 0) {
                    tv.text = value.toString()
                }

                tv.textSize = 28f
                tv.setTextColor(Color.BLACK)

                if (j == 1) {
                    /*val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(10, 0, 10, 0)
                    tv.layoutParams = params*/
                    tv.setPadding(18, 0, 18, 0);
                }


                //tv.setTypeface(null, Typeface.BOLD)

                val border = ShapeDrawable(RectShape())
                border.paint.style = Paint.Style.STROKE
                border.paint.color = Color.BLACK
                tv.background = border
                tv.isClickable = true

                val iv = ImageView(context)

                val frameLayout = FrameLayout(context)

                frameLayout.addView(tv)
                frameLayout.addView(iv)

                tv.setOnClickListener {

                    Toast.makeText(context, tv.text, Toast.LENGTH_SHORT).show()
                    iv.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
                }

                row.addView(frameLayout)

            }
            table.addView(row)
        }

        //check empty values
        if (
            ((fullTicketNumberList[0] + fullTicketNumberList[9] + fullTicketNumberList[18]) == 0) ||
            ((fullTicketNumberList[1] + fullTicketNumberList[10] + fullTicketNumberList[19]) == 0) ||
            ((fullTicketNumberList[2] + fullTicketNumberList[11] + fullTicketNumberList[20]) == 0) ||
            ((fullTicketNumberList[3] + fullTicketNumberList[12] + fullTicketNumberList[21]) == 0) ||
            ((fullTicketNumberList[4] + fullTicketNumberList[13] + fullTicketNumberList[22]) == 0) ||
            ((fullTicketNumberList[5] + fullTicketNumberList[14] + fullTicketNumberList[23]) == 0) ||
            ((fullTicketNumberList[6] + fullTicketNumberList[15] + fullTicketNumberList[24]) == 0) ||
            ((fullTicketNumberList[7] + fullTicketNumberList[16] + fullTicketNumberList[25]) == 0) ||
            ((fullTicketNumberList[8] + fullTicketNumberList[17] + fullTicketNumberList[26]) == 0)

        ) {
            isDraw = false

        } else {
            linearLayout.addView(table)
            isDraw = true
        }

        cardModel.isDraw = isDraw
        cardModel.fullTicketNumberList = fullTicketNumberList

        resetCard()

        return cardModel

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

        fullTicketNumberList.clear()
    }

}