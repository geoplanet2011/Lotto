package ge.gogichaishvili.lotto.main.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentRoomListBinding
import ge.gogichaishvili.lotto.main.enums.RoomSateEnums
import ge.gogichaishvili.lotto.main.models.Room
import ge.gogichaishvili.lotto.main.presentation.adapters.RoomAdapter
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.RoomListViewModel

class RoomListFragment : BaseFragment<RoomListViewModel>(RoomListViewModel::class) {

    private var _binding: FragmentRoomListBinding? = null
    private val binding get() = _binding!!

    private var roomList: MutableList<Room> = ArrayList()
    private lateinit var adapter: RoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomListBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.roomListRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        readRooms()

        binding.addRoom.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    CreateRoomFragment()
                ).addToBackStack(
                    CreateRoomFragment::class.java.name
                ).commit()
        }

        binding.searchRoom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRooms(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tvBack.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }
        //onlineUserStatus("Online")

    }

    private fun readRooms() {
        mViewModel.setLoading(true)
        val reference = FirebaseDatabase.getInstance().getReference("Rooms")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (binding.searchRoom.text.toString().isEmpty()) {
                    roomList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val room = snapshot.getValue(Room::class.java)
                        if (room?.state == RoomSateEnums.OPEN) {
                            roomList.add(room)
                        }
                    }
                    adapter = RoomAdapter(requireContext(), roomList)
                    binding.roomListRv.adapter = adapter
                    mViewModel.setLoading(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                mViewModel.setLoading(false)
            }
        })
    }

    private fun searchRooms(s: String) {
        mViewModel.setLoading(true)
        val query = FirebaseDatabase.getInstance().getReference("Rooms").orderByChild("name").startAt(s).endAt("$s\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                roomList.clear()
                for (snapshot in dataSnapshot.children) {
                    val room = snapshot.getValue(Room::class.java)
                    if (room?.state == RoomSateEnums.OPEN) {
                        roomList.add(room)
                    }
                }

                adapter = RoomAdapter(requireContext(), roomList)
                binding.roomListRv.adapter = adapter
                mViewModel.setLoading(false)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                mViewModel.setLoading(false)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onlineUserStatus(status: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        databaseReference.updateChildren(hashMap)
    }

  /*  override fun onResume() {
        super.onResume()
        onlineUserStatus("Online")
    }

    override fun onPause() {
        super.onPause()
       onlineUserStatus("Offline")
    }*/

    override fun bindObservers() {
        super.bindObservers()

        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
           binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

}