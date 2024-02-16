package ge.gogichaishvili.lotto.main.presentation.activities

import android.content.ContentValues
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.OnUserEarnedRewardListener
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.ActivityAdMobBinding
import ge.gogichaishvili.lotto.databinding.ActivityMainBinding
import java.util.Arrays

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
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                }
            })
    }


    private fun showRewardedAd() {
        if (mRewardedAd != null) {

            mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    mRewardedAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    mRewardedAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    mRewardedAd = null
                    loadRewardedAd()
                }
            }

            mRewardedAd!!.show(this, OnUserEarnedRewardListener() {

                val rewardAmount = it.amount
                var rewardType = it.type

                totalCoins += rewardAmount
                binding.tvInfo.text = totalCoins.toString()

            })
        } else {
            println("The rewarded ad wasn't ready yet.")
            loadRewardedAd()
        }
    }
}