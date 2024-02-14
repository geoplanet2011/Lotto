package ge.gogichaishvili.lotto.app.tools

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun Fragment.getBackStackTag(): String {
    return this::class.java.name
}

fun FragmentManager.removeFragmentByStackName(backStackName: String, flag: Int = 1): Boolean {
    var hasManualDeletePerformed = false
    try {
        if (backStackEntryCount != 0) {
            (0..backStackEntryCount).forEach {
                val entry = getBackStackEntryAt(it)
                if (entry.name == backStackName) {
                    popBackStack(entry.name, flag)
                    hasManualDeletePerformed = true
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return hasManualDeletePerformed
}

