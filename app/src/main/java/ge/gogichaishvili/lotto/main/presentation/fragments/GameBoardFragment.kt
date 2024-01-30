package ge.gogichaishvili.lotto.main.presentation.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.Utils
import ge.gogichaishvili.lotto.databinding.FragmentGameBoardBinding

import ge.gogichaishvili.lotto.main.models.LottoDrawResult
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

        mViewModel.generateCard(requireContext(), binding.llCards)
        mViewModel.generateLottoCardRequestStateLiveData.observe(requireActivity(), Observer {

        })


        getLottoStones()
        mViewModel.requestStateLiveData.observe(requireActivity(), Observer { it ->
            handleLottoDrawResult(it)
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

    private fun handleLottoDrawResult(result: LottoDrawResult) {
        if (result.isEmpty) {
            println("bag is empty")
        } else {
            val newNumber = result.numbers.last()
            addLottoStoneButton(newNumber)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun addLottoStoneButton(number: Int) {
        val lottoStone = Button(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                setMargins(10, 0, 10, 0)
            }
            setBackgroundResource(R.drawable.barrel_small)
            text = number.toString()
            visibility = View.INVISIBLE
        }

        if (binding.llStones.childCount > 3) {
            removeOldestLottoStone()
        }

        binding.llStones.addView(lottoStone, 0)
        animateLottoStoneAppearance(lottoStone)

        playSoundForNumber(number)
    }

    private fun removeOldestLottoStone() {
        val lastLottoNumberID = binding.llStones.childCount - 1
        val lastLottoNumberView = binding.llStones.getChildAt(lastLottoNumberID)
        lastLottoNumberView.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.scale_down
            ).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}
                    override fun onAnimationRepeat(p0: Animation?) {}
                    override fun onAnimationEnd(p0: Animation?) {
                        binding.llStones.removeView(lastLottoNumberView)
                    }
                })
            })
    }

    private fun animateLottoStoneAppearance(lottoStone: Button) {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    lottoStone.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {}
            })
        }
        lottoStone.startAnimation(animation)
    }

    @SuppressLint("DiscouragedApi")
    private fun playSoundForNumber(number: Int) {
        val soundFileName = "a$number"
        val resID = resources.getIdentifier(soundFileName, "raw", activity?.packageName)
        Utils.playSound(activity, resID)
    }

}


