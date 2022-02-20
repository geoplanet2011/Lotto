package ge.gogichaishvili.lotto.main.presentation.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.Utils
import ge.gogichaishvili.lotto.databinding.FragmentMainBinding
import ge.gogichaishvili.lotto.main.data.LottoStonesModel
import ge.gogichaishvili.lotto.main.helpers.LottoCardManager
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class MainFragment : Fragment() {

    //shared viewModel
    private val mainActivityVM: MainViewModel by sharedViewModel()

    //Binding
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val timer: Timer = Timer()
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Start background music service
        //context!!.startService(Intent(context, SoundService::class.java))

        mainActivityVM.generateCard(requireContext(), binding.llCards)

        getLottoStones()

        //observe
        mainActivityVM.requestStateLiveData.observe(requireActivity(), Observer { it ->

            addLottoStones(it)


        })



        mainActivityVM.generateLottoCardRequestStateLiveData.observe(requireActivity(), Observer {
            if (!it.isDraw) mainActivityVM.generateCard(requireContext(), binding.llCards)

        })


    }


    private fun getLottoStones() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread(Runnable {
                    mainActivityVM.getNumberFromBag() //get number from bag
                })
            }

        }, 0, 2000)
    }


    private fun addLottoStones(lottoStonesModel: LottoStonesModel) {
        //create dynamic lotto stone
        val lottoStone = Button(requireContext())

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            150,
            150
        )
        params.setMargins(10, 0, 10, 0)
        lottoStone.layoutParams = params
        lottoStone.setBackgroundResource(R.drawable.barrel_small)
        //lottoStone.text = it.lottoNumbers.last().toString()

        //add lotto stones in layout
        lottoStonesModel.lottoNumbers.forEach { lottoNumber ->

            lottoStone.text = lottoNumber.toString()
            if (lottoStone.parent != null) {
                (lottoStone.parent as ViewGroup).removeView(lottoStone)
            }

            //if lotto stone numbers > 3 then remove last lotto stone
            if (binding.llStones.childCount > 3) {

                val lastLottoNumberID =
                    binding.llStones.childCount - 1   //get last lotto stone number id

                val lastLottoNumberView =
                    binding.llStones.getChildAt(lastLottoNumberID)  //get last lotto stone view

                val animation: Animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
                animation.fillBefore = true
                animation.fillAfter = true
                lastLottoNumberView.startAnimation(animation)

                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {

                    }

                    override fun onAnimationRepeat(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {

                        binding.llStones.removeView(lastLottoNumberView)
                    }
                })

            }

            //add lotto stone
            lottoStone.visibility = View.INVISIBLE
            binding.llStones.addView(lottoStone, 0)

            val animation2: Animation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            animation2.fillBefore = true
            animation2.fillAfter = true
            lottoStone.startAnimation(animation2)
            animation2.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    lottoStone.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    animation2.cancel()

                }
            })

            //sound
            val soundFileName = "a" + lottoStonesModel.lottoNumbers.last().toString()
            val resID = resources.getIdentifier(soundFileName, "raw", activity?.packageName)
            Utils.playSound(activity, resID) //play sound

        }

        /* binding.llStones.removeAllViews()
         lottoStone.id = 1
         val temp = binding.llStones.findViewById<Button>(lottoStone.id)
         binding.llStones.removeView(temp)


         //get first view
         val childCount: Int = binding.llStones.childCount
         for (i in 0 until childCount) {
             val v: View = binding.llStones.getChildAt(i)

         }

         val views = ArrayList<View>()
         for (x in 0 until binding.llStones.childCount) {
             views.add(binding.llStones.getChildAt(x))
         }
         binding.llStones.removeAllViews()
         for (x in views.indices.reversed()) {
             binding.llStones.addView(views[x])
         }*/
    }

    override fun onDestroy() {
        //context!!.stopService(Intent(context, SoundService::class.java))
        super.onDestroy()
    }

}

