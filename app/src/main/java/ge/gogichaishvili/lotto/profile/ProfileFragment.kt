package ge.gogichaishvili.lotto.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
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
import ge.gogichaishvili.lotto.main.presentation.activities.MainActivity
import ge.gogichaishvili.lotto.main.presentation.fragments.TopFragment
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

    private var mRewardedAd: RewardedAd? = null

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
        loadRewardedAd()

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

        binding.deleteBtn.setOnClickListener {
            deleteUser()
        }

        binding.getCoinBtn.setOnClickListener {
            showRewardedAd()
        }

        binding.ratingBtn.setOnClickListener {
            TopFragment()
                .apply {
                    isCancelable = false
                }
                .show(parentFragmentManager, null)
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
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)

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

                            binding.tvName.text = p0.child("firstname").value.toString()
                            binding.tvMoney.text = p0.child("coin").value.toString()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}
                })
        }
    }

    private fun deleteUserFromDatabase(uid: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(uid).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                deleteUserAccount()
            } else {
                Toast.makeText(context, "Failed to delete user data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUserAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signOutUser()
            } else {
                Toast.makeText(context, "Failed to delete user account.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
        navigateToSignInScreen()
    }

    private fun navigateToSignInScreen() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun deleteUser() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(getString(R.string.confirm_delete_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserUid != null) {
                    deleteUserFromDatabase(currentUserUid)
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()

        alert.setOnShowListener {
            alert.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD)
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD)
        }

        alert.show()
    }

    private fun loadRewardedAd() {

        try {
            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                requireContext(),
                "ca-app-pub-4290928451578259/9410286233",
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mRewardedAd = null
                        loadRewardedAd()
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        mRewardedAd = rewardedAd
                        mRewardedAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    mRewardedAd = null
                                    loadRewardedAd()
                                }

                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    mRewardedAd = null
                                    loadRewardedAd()
                                }

                                override fun onAdShowedFullScreenContent() {

                                }
                            }
                    }
                })
        } catch (e: Exception) {
            println(e.message.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd!!.show(requireActivity()) {
                updateUserCoin(it.amount.toLong())
                if (isAdded) {
                   Toast.makeText(
                       requireContext(),
                       getString(R.string.balance_updated),
                       Toast.LENGTH_SHORT
                   ).show()
               }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "The rewarded ad wasn't ready yet.",
                Toast.LENGTH_SHORT
            ).show()
            loadRewardedAd()
        }
    }

    private fun updateUserCoin(additionalBalance: Long) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userUid)

        userRef.child("coin").get().addOnSuccessListener { dataSnapshot ->
            val currentBalance = dataSnapshot.value.toString().toLongOrNull() ?: 0L
            val newBalance = currentBalance + additionalBalance

            userRef.child("coin").setValue(newBalance.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Coin value updated successfully to $newBalance.")
                    if (isAdded) {
                        binding.tvMoney.text = newBalance.toString()
                    }
                } else {
                    Log.e("Firebase", "Failed to update coin value.", task.exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error getting current coin value", exception)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}