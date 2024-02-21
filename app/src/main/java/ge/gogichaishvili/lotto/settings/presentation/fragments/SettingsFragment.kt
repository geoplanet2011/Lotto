package ge.gogichaishvili.lotto.settings.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.getBackStackTag
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentSettingsBinding
import ge.gogichaishvili.lotto.main.enums.GameSpeedEnum
import ge.gogichaishvili.lotto.main.presentation.activities.MainActivity
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.settings.presentation.viewmodels.SettingsViewModel

class SettingsFragment : BaseFragment<SettingsViewModel>(SettingsViewModel::class) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playerInfo = mViewModel.getPlayerInfo()
        binding.etPlayerName.setText(playerInfo.nickName.toString())
        binding.ivAvatar.setImageResource(playerInfo.avatar)

        val isHintEnabled = mViewModel.isHintEnabled()
        binding.hintSwitch.isChecked = isHintEnabled
        binding.hintSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingsFragment", "Hint switch changed to: $isChecked")
            mViewModel.onHintChanged(isChecked)
        }

        val isSoundEnabled = mViewModel.isSoundEnabled()
        binding.soundSwitch.isChecked = isSoundEnabled
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingsFragment", "Sound switch changed to: $isChecked")
            mViewModel.onSoundChanged(isChecked)
        }

        when (mViewModel.getSelectedLanguage()) {
            "ka" -> {
                binding.rbGeorgian.isChecked = true
                binding.rbEnglish.isChecked = false
                binding.rbRussian.isChecked = false
            }

            "en" -> {
                binding.rbGeorgian.isChecked = false
                binding.rbEnglish.isChecked = true
                binding.rbRussian.isChecked = false
            }

            "ru" -> {
                binding.rbRussian.isChecked = true
                binding.rbGeorgian.isChecked = false
                binding.rbEnglish.isChecked = false
            }
        }

        binding.radioGroup2.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rb_georgian) {
                mViewModel.onLanguageChanged("ka")
            }

            if (checkedId == R.id.rb_english) {
                mViewModel.onLanguageChanged("en")
            }

            if (checkedId == R.id.rb_russian) {
                mViewModel.onLanguageChanged("ru")
            }
            refreshSettings()
        }

        when (mViewModel.getGameSpeed()) {
            GameSpeedEnum.HIGH.value -> {
                binding.rbHigh.isChecked = true
                binding.rbMedium.isChecked = false
                binding.rbLow.isChecked = false
            }

            GameSpeedEnum.MEDIUM.value -> {
                binding.rbHigh.isChecked = false
                binding.rbMedium.isChecked = true
                binding.rbLow.isChecked = false
            }

            GameSpeedEnum.LOW.value -> {
                binding.rbHigh.isChecked = false
                binding.rbMedium.isChecked = false
                binding.rbLow.isChecked = true
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rb_high) {
                mViewModel.onGameSpeedChanged(GameSpeedEnum.HIGH.value)
            }

            if (checkedId == R.id.rb_medium) {
                mViewModel.onGameSpeedChanged(GameSpeedEnum.MEDIUM.value)
            }

            if (checkedId == R.id.rb_low) {
                mViewModel.onGameSpeedChanged(GameSpeedEnum.LOW.value)
            }

        }

        binding.tvBack.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

        binding.btnNext.setOnClickListener {
            mViewModel.nextAvatar()
            binding.ivAvatar.setImageResource(mViewModel.getAvatar())
        }

        binding.btnPrevious.setOnClickListener {
            mViewModel.previousAvatar()
            binding.ivAvatar.setImageResource(mViewModel.getAvatar())
        }

        binding.btnSave.setOnClickListener {
            hideKeyboard()
            if (binding.etPlayerName.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.enter_player_name,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mViewModel.savePlayerInfo(binding.etPlayerName.text.toString().trim())
                Snackbar.make(binding.root, getString(R.string.saved), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshSettings() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("openSettingsFragment", true)
            }
            requireActivity().finish()
            if (Build.VERSION.SDK_INT >= 34) {
                requireActivity().overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            } else {
                requireActivity().overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            }
            startActivity(intent)
        }, 200)
    }

}