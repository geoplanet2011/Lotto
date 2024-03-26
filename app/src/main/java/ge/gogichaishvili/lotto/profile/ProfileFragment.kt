package ge.gogichaishvili.lotto.profile

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
import ge.gogichaishvili.lotto.databinding.FragmentProfileBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    private lateinit var uri: Uri
    private var filePath = ""

    private var userPhotoLink: String = ""

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
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Users")

        loadProfile()

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

                if (isAdded) {
                    binding.progressbarPhoto.visibility = View.VISIBLE
                }

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
                    if (isAdded) {
                        binding.progressbarPhoto.visibility = View.INVISIBLE
                    }
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

            if (uri == null) {
                if (isAdded) {
                    binding.progressbarPhoto.visibility = View.INVISIBLE
                }
                return@registerForActivityResult
            }

            if (isAdded) {
                binding.progressbarPhoto.visibility = View.VISIBLE
            }

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
                        if (isAdded) {
                            binding.progressbarPhoto.visibility = View.INVISIBLE
                        }
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (isAdded) {
                            binding.progressbarPhoto.visibility = View.INVISIBLE
                        }
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
                if (isAdded) {
                    binding.progressbarPhoto.visibility = View.INVISIBLE
                }
            }

        }

    private fun resizeImageAndUpload(originalBitmap: Bitmap) {
        val resizedBitmap = resizeBitmap(originalBitmap, 500)
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val storageRef =
            FirebaseStorage.getInstance().reference.child("image/" + UUID.randomUUID().toString())
        storageRef.putBytes(image)
            .addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        urlTask.result?.let { uri ->
                            userPhotoLink = uri.toString()
                            updatePhotoInDatabase(userPhotoLink)
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

    private fun updatePhotoInDatabase(photoUrl: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userPhotoMap = mapOf("photo" to photoUrl)
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(user.uid).updateChildren(userPhotoMap)
                .addOnSuccessListener {
                    if (isAdded) {
                        binding.progressbarPhoto.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Profile photo updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .addOnFailureListener { e ->
                    if (isAdded) {
                        binding.progressbarPhoto.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Failed to update profile photo: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
        }
    }

    private fun loadProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseDatabase.getInstance().reference.child("Users").child(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (isAdded) {
                            Glide.with(requireActivity())
                                .load(p0.child("photo").value.toString())
                                .placeholder(R.drawable.male)
                                .error(R.drawable.male)
                                .into(binding.pictureIV)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}