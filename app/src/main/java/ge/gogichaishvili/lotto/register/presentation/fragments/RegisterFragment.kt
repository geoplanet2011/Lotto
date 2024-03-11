package ge.gogichaishvili.lotto.register.presentation.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Users")

        binding.registerBtn.setOnClickListener {
            register()
        }

        binding.pictureIV.setOnClickListener {

        }

        binding.cameraBtn.setOnClickListener {

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun register() {

        if (binding.emailInput.text.trim().toString().isEmpty() || binding.passwordInput.text.trim()
                .toString().isEmpty()
        ) {
            Toast.makeText(requireContext(), R.string.input_required, Toast.LENGTH_SHORT).show()
            return
        }
        createUser(
            binding.emailInput.text.trim().toString(),
            binding.passwordInput.text.trim().toString()
        )
    }

    private fun createUser(email: String, password: String) {
        showLoader()
        hideKeyboard()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) {
                hideLoader()
                if (it.isSuccessful) {
                    val currentUser = auth.currentUser
                    val currentUserDb = databaseReference?.child((currentUser?.uid!!))
                    currentUserDb?.child("firstname")
                        ?.setValue(binding.firstnameInput.text.trim().toString())
                    currentUserDb?.child("status")?.setValue("offline")
                    Toast.makeText(
                        requireContext(),
                        R.string.registration_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                        requireActivity().supportFragmentManager.popBackStackImmediate()
                    }
                } else {
                    val error = it.exception?.message ?: getString(R.string.error)
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) {
                    return source
                }
                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            } else {
                if (source.width <= maxLength) {
                    return source
                }
                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: Exception) {
            return source
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

}