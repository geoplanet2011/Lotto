package ge.gogichaishvili.lotto.app.tools

import android.app.Activity
import android.app.Application
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.mikhaellopez.circularimageview.CircularImageView

object Tools {

    fun playSound(context: Context, audio: Int) {

        val mediaPlayer = MediaPlayer.create(context, audio)

        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            mediaPlayer.isLooping = false

            mediaPlayer.setVolume(5f, 5f)

            mediaPlayer.setOnCompletionListener { player ->
                player.stop()
                player.release()
            }

            mediaPlayer.start()

        }

    }

    fun setLocked(v: CircularImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val cf = ColorMatrixColorFilter(matrix)
        v.colorFilter = cf
        v.imageAlpha = 128
    }

    fun setUnlocked(v: CircularImageView) {
        v.colorFilter = null
        v.imageAlpha = 255
        v.clearColorFilter()
        v.setColorFilter(0xFFFFFFFF.toInt(), PorterDuff.Mode.MULTIPLY)
        v.invalidate()
    }

    fun alphaInAnimation(view: View) {
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

}