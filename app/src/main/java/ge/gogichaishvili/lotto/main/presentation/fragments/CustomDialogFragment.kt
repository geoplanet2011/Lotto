package ge.gogichaishvili.lotto.main.presentation.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.getSerializableCompat
import ge.gogichaishvili.lotto.app.tools.vibratePhone
import ge.gogichaishvili.lotto.databinding.FragmentCustomDialogBinding

class CustomDialogFragment : DialogFragment() {

    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.updateLayoutParams<FrameLayout.LayoutParams> {
            val w = (resources.displayMetrics.widthPixels * 0.90).toInt()
            width = w
        }

        vibratePhone()

        val text = arguments?.getSerializableCompat("DATA", String::class.java) as String
        if (text == getString(R.string.lose)) {
            binding.ivLogo.setImageResource(R.drawable.lose)
        }
        binding.tvTitle.text = text

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}