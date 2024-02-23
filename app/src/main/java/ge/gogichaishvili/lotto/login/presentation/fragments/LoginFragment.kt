package ge.gogichaishvili.lotto.login.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentLoginBinding
import ge.gogichaishvili.lotto.main.presentation.fragments.DashboardFragment
import ge.gogichaishvili.lotto.recovery.presentation.fragments.RecoveryFragment
import ge.gogichaishvili.lotto.register.presentation.fragments.RegisterFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    DashboardFragment()
                ).addToBackStack(
                    DashboardFragment::class.java.name
                ).commit()
        }

        binding.loginBtn.setOnClickListener {
            login()
        }

        binding.registerBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    RegisterFragment()
                ).addToBackStack(
                    RegisterFragment::class.java.name
                ).commit()
        }

        binding.recoverBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    RecoveryFragment()
                ).addToBackStack(
                    RecoveryFragment::class.java.name
                ).commit()
        }

    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if (user != null) {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    DashboardFragment()
                ).addToBackStack(
                    DashboardFragment::class.java.name
                ).commit()
        }
    }

    private fun login() {

        if (binding.emailInput.text.trim().toString()
                .isNotEmpty() || binding.passwordInput.text.trim().toString().isNotEmpty()
        ) {
            singInUser(
                binding.emailInput.text.trim().toString(),
                binding.passwordInput.text.trim().toString()
            )
        } else {
            Toast.makeText(requireContext(), R.string.input_required, Toast.LENGTH_SHORT).show()
        }
    }

    private fun singInUser(email: String, password: String) {
        showLoader()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) {
                hideLoader()
                if (it.isSuccessful) {
                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainerView,
                            DashboardFragment()
                        ).addToBackStack(
                            DashboardFragment::class.java.name
                        ).commit()
                } else {
                    Toast.makeText(requireContext(), "Error " + it.exception, Toast.LENGTH_SHORT)
                        .show()
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

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirebaseAuth.getInstance().signOut()
        _binding = null
    }

}