package ge.gogichaishvili.lotto.main.helpers

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton

object AnimationManager {

    fun chipAnimation(imageButton: ImageButton) {
        val scaleXAnimation = ObjectAnimator.ofFloat(imageButton, View.SCALE_X, 1.2f).apply {
            duration = 50
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleYAnimation = ObjectAnimator.ofFloat(imageButton, View.SCALE_Y, 1.2f).apply {
            duration = 50
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleXAnimation2 = ObjectAnimator.ofFloat(imageButton, View.SCALE_X, 1f).apply {
            duration = 50
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleYAnimation2 = ObjectAnimator.ofFloat(imageButton, View.SCALE_Y, 1f).apply {
            duration = 50
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            play(scaleXAnimation).with(scaleYAnimation)
            play(scaleXAnimation2).with(scaleYAnimation2)
            play(scaleXAnimation2).after(scaleXAnimation)
            start()
        }
    }

}