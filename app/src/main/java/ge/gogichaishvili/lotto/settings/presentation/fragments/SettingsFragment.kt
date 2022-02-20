package ge.gogichaishvili.lotto.settings.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ge.gogichaishvili.lotto.app.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.databinding.FragmentSettingsBinding
import ge.gogichaishvili.lotto.settings.presentation.viewmodels.SettingsViewModel


class SettingsFragment : BaseFragment<SettingsViewModel>(SettingsViewModel::class) {

    //Binding
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}