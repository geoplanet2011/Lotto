package ge.gogichaishvili.lotto.main.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.hideKeyboard
import ge.gogichaishvili.lotto.databinding.FragmentCreateRoomBinding
import ge.gogichaishvili.lotto.main.enums.PlayerStatusEnum
import ge.gogichaishvili.lotto.main.enums.RoomSateEnums
import ge.gogichaishvili.lotto.main.models.Room
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.CreateRoomViewModel

class CreateRoomFragment : BaseFragment<CreateRoomViewModel>(CreateRoomViewModel::class) {

    private var _binding: FragmentCreateRoomBinding? = null
    private val binding get() = _binding!!

    private var balance: String? = null

    interface MoneyCheckCallback {
        fun onCheckSuccess()
        fun onCheckFailure()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etCoin.inputType =
            InputType.TYPE_CLASS_NUMBER

        binding.passwordSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.passwordTextInputLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) binding.etPassword.text?.clear()
        }

        binding.coinSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.coinTextInputLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.tvBalance.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.tvBalanceTitle.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) binding.etCoin.text?.clear()
            showUserBalance()
        }

        binding.createRoomButton.setOnClickListener {
            try {
                createRoom()
            } catch (e: Exception) {
                println(e.message.toString())
            }
        }

        binding.backButton.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

        binding.tvBack.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

        binding.etCoin.doAfterTextChanged {
            mViewModel.onTextChanged(it.toString())
        }

    }

    private fun createRoom() {

        if (binding.etRoomName.text.toString().isNotEmpty()) {

            showLoader()
            hideKeyboard()

            checkUserMoney(object : MoneyCheckCallback {
                override fun onCheckSuccess() {
                    val roomName = binding.etRoomName.text.toString().trim()
                    val roomPassword = binding.etPassword.text.toString().trim()
                    val isLocked = binding.passwordSwitch.isChecked
                    val money = binding.etCoin.text.toString().trim().ifEmpty { "0" }
                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val initialCommands = mapOf("commandKey" to "command1Value")

                    val reference = FirebaseDatabase.getInstance().getReference("Rooms")
                    reference.orderByChild("name").equalTo(roomName)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    hideLoader()
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.room_exist),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {

                                    val room = Room(
                                        roomName,
                                        isLocked,
                                        roomPassword,
                                        RoomSateEnums.OPEN,
                                        money,
                                        mutableListOf(currentUserUid),
                                        initialCommands
                                    )

                                    reference.child(roomName).setValue(room)
                                        .addOnCompleteListener { task ->
                                            hideLoader()
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.room_created),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                /*if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                                                    requireActivity().supportFragmentManager.popBackStackImmediate()
                                                }*/
                                                navigateToDashboard(roomName, money)
                                            } else {
                                                val errorMessage =
                                                    task.exception?.message
                                                        ?: getString(R.string.error)
                                                Toast.makeText(
                                                    requireContext(),
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                hideLoader()
                            }
                        })
                }

                override fun onCheckFailure() {
                    hideLoader()
                }
            })

        } else {
            Toast.makeText(requireContext(), R.string.input_required, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUserMoney(callback: MoneyCheckCallback) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        userUid?.let { uid ->
            databaseReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userMoney = snapshot.child("coin").value?.toString()
                        val coin = binding.etCoin.text.toString().trim().ifEmpty { "0" }

                        if (!userMoney.isNullOrEmpty() && userMoney.toLong() >= coin.toLong()) {
                            callback.onCheckSuccess()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.no_money),
                                Toast.LENGTH_SHORT
                            ).show()
                            callback.onCheckFailure()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(requireContext(), databaseError.message, Toast.LENGTH_SHORT)
                            .show()
                        callback.onCheckFailure()
                    }
                })
        } ?: callback.onCheckFailure()
    }

    private fun showUserBalance() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        userUid?.let { uid ->
            databaseReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.tvBalance.text =
                            "${snapshot.child("coin").value?.toString()}"
                        balance = snapshot.child("coin").value?.toString()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        if (binding.progressBar.isVisible) {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    fun navigateToDashboard(roomName: String, bet: String) {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString("roomId", roomName)
                    putInt("playerStatus", PlayerStatusEnum.CREATOR.value)
                    putString("bet", bet)
                }
            })
            addToBackStack(null)
            commit()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindObservers() {
        mViewModel.inputText.observe(this, Observer { inputText ->
            val currentTextValue = balance?.toLongOrNull() ?: 0
            val editTextValue = inputText.toLongOrNull() ?: 0
            val result = currentTextValue - editTextValue
            binding.tvBalance.text = result.toString()
        })
    }

}