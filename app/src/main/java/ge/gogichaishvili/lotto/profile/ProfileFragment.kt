package ge.gogichaishvili.lotto.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentProfileBinding
import ge.gogichaishvili.lotto.databinding.FragmentRecoveryBinding
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val DEFAULT_IMAGE_URL = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png"
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        //upload button
        binding.uploadBt.setOnClickListener {
            takePicture()
        }

        binding.savedBt.setOnClickListener {
            val photo = when {
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }

            val updates = UserProfileChangeRequest.Builder()
                .setPhotoUri(photo)
                .build()

            binding.progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener{ task ->
                    binding.progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(requireContext(), task.exception?.message!!, Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //Take picture
    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(requireActivity()?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    //Take picture permission
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    //upload image to firebase
    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef =
            FirebaseStorage.getInstance().reference.child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()
        val upload = storageRef.putBytes(image)

        binding.progressbar.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            binding.progressbar.visibility = View.INVISIBLE
            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        Toast.makeText(requireContext(), imageUri.toString(), Toast.LENGTH_SHORT).show()

                        binding.profileImg.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    Toast.makeText(requireContext(), it.message!!, Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
}