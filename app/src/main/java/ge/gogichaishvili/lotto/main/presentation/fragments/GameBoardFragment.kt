package ge.gogichaishvili.lotto.main.presentation.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentGameBoardBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.GameBoardViewModel
import java.util.Timer
import java.util.TimerTask

class GameBoardFragment : BaseFragment<GameBoardViewModel>(GameBoardViewModel::class) {

    private val timer: Timer = Timer()
    private var player: MediaPlayer? = null

    private var _binding: FragmentGameBoardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBoardBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getLottoStones()


        mViewModel.requestStateLiveData.observe(requireActivity(), Observer { it ->

            //create dynamic lotto stone
            val lottoStone = Button(requireContext())

            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                150,
                150
            )
            params.setMargins(10, 0, 10, 0)
            lottoStone.layoutParams = params
            lottoStone.setBackgroundResource(R.drawable.barrel_small)
            lottoStone.text = it.lottoNumbers.last().toString()

            binding.llStones.addView(lottoStone)



            val soundFileName = "a" + it.lottoNumbers.last().toString()
            val resID = resources.getIdentifier(soundFileName, "raw", activity?.packageName)
           // Utils.playSound(activity, resID) //play sound



        })

    }


    private fun getLottoStones() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread(Runnable {
                    mViewModel.getNumberFromBag()
                })
            }

        }, 0, 2000)
    }

}