package ge.gogichaishvili.lotto.register.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentRegisterBinding
import java.net.URI
import java.util.UUID

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    private var filePath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

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

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        binding.registerBtn.setOnClickListener {
            register()
        }

        binding.galleryBtn.setOnClickListener {

        }

        binding.cameraBtn.setOnClickListener {

        }

        binding.tvBack.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

        getRegisterUsersCount()

    }

    private fun getRegisterUsersCount () {
        databaseReference?.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val totalUsers = dataSnapshot.childrenCount.toString()
                binding.tvTotal.text = "${getString(R.string.total_users_count)} $totalUsers"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun register() {

        if (binding.emailInput.text.trim().toString().isEmpty() || binding.passwordInput.text.trim()
                .toString().isEmpty() || binding.firstnameInput.text.trim().toString().isEmpty()
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

    private fun uploadImage() {
        if (filePath != null) {
            var ref: StorageReference = storageRef.child("image/"+UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    OnSuccessListener<UploadTask.TaskSnapshot> {
                        Toast.makeText(requireContext(), "uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{
                    OnFailureListener{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnProgressListener {
                    OnProgressListener<UploadTask.TaskSnapshot> {
                        var progress: Double = (100.0 * it.bytesTransferred/it.totalByteCount)
                    }
                }
        }
    }

}