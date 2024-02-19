package ge.gogichaishvili.lotto.main.presentation.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.enableFullScreen
import ge.gogichaishvili.lotto.databinding.ActivityAdMobBinding
import ge.gogichaishvili.lotto.main.presentation.viewmodels.AdmobViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdMobActivity : AppCompatActivity() {

    private val viewModel: AdmobViewModel by viewModel()

    private var _binding: ActivityAdMobBinding? = null
    private val binding get() = _binding!!

    private var mRewardedAd: RewardedAd? = null

    private var totalCoins = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        enableFullScreen()

        _binding = ActivityAdMobBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding?.root)

        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("cc86e2a9-f3f7-41a4-a881-10ca1ad1abe1")) // Your test device ID
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        MobileAds.initialize(this)

        loadRewardedAd()

        binding.btnShow.setOnClickListener {
            showRewardedAd()
        }

    }

    private fun loadRewardedAd() {

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            this,
            "ca-app-pub-4290928451578259/9410286233",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mRewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            mRewardedAd = null
                            loadRewardedAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            mRewardedAd = null
                            loadRewardedAd()
                        }

                        override fun onAdShowedFullScreenContent() {

                        }
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd!!.show(this) {
                val rewardAmount = it.amount
                var rewardType = it.type
                viewModel.saveNewBalance(it.amount)

                totalCoins += rewardAmount
                binding.tvInfo.text = "${getString(R.string.new_balance_is)} ${viewModel.getNewBalance()} ${getString(R.string.coins)}"
            }
        } else {
            Toast.makeText(this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show()
            loadRewardedAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}