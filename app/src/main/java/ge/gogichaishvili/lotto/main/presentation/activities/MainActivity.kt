package ge.gogichaishvili.lotto.main.presentation.activities

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import ge.gogichaishvili.lotto.app.tools.enableFullScreen
import ge.gogichaishvili.lotto.app.tools.getBackStackTag
import ge.gogichaishvili.lotto.app.tools.removeFragmentByStackName
import ge.gogichaishvili.lotto.app.tools.wrap
import ge.gogichaishvili.lotto.databinding.ActivityMainBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.MainFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainActivityViewModel
import ge.gogichaishvili.lotto.settings.presentation.fragments.SettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableFullScreen()
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding?.root)

        setCorrectScreen()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        enableFullScreen()
    }

    override fun attachBaseContext(newBase: Context) {
        val selectedLanguage = SharedPreferenceManager(newBase).getSelectedLanguageCode()
        super.attachBaseContext(ContextWrapper(newBase).wrap(selectedLanguage))
    }

    private fun setCorrectScreen() {
        val targetFragment = if (intent.getBooleanExtra("openSettingsFragment", false)) {
            val settingsFragment = SettingsFragment()
            val mainFragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, mainFragment)
                .addToBackStack(mainFragment.getBackStackTag())
                .commit()

            settingsFragment
        } else {
            MainFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView,
                targetFragment,
                targetFragment.arguments?.getString("TAG")
            )
            .addToBackStack(
                targetFragment.arguments?.getString("TAG") ?: targetFragment.getBackStackTag()
            )
            .commit()
    }

}