package ge.gogichaishvili.lotto.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.app.tools.wrap
import ge.gogichaishvili.lotto.databinding.ActivitySplashScreenBinding
import ge.gogichaishvili.lotto.main.presentation.activities.MainActivity


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    //Binding
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        //Binding
        _binding = ActivitySplashScreenBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding?.root)

        //check Dark Mode
        val isDarkMode = SharedPreferenceManager(this).isDarkMode()
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //app first run
        //SharedPreferenceManager(this).setIsFirstLaunch(isFirst = false)

        //Animation
        /* binding.ivLogo.alpha = 0f
         binding.ivLogo.animate().setDuration(1500).alpha(1f).withEndAction {
             val i = Intent(this, MainActivity::class.java)
             startActivity(i)
             overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
             finish()
         }*/

        //No Animation (Right way)
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        //overridePendingTransition(0,0)
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //set language new method from LanguageContextWrapper
    override fun attachBaseContext(newBase: Context) {
        val selectedLanguage = SharedPreferenceManager(newBase).getSelectedLanguageCode()

        super.attachBaseContext(ContextWrapper(newBase).wrap(selectedLanguage))
    }

}