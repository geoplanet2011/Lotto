package ge.gogichaishvili.lotto.main.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.gogichaishvili.lotto.databinding.FragmentDashboardBinding
import ge.gogichaishvili.lotto.main.enums.RoomSateEnums
import ge.gogichaishvili.lotto.main.models.Room

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var databaseReferenceForRooms: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Users")
        databaseReferenceForRooms = database?.reference!!.child("Rooms")

        //loadProfile()

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
        }


        binding.profileBtn.setOnClickListener {

        }

        binding.sendBtn.setOnClickListener {
           // sendData()
        }

        //getData ()
    }


    private fun sendData () {
        databaseReferenceForRooms!!.child("room1").setValue(Room("otaxi1", true, "123", RoomSateEnums.OPEN))
    }


    private fun getData() {
        databaseReferenceForRooms!!.addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val sb = StringBuilder()
                    for (i in p0.children) {
                        val name = i.child("name")
                        val status = i.child("status")
                        sb.append("${i.key} $name $status")
                    }

                    binding.answerTv.text = sb
                }
            }
        )
    }


    private fun loadProfile() {
        val user = auth.currentUser
        val userReference = databaseReference?.child(user?.uid!!)
        binding.emailTv.text = user?.email
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                binding.firstNameTv.text = p0.child("firstname").value.toString()

                Glide.with(requireActivity())
                    .load(user?.photoUrl)
                    .into(binding.profileImg)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}