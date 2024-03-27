package ge.gogichaishvili.lotto.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.gogichaishvili.lotto.databinding.FragmentTopBinding
import ge.gogichaishvili.lotto.main.models.OnlineUser
import ge.gogichaishvili.lotto.main.presentation.adapters.TopUsersAdapter
import ge.gogichaishvili.lotto.main.presentation.viewmodels.TopUsersViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTopBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TopUsersViewModel by viewModel()

    private var userList: MutableList<OnlineUser> = ArrayList()
    private lateinit var adapter: TopUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.peekHeight = 0
            behavior.isHideable = false
            behavior.isDraggable = false

        }
        return bottomSheetDialog

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rootView.updateLayoutParams<FrameLayout.LayoutParams> {
            val h = (resources.displayMetrics.heightPixels * 0.90).toInt()
            height = h
        }

        bindObservers()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.rvUsers.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        readUsers()
    }

    private fun readUsers() {
        viewModel.setLoading(true)
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        val query = usersRef.orderByChild("coin").limitToLast(10)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val topUsers = mutableListOf<OnlineUser>()
                dataSnapshot.children.forEach { childSnapshot ->
                    val user = childSnapshot.getValue(OnlineUser::class.java)
                    user?.let { topUsers.add(it) }
                }
                topUsers.reverse()
                adapter = TopUsersAdapter(requireContext(), topUsers)
                binding.rvUsers.adapter = adapter
                viewModel.setLoading(false)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun bindObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isAdded) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

}
