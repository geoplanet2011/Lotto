package ge.gogichaishvili.lotto.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {
            val newPassword = binding.passwordInput.text.toString().trim()
            if (newPassword.isNotEmpty()) {
                updatePassword(newPassword)
            } else {
                Toast.makeText(requireContext(), R.string.input_required, Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvBack.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

    }

    private fun updatePassword(newPassword: String) {
        showLoader()
        hideKeyboard()
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                hideLoader()
                if (task.isSuccessful) {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Password updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                        requireActivity().supportFragmentManager.popBackStackImmediate()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to update password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        if (binding.progressBar.isVisible) {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}