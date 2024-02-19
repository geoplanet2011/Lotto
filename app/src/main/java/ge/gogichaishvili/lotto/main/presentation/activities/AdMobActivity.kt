package ge.gogichaishvili.lotto.main.presentation.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import ge.gogichaishvili.lotto.databinding.ActivityAdMobBinding

class AdMobActivity : AppCompatActivity() {

    private var _binding: ActivityAdMobBinding? = null
    private val binding get() = _binding!!

    private var mRewardedAd: RewardedAd? = null

    private var totalCoins = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        _binding = ActivityAdMobBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding?.root)

        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("cc86e2a9-f3f7-41a4-a881-10ca1ad1abe1")) // Your test device ID
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        MobileAds.initialize(this) {
            loadRewardedAd()
        }

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
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            loadRewardedAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            loadRewardedAd()
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Ad showed fullscreen content
                        }
                    }
                }
            })
    }

    private fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd!!.show(this) {
                val rewardAmount = it.amount
                var rewardType = it.type

                totalCoins += rewardAmount
                binding.tvInfo.text = totalCoins.toString()
            }
        } else {
            println("The rewarded ad wasn't ready yet.")
            loadRewardedAd()
        }
    }

}