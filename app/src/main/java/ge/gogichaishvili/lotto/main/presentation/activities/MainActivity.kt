package ge.gogichaishvili.lotto.main.presentation.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.enableFullScreen
import ge.gogichaishvili.lotto.databinding.ActivityMainBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.MainFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView,
                MainFragment()
            ).addToBackStack(
                MainFragment::class.java.name
            ).commit()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        enableFullScreen()
    }

}