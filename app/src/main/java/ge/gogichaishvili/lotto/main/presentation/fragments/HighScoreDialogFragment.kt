package ge.gogichaishvili.lotto.main.presentation.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.vibratePhone
import ge.gogichaishvili.lotto.databinding.FragmentHighscoreDialogBinding
import ge.gogichaishvili.lotto.main.helpers.RatingManager
import ge.gogichaishvili.lotto.main.presentation.viewmodels.HighScoreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HighScoreDialogFragment : DialogFragment() {

    private var _binding: FragmentHighscoreDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HighScoreViewModel by viewModel()

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialogTheme)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentHighscoreDialogBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.updateLayoutParams<FrameLayout.LayoutParams> {
            val w = (resources.displayMetrics.widthPixels * 0.90).toInt()
            width = w
        }

        vibratePhone()

        animateTrophy()

        val balance = viewModel.getBalance()
        val wins = viewModel.getWins()
        val loses = viewModel.getLoses()
        val rating = viewModel.calculatePlayerRating(wins, loses, balance)

        viewModel.initRatingManager(rating)
        updateStarsUI(viewModel.getStars())

        binding.tvRating.text = "${getString(R.string.your_rating)} $rating"
        binding.tvBalance.text =
            "${getString(R.string.your_balance)} $balance ${getString(R.string.valuta)}"
        binding.tvWin.text = "${getString(R.string.wins)} $wins"
        binding.tvLose.text = "${getString(R.string.loses)} $loses"

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.applause)
        mediaPlayer.start()

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun updateStarsUI(stars: List<RatingManager.Star>) {
        binding.llStars.removeAllViews()
        var delay = 0L
        val animationDuration = 300L

        stars.forEach { star ->
            val starImageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { params ->
                    val marginInPixels = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics
                    ).toInt()
                    params.setMargins(
                        marginInPixels,
                        marginInPixels,
                        marginInPixels,
                        marginInPixels
                    )
                }
                setImageResource(
                    when (star.state) {
                        RatingManager.Star.StarState.FullStar -> R.drawable.starfull
                        RatingManager.Star.StarState.HalfStar -> R.drawable.starhalf
                        else -> R.drawable.star
                    }
                )
                alpha = 0f
                scaleX = 0f
                scaleY = 0f
            }
            binding.llStars.addView(starImageView)

            starImageView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(animationDuration)
                .setStartDelay(delay)
                .start()

            delay += 100
        }
    }

    private fun animateTrophy() {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            binding.ivLogo,
            PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f)
        ).apply {
            duration = 500
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
        }

        scaleUp.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.stop()
        mediaPlayer.release()
        _binding = null
    }


}

