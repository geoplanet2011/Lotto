package ge.gogichaishvili.lotto.main.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentMainBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainActivityViewModel
import ge.gogichaishvili.lotto.settings.presentation.fragments.SettingsFragment

class MainFragment : BaseFragment<MainActivityViewModel>(MainActivityViewModel::class) {

    override fun isGlobal() = true

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var mRewardedAd: RewardedAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        try {
            loadRewardedAd()
        } catch (e: Exception) {
            println(e.message.toString())
        }

        binding.newGameBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    GameBoardFragment()
                ).addToBackStack(
                    GameBoardFragment::class.java.name
                ).commit()
        }

        binding.settingsBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    SettingsFragment()
                ).addToBackStack(
                    SettingsFragment::class.java.name
                ).commit()
        }

        binding.admobBtn.setOnClickListener {
            showRewardedAd()
        }

        binding.recordsBtn.setOnClickListener {
            val dialog = HighScoreDialogFragment()
            dialog.show(
                (context as FragmentActivity).supportFragmentManager,
                "HighScoreDialogFragment"
            )
        }

        binding.exitBtn.setOnClickListener {
            activity?.finishAndRemoveTask()
        }

    }

    private fun loadRewardedAd() {

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            requireContext(),
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
            mRewardedAd!!.show(requireActivity()) {
                mViewModel.saveNewBalance(it.amount)
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.new_balance_is)} ${mViewModel.getNewBalance()} ${
                        getString(R.string.coins)
                    }",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "The rewarded ad wasn't ready yet.",
                Toast.LENGTH_SHORT
            ).show()
            loadRewardedAd()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}