package ge.gogichaishvili.lotto.app.tools

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.ContextWrapper
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


inline fun View.afterMeasured(crossinline f: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (width > 0 && height > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}


//get screen size
fun Activity.getScreenSize(): DisplayMetrics {
    /* val display = windowManager.defaultDisplay
     val size = Point()
     display.getSize(size)
     return size*/

    val outMetrics = DisplayMetrics()

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val display = display
        display?.getRealMetrics(outMetrics)
    } else {
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(outMetrics)
    }
    return outMetrics
}


/*fun ProgressBar.setBigMax(max: Int) {
    this.max = max * 1000
}

fun ProgressBar.animateTo(progressTo: Int, startDelay: Long) {
    val animation = ObjectAnimator.ofInt(
        this,
        "progress",
        this.progress,
        progressTo * 1000
    )

    animation.duration = 500
    animation.interpolator = DecelerateInterpolator()
    animation.startDelay = startDelay
    animation.start()
}*/



fun View.hide() {
    this.visibility = View.GONE
}

fun View.hideIf(hide: Boolean) {
    this.visibility = if (hide) View.GONE else View.VISIBLE
}


val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.pxToSp: Int
    get() = ((this / Resources.getSystem().displayMetrics.scaledDensity).toInt())


//RecyclerView decoration
fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}


//hide keyboard
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

//show keyboard
fun Fragment.showKeyboard() {
    view?.let { activity?.showKeyboard(it) }
}

fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}

fun Context.showKeyboard(view: View) {
/*    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)*/

    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}


//show keyboard for edittext
fun EditText.showKeyboard() {
    if (requestFocus()) {
        (getActivity()?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, SHOW_IMPLICIT)
        setSelection(text.length)
    }
}

fun View.getActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}


//check email verification
fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

//string to boolean
fun String.toBoolean() = equals("true", ignoreCase = true)


//Only there
// add & remove view
fun View?.removeSelf() {
    this ?: return
    val parent = parent as? ViewGroup ?: return
    parent.removeView(this)
}

fun ViewGroup?.addView(view: View?) {
    this ?: return
    view ?: return
    addView(view)
}

fun View?.addTo(parent: ViewGroup?) {
    this ?: return
    parent ?: return
    parent.addView(this)
}