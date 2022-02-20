package ge.gogichaishvili.lotto.main.presentation.activities

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.ActivityMainBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.MainFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainViewModel
import ge.gogichaishvili.lotto.app.utils.Utils.hideSystemUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()  //viewModel

    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)  //set screen orientation
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  //keep screen on

        //Binding
        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding?.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, MainFragment())
            .addToBackStack(MainFragment::class.java.name)
            .commit()

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(this)
    }

}