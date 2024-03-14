package ge.gogichaishvili.lotto.login.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentLoginBinding
import ge.gogichaishvili.lotto.login.presentation.viewmodels.LoginViewModel
import ge.gogichaishvili.lotto.main.presentation.fragments.DashboardFragment
import ge.gogichaishvili.lotto.main.presentation.fragments.RoomListFragment
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.CreateRoomViewModel
import ge.gogichaishvili.lotto.recovery.presentation.fragments.RecoveryFragment
import ge.gogichaishvili.lotto.register.presentation.fragments.RegisterFragment

class LoginFragment : BaseFragment<LoginViewModel>(LoginViewModel::class) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private var isPasswordVisible = false

    private var rememberStatus: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        /*val currentUser = auth.currentUser
        if (currentUser != null) {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    DashboardFragment()
                ).addToBackStack(
                    DashboardFragment::class.java.name
                ).commit()
        }*/

        rememberStatus = mViewModel.getUserRememberStatus()
        binding.rbRemember.isChecked = rememberStatus == true
        if (rememberStatus) {
            try {
                val username = mViewModel.getUserName()
                binding.emailInput.setText(username)
            } catch (e: Exception) {
                println(e.message.toString())
            }
        }

        binding.rbRemember.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                mViewModel.onUserRememberRadioButtonChange(false, "")
            }
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

        binding.passwordInput.setOnTouchListener { v, event ->

            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.passwordInput.right - binding.passwordInput.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        binding.passwordInput.inputType =
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        binding.passwordInput.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    binding.passwordInput.setSelection(binding.passwordInput.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.tvBack.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
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
                .isNotEmpty() && binding.passwordInput.text.trim().toString().isNotEmpty()
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
        hideKeyboard()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) {
                hideLoader()
                if (it.isSuccessful) {
                    setUserRemember()
                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainerView,
                            RoomListFragment()
                        ).addToBackStack(
                            RoomListFragment::class.java.name
                        ).commit()
                } else {
                    val error = it.exception?.message ?: getString(R.string.error)
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
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

    private fun setUserRemember () {
        if (binding.rbRemember.isChecked && binding.emailInput.text.toString().trim()
                .isNotEmpty()
        ) {
            mViewModel.onUserRememberRadioButtonChange(
                true,
                binding.emailInput.text.toString().trim()
            )
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