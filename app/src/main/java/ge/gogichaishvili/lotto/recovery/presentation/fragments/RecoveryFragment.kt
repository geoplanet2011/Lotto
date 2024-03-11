package ge.gogichaishvili.lotto.recovery.presentation.fragments

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
import ge.gogichaishvili.lotto.databinding.FragmentRecoveryBinding

class RecoveryFragment : Fragment() {

    private var _binding: FragmentRecoveryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecoveryBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recoverBtn.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val email: String = binding.emailInput.text.trim().toString()
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), R.string.enter_email, Toast.LENGTH_SHORT).show()
        } else {
            showLoader()
            hideKeyboard()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    hideLoader()
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            R.string.reset_password,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        }
                    } else {
                        val errorMessage = task.exception?.message ?: getString(R.string.error)
                        Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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