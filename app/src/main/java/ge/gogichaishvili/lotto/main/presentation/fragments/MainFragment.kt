package ge.gogichaishvili.lotto.main.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentMainBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainActivityViewModel

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

        binding.newGameBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    GameBoardFragment()
                ).addToBackStack(
                    GameBoardFragment::class.java.name
                ).commit()
        }

    }

}