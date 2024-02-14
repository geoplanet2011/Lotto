package ge.gogichaishvili.lotto.main.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentMainBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainActivityViewModel
import ge.gogichaishvili.lotto.settings.presentation.fragments.SettingsFragment

class MainFragment : BaseFragment<MainActivityViewModel>(MainActivityViewModel::class) {

    override fun isGlobal() = true

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

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

        binding.exitBtn.setOnClickListener {
            activity?.finishAndRemoveTask()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}