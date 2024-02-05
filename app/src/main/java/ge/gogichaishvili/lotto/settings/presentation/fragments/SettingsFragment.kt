package ge.gogichaishvili.lotto.settings.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentSettingsBinding
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
}