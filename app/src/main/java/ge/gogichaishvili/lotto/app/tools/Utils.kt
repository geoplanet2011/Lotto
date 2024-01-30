package ge.gogichaishvili.lotto.app.tools

import android.animation.Animator
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes

import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import ge.gogichaishvili.lotto.R

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.*



object Utils {

    //Generate random colors
    private fun generateColor(pos: Int, total: Int): IntArray {
        val increment = 360f / total
        val hue = increment * (pos + 0.5f)
        val colorLower = Color.HSVToColor(floatArrayOf(hue - hue * 0.05f, 100f, 10f))
        val colorHigher = Color.HSVToColor(floatArrayOf(hue + hue * 0.05f, 100f, 90f))
        return intArrayOf(colorLower, colorHigher)
    }

    //draw gradient
    fun getGradientDrawable(
        pos: Int,
        total: Int,
        mGradientDrawables: MutableMap<Int, GradientDrawable>
    ): GradientDrawable {
        var gd: GradientDrawable? = null
        gd = mGradientDrawables[pos]
        if (gd == null) {
            val colors = generateColor(pos, total)
            gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
            mGradientDrawables.put(pos, gd)
        }
        return gd
    }


    fun FullScreen(myActivity: Activity) {

        //Hide android bottom menu
        val v = myActivity.window.decorView
        v.systemUiVisibility = View.GONE

        //Hide clock
        val w = myActivity.window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //prevent go to sleep android
        val win = myActivity.window
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }


    //show progress dialog
    private var dialog: Dialog? = null

    fun showProgressDialog(context: Context, show: Boolean) {
        if (dialog == null) {
            val builder = AlertDialog.Builder(context)
            //builder.setView(R.layout.layout_loading_dialog)
            builder.setView(R.layout.progress_dalog)

            dialog = builder.create()

            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        if (show)
            dialog?.show()
        else {
            dialog?.dismiss()
            dialog = null
        }
    }


    //sound
    fun playAlertSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.alert)
        mediaPlayer?.start()
    }


    //play simple sound
    fun playAudio(context: Context, sound: Int) {
        val mediaPlayer = MediaPlayer.create(context, sound)
        mediaPlayer?.start()
    }


    //check valid password
    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password.toString())
        return matcher.matches()
    }


    //enable or disable viewGroup
    fun enableDisableViewGroup(viewGroup: ViewGroup, enabled: Boolean) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val view = viewGroup.getChildAt(i)
            view.isEnabled = enabled
            if (view is ViewGroup) {
                enableDisableViewGroup(view, enabled)
            }
        }
    }


    //Set current language old version
    fun setLocate(ContextWrapper: ContextWrapper, lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        ContextWrapper.baseContext.resources.updateConfiguration(
            config,
            ContextWrapper.baseContext.resources.displayMetrics
        )
    }

    //set language old version 2
    fun setLocale(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    // convert from bitmap to byte array
    @Throws(IOException::class)
    fun getBytes(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        stream.close()
        return stream.toByteArray()
    }

    // convert from byte array to bitmap
    fun getImage(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }


    //convert eng letters to geo
    fun convertEngToGeo(letters: String): String {

        val replaceMap = mapOf(
            "a" to "ა", "b" to "ბ", "c" to "ც", "d" to "დ", "e" to "ე",
            "f" to "ფ", "g" to "გ", "h" to "ჰ", "i" to "ი", "j" to "ჯ", "k" to "კ", "l" to "ლ",
            "m" to "მ", "n" to "ნ", "o" to "ო", "p" to "პ", "q" to "ქ", "r" to "რ", "s" to "ს",
            "t" to "ტ", "u" to "უ", "v" to "ვ", "w" to "წ", "x" to "ხ", "y" to "ყ", "z" to "ზ",
            "A" to "ა", "B" to "ბ", "C" to "ჩ", "D" to "დ", "E" to "ე", "F" to "ფ", "G" to "გ",
            "H" to "ჰ", "I" to "ი", "J" to "ჟ", "K" to "კ", "L" to "ლ", "M" to "მ", "N" to "ნ",
            "O" to "ო", "P" to "პ", "Q" to "ქ", "R" to "ღ", "S" to "შ", "T" to "თ", "U" to "უ",
            "V" to "ვ", "W" to "ჭ", "X" to "ხ", "Y" to "ყ", "Z" to "ძ"
        )

        var str = letters
        for ((key, value) in replaceMap) {
            str = str.replace(key.toRegex(), value)
        }

        return str

    }


    //show datePicker dialog func
    fun showDatePickerDialog(textView: TextView, context: Context) {

        val datePicker: DatePickerHelper = DatePickerHelper(context, true)

        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)

        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "$dayofMonth"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "$mon"
                val date = "${dayStr}/${monthStr}/${year}"
                textView.text = date.toString()
            }
        })
    }


    //convert date format (for back)
    fun convertDateFormat(date: String): String {
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parsedDate = LocalDate.parse(date, format)
        return parsedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    }


    //convert date format (from back to android)
    fun convertDateTimeFormat(dateString: String, hasTime: Boolean = true): String {
        //Before Java 8:
        /* try {
             val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
             val output = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
             val result = input.parse(date)
             return output.format(result!!)
         } catch (e: Exception) {
             throw IllegalArgumentException (e)
         }*/

        try {
            val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneOffset.UTC)
            val date: TemporalAccessor = fmt.parse(dateString)
            val time: Instant = Instant.from(date)
            val fmtOut: DateTimeFormatter = if (hasTime) {
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneOffset.UTC)
            } else {
                DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneOffset.UTC)
            }
            return fmtOut.format(time)
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }


    //check if dark mode ?
    fun isNightMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    //play sound
    fun playSound(myActivity: Activity?, myAudioFile: Int) {

        val player = MediaPlayer.create(myActivity, myAudioFile)

        player.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            player.isLooping = false

            player.setVolume(5f, 5f)

            player.setOnCompletionListener { player ->
                player.stop()
                player.release()
            }

            player.start()
        }
    }



    fun alphaInAnimation (view: View) {
        view.alpha = 0.5f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .translationY(view.height.toFloat())
            .rotation(360f)
            .scaleX(0.5f).scaleY(0.5f)
            .setDuration(400)
            .setListener(null)
    }

    fun alphaOutAnimation (view: View) {
        view.alpha = 0.5f
        view.animate()
            .alpha(0f)
            .setDuration(400)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator) {}
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationStart(p0: Animator) {}

                override fun onAnimationEnd(p0: Animator) {
                    view.visibility = View.GONE
                }
            })
    }

}



