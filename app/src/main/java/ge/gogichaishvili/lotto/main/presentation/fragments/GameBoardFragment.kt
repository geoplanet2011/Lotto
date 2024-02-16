package ge.gogichaishvili.lotto.main.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.Utils
import ge.gogichaishvili.lotto.databinding.FragmentGameBoardBinding
import ge.gogichaishvili.lotto.main.enums.ChipValueEnum
import ge.gogichaishvili.lotto.main.enums.GameOverStatusEnum
import ge.gogichaishvili.lotto.main.helpers.AnimationManager
import ge.gogichaishvili.lotto.main.helpers.BetChipDrawerManager
import ge.gogichaishvili.lotto.main.models.LottoDrawResult
import ge.gogichaishvili.lotto.main.models.OpponentAvatarModel
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.GameBoardViewModel
import java.util.Objects
import java.util.Timer
import java.util.TimerTask
import kotlin.math.sqrt

class GameBoardFragment : BaseFragment<GameBoardViewModel>(GameBoardViewModel::class),
    View.OnClickListener {

    private var timer: Timer = Timer()

    private var isGamePaused = false

    private var bet = 0
    private val maxBet = 500
    private var balance = 0

    private val betChipDrawerManager = BetChipDrawerManager()

    private lateinit var opponent: OpponentAvatarModel
    private var _binding: FragmentGameBoardBinding? = null
    private val binding get() = _binding!!

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBoardBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!
            .registerListener(
                sensorListener, sensorManager!!
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
            )
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        resetGame()

        val player = mViewModel.getPlayerInfo()
        opponent = mViewModel.getOpponentInfo()
        binding.tvPlayerOneName.text = player.nickName.toString()
        binding.tvPlayerTwoName.text = getString(opponent.name)
        balance = player.balance
        binding.tvPlayerOneScore.text = balance.toString()
        binding.ivPlayer.setImageResource(player.avatar)
        binding.ivOpponent.setImageResource(opponent.avatar)

        mViewModel.generateOpponentCard()
        mViewModel.generateCard(requireContext(), binding.llCards)

        arrayOf(
            binding.chip0,
            binding.chip5,
            binding.chip10,
            binding.chip25,
            binding.chip50,
            binding.chip100
        ).forEach {
            it.setOnClickListener(this)
            it.isSoundEffectsEnabled = false
        }

        binding.btnChange.setOnClickListener {
            mViewModel.redrawCard(requireContext(), binding.llCards)
        }

        binding.btnStart.setOnClickListener {
            if (bet > 0) {
                binding.btnChange.visibility = View.GONE
                binding.btnStart.visibility = View.GONE
                binding.btnPause.visibility = View.VISIBLE
                binding.llChips.visibility = View.GONE
                binding.betText.visibility = View.GONE
                binding.llDrawChips.visibility = View.GONE
                timer = Timer()
                getLottoStones()
            } else {
                Toast.makeText(requireContext(), getString(R.string.min_bet), Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnPause.setOnClickListener {
            if (isGamePaused) {
                resumeGame()
            } else {
                pauseGame()
            }
        }

    }

    private fun getLottoStones() {
       val period = mViewModel.getGameSpeed()
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread(Runnable {
                    mViewModel.getNumberFromBag()
                })
            }

        }, 0, period)
    }

    private fun handleLottoDrawResult(result: LottoDrawResult) {
        if (result.isEmpty) {
            println("bag is empty")
            calculateNewBalance(GameOverStatusEnum.Draw)
            mViewModel.checkGameResult(GameOverStatusEnum.Draw, requireContext())
            resetGame()
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
            textSize = 22f
            setTextColor(Color.rgb(112, 40, 31))
            visibility = View.INVISIBLE
        }
        if (binding.llStones.childCount == 3) {
            removeOldestLottoStone()
        }
        binding.llStones.addView(lottoStone, 0)
        animateLottoStoneAppearance(lottoStone)
        if (mViewModel.isSoundEnabled()) {
            playSoundForNumber(number)
        }
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
        var soundFileName = ""
        when (mViewModel.getSelectedLanguage()) {
            "en" -> {
                soundFileName = "a$number"
            }
            "ru" -> {
                soundFileName = "r$number"
            }
            "ka" -> {
               soundFileName = "g$number"
            }
        }
        val resID = resources.getIdentifier(soundFileName, "raw", activity?.packageName)
        Utils.playSound(activity, resID)
    }


    private fun pauseGame() {
        timer.cancel()
        timer.purge()
        isGamePaused = true
        binding.btnPause.setBackgroundResource(R.drawable.baseline_play)
        mViewModel.disableAllViewsInViewGroup(binding.llCards)
    }

    private fun resumeGame() {
        timer = Timer()
        getLottoStones()
        isGamePaused = false
        binding.btnPause.setBackgroundResource(R.drawable.baseline_pause)
        mViewModel.enableAllViewsInViewGroup(binding.llCards)
    }

    override fun bindObservers() {

        mViewModel.lineCompletionEvent.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.line_is_filled), Toast.LENGTH_SHORT).show()
        }
        mViewModel.cardCompletionEvent.observe(viewLifecycleOwner) {
            //Toast.makeText(requireContext(), "ბილეთი შევსებულია!", Toast.LENGTH_SHORT).show()
            calculateNewBalance(GameOverStatusEnum.PLAYER_WIN)
            mViewModel.checkGameResult(GameOverStatusEnum.PLAYER_WIN, requireContext())
            resetGame()
        }

        mViewModel.requestStateLiveData.observe(viewLifecycleOwner) { it ->
            handleLottoDrawResult(it)

            if (it.numbers.isNotEmpty()) {

                if (mViewModel.isHintEnabled()) {
                    mViewModel.lottoCardManager.setHints(it.numbers, binding.llCards)
                }

                val removedNumbers =
                    mViewModel.lottoCardManager.previousNumbers - it.numbers.toSet()

                mViewModel.lottoCardManager.setLoss(removedNumbers, binding.llCards)

                mViewModel.lottoCardManager.previousNumbers = it.numbers

                mViewModel.checkOpponentGameCompletion(it.numbers.last())
            }

        }

        mViewModel.opponentLineCompletionEvent.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "${opponent.name}${getString(R.string.opponent_line_is_filled)}", Toast.LENGTH_SHORT)
                .show()
        }
        mViewModel.opponentCardCompletionEvent.observe(viewLifecycleOwner) {
            //Toast.makeText(requireContext(), "ოპონენტის ბილეთი შევსებულია!", Toast.LENGTH_SHORT).show()
            calculateNewBalance(GameOverStatusEnum.OPPONENT_WIN)
            mViewModel.checkGameResult(GameOverStatusEnum.OPPONENT_WIN, requireContext())
            resetGame()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.chip0 -> {
                onChipSelect(ChipValueEnum.Zero)
                betChipDrawerManager.drawChips(binding.llDrawChips, requireContext(), bet)
            }

            binding.chip5 -> {
                if (bet + ChipValueEnum.Five.value > maxBet) {
                    Toast.makeText(requireContext(), getString(R.string.max_bet), Toast.LENGTH_SHORT).show()
                    return
                }
                onChipSelect(ChipValueEnum.Five)
                betChipDrawerManager.drawChips(binding.llDrawChips, requireContext(), bet)
            }

            binding.chip10 -> {
                if (bet + ChipValueEnum.Ten.value > maxBet) {
                    Toast.makeText(requireContext(), getString(R.string.max_bet), Toast.LENGTH_SHORT).show()
                    return
                }
                onChipSelect(ChipValueEnum.Ten)
                betChipDrawerManager.drawChips(binding.llDrawChips, requireContext(), bet)
            }

            binding.chip25 -> {
                if (bet + ChipValueEnum.TwentyFive.value > maxBet) {
                    Toast.makeText(requireContext(), getString(R.string.max_bet), Toast.LENGTH_SHORT).show()
                    return
                }
                onChipSelect(ChipValueEnum.TwentyFive)
                betChipDrawerManager.drawChips(binding.llDrawChips, requireContext(), bet)
            }

            binding.chip50 -> {
                if (bet + ChipValueEnum.Fifty.value > maxBet) {
                    Toast.makeText(requireContext(), getString(R.string.max_bet), Toast.LENGTH_SHORT).show()
                    return
                }
                onChipSelect(ChipValueEnum.Fifty)
                betChipDrawerManager.drawChips(binding.llDrawChips, requireContext(), bet)
            }

            binding.chip100 -> {
                if (bet + ChipValueEnum.Hundred.value > maxBet) {
                    Toast.makeText(requireContext(), getString(R.string.max_bet), Toast.LENGTH_SHORT).show()
                    return
                }
                onChipSelect(ChipValueEnum.Hundred)
                betChipDrawerManager.drawChips(binding.llDrawChips, requireContext(), bet)
            }
        }
        Utils.playSound(activity, R.raw.chip)
        AnimationManager.chipAnimation(p0 as ImageButton)


    }

    private fun onChipSelect(chip: ChipValueEnum) {
        if (chip === ChipValueEnum.Zero) {
            balance += bet
            bet = 0
            binding.tvPlayerOneScore.text = balance.toString()
            binding.betText.text = bet.toString()
        } else if (balance >= chip.value) {
            bet += chip.value
            balance -= chip.value
            binding.tvPlayerOneScore.text = balance.toString()
            binding.betText.text = bet.toString()
        } else {
            Toast.makeText(context, getString(R.string.no_money), Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateNewBalance(gameOverStatus: GameOverStatusEnum) {
        when (gameOverStatus) {

            GameOverStatusEnum.PLAYER_WIN -> {
                balance += bet * 2
                bet = 0
            }

            GameOverStatusEnum.OPPONENT_WIN -> {
                bet = 0
            }

            GameOverStatusEnum.Draw -> {
                balance += bet
                bet = 0
            }

        }
        mViewModel.saveNewBalance(balance)
        binding.tvPlayerOneScore.text = balance.toString()
        binding.betText.text = bet.toString()
    }

    private fun resetGame() {
        timer.cancel()
        timer.purge()
        mViewModel.resetManagers()
        mViewModel.generateOpponentCard()
        mViewModel.redrawCard(requireContext(), binding.llCards)
        binding.llStones.removeAllViews()
        binding.llDrawChips.removeAllViews()
        binding.btnChange.visibility = View.VISIBLE
        binding.btnStart.visibility = View.VISIBLE
        binding.btnPause.visibility = View.GONE
        binding.llChips.visibility = View.VISIBLE
        binding.betText.visibility = View.VISIBLE
        binding.llDrawChips.visibility = View.VISIBLE
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 10) {
                if (binding.btnStart.isVisible) {
                    Utils.playSound(activity, R.raw.shuffle)
                    mViewModel.bagShuffle()
                    Toast.makeText(requireContext(), getString(R.string.shuffle), Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

}


