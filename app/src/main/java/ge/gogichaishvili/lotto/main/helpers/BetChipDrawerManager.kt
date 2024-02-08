package ge.gogichaishvili.lotto.main.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.widget.ImageView
import android.widget.LinearLayout
import ge.gogichaishvili.lotto.R

class BetChipDrawerManager() {

    private var coins = arrayOf<IntArray>(
        intArrayOf(100, 50, 25, 10, 5, 1),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(
            R.drawable.chip100,
            R.drawable.chip50,
            R.drawable.chip25,
            R.drawable.chip10,
            R.drawable.chip5,
            R.drawable.chip1
        )
    )

    private var chipBitmap: Bitmap? = null
    private var canvas: Canvas? = null

    fun drawChips(layout: LinearLayout, context: Context, bet: Int) {
        var value: Int
        var sum: Int = bet
        value = 0
        layout.removeAllViews()
        var cnt = 0
        for (i in coins[0].indices) {
            value = sum / coins[0][i]
            coins[1][i] = value
            sum -= coins[0][i] * value
            if (value > 0) {
                cnt += 1
            }
        }
        val w = layout.width
        val h = layout.height
        chipBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(chipBitmap!!)
        var top = (h - 87) / 2
        var left = 0
        left = w / 2 + 87 * cnt / 2
        cnt = 0
        for (i in coins[0].indices) {

            if (coins[1][i] > 0) {
                cnt += 1
                left -= 90
                top = (h - 87) / 2
            }
            for (j in 1 until coins[1][i] + 1) {
                paintChips(context, coins[2][i], left, top)
                top -= 10
            }
        }
        paintBitmap(layout, context, w, h)
    }

    private fun paintChips(
        context: Context,
        coin: Int,
        left: Int,
        top: Int
    ) {
        var cardBitmap = BitmapFactory.decodeResource(context.resources, coin)
        cardBitmap = Bitmap.createScaledBitmap(cardBitmap!!, 90, 100, false)
        canvas!!.drawBitmap(cardBitmap, left.toFloat(), top.toFloat(), null)
    }

    private fun paintBitmap(layout: LinearLayout, context: Context, w: Int, h: Int) {
        val imageView = ImageView(context)
        imageView.setImageBitmap(chipBitmap)
        val layoutParams = LinearLayout.LayoutParams(w, h)
        layoutParams.setMargins(0, 0, 0, 0)
        imageView.layoutParams = layoutParams
        layout.addView(imageView)
    }

}
