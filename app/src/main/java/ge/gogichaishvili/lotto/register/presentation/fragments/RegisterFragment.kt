package ge.gogichaishvili.lotto.register.presentation.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentRegisterBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    private lateinit var uri: Uri
    private var filePath = ""

    private var userPhotoLink: String? = null

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

        binding.galleryBtn.setOnClickListener {
            folderCheckPermission()
        }

        binding.cameraBtn.setOnClickListener {
            cameraCheckPermission()
        }

        binding.tvBack.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

        getRegisterUsersCount()

    }

    private fun getRegisterUsersCount() {
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

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        if (binding.progressBar.isVisible) {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun cameraCheckPermission() {
        val readPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES else android.Manifest.permission.READ_EXTERNAL_STORAGE
        Dexter.withContext(requireContext())
            .withPermissions(
                android.Manifest.permission.CAMERA,
                readPermission
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            camera()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Toast.makeText(requireContext(), it.name, Toast.LENGTH_SHORT).show()
            }
            .check()
    }

    private fun camera() {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile.also {
                filePath = it.absolutePath
            }
        )

        takePicture.launch(uri)
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
            if (isSaved) {

                //uploadImage(uri)
                binding.progressbarPhoto.visibility = View.VISIBLE

                Glide.with(requireContext())
                    .asBitmap()
                    .load(uri)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Bitmap>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            //progressbarGallery.visibility = View.INVISIBLE
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            //progressbarGallery.visibility = View.INVISIBLE
                            return false
                        }
                    })
                    .into(binding.pictureIV)

                try { //save image
                    uri.let {
                        val source =
                            ImageDecoder.createSource(requireActivity().contentResolver, uri)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        resizeImageAndUpload(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.progressbarPhoto.visibility = View.INVISIBLE
                }

            }
        }

    private fun folderCheckPermission() {
        val readImagePermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES else android.Manifest.permission.READ_EXTERNAL_STORAGE
        Dexter.withContext(requireContext())
            .withPermission(readImagePermission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    gallery()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) { /* ... */
                }
            }).check()
    }

    private fun gallery() {
        pickPicture.launch("image/*")
    }

    private var pickPicture =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            //uploadImage(uri)
            binding.progressbarPhoto.visibility = View.VISIBLE

            Glide.with(requireContext())
                .asBitmap()
                .load(uri)
                .listener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //progressbarGallery.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //progressbarGallery.visibility = View.INVISIBLE
                        return false
                    }
                })
                .into(binding.pictureIV)

            try {  //get bitmap from uri
                uri?.let {
                    val source =
                        ImageDecoder.createSource(requireActivity().contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    resizeImageAndUpload(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.progressbarPhoto.visibility = View.INVISIBLE
            }

        }

    private fun uploadImage(uri: Uri?) {
        uri?.let {
            val ref = FirebaseStorage.getInstance().reference.child(
                "image/" + UUID.randomUUID().toString()
            )
            ref.putFile(it)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Uploaded", Toast.LENGTH_SHORT).show()
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        Toast.makeText(
                            requireContext(),
                            "Download link: $downloadUri",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                }
        } ?: run {
            Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resizeImageAndUpload(originalBitmap: Bitmap) {
        val resizedBitmap = resizeBitmap(originalBitmap, 500)
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val storageRef =
            FirebaseStorage.getInstance().reference.child("image/" + UUID.randomUUID().toString())
        //progressbar.visibility = View.VISIBLE
        storageRef.putBytes(image)
            .addOnCompleteListener { uploadTask ->
                //progressbar.visibility = View.INVISIBLE
                binding.progressbarPhoto.visibility = View.INVISIBLE
                if (uploadTask.isSuccessful) {
                    storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        urlTask.result?.let { uri ->
                            //Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT) .show()
                            userPhotoLink = uri.toString()
                        }
                    }
                } else {
                    uploadTask.exception?.let {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress =
                    (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            }
    }

    private fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
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

}