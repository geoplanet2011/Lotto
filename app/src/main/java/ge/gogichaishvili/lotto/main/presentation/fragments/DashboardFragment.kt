package ge.gogichaishvili.lotto.main.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.gogichaishvili.lotto.databinding.FragmentDashboardBinding
import ge.gogichaishvili.lotto.main.enums.RoomSateEnums
import ge.gogichaishvili.lotto.main.models.Room
import ge.gogichaishvili.lotto.main.models.User

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var roomId: String? = null
    private var playerId: String? = null
    private var opponentId: String? = null

    private var firebaseUser: FirebaseUser? = null

    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var databaseReferenceForRooms: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomId = arguments?.getString("roomId") ?: ""

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Users")
        databaseReferenceForRooms = database?.reference!!.child("Rooms")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
        }

        binding.sendBtn.setOnClickListener {
           sendData()
        }

        getData ()
    }


    private fun sendData () {
        databaseReferenceForRooms!!.child(roomId!!).setValue(Room("room8", true, "123", RoomSateEnums.OPEN))
    }

    private fun addPlayerToRoom(playerId: String) {
        // მაგალითი: მოთამაშის დამატება ოთახში
        //roomRef.child("players").push().setValue(playerId)
    }

    private fun sendCommand(playerId: String, command: String) {
        //roomRef.child("commands").child(playerId).setValue(command)
    }

    private fun waitForOpponent() {
        // მაგალითი: ლოდინი სხვა მოთამაშეზე
      /*  roomRef.child("players").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount == 2) {
                    // ორი მოთამაშე არის, თამაში იწყება
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // შეცდომის დამუხტვა
            }
        })*/
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
                        //val status = i.child("status")
                        sb.append("${i.key} $name")
                    }

                    binding.tvAnswer.text = sb

                    //val user  = p0.getValue(User::class.java)
                }
            }
        )
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message

        databaseReference?.child("Chat")?.push()?.setValue(hashMap)

    }

    @SuppressLint("SuspiciousIndentation")
    private fun readMessage(senderId: String, receiverId: String) {
      val reference = FirebaseDatabase.getInstance().getReference("Chat")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(User::class.java)
                    val chatList = ArrayList<User>()
                    chatList.add(chat!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadProfile() {
        val user = auth.currentUser
        val userReference = databaseReference?.child(user?.uid!!)
        //binding.emailTv.text = user?.email
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                //binding.firstNameTv.text = p0.child("firstname").value.toString()

                /*Glide.with(requireActivity())
                    .load(user?.photoUrl)
                    .into(binding.profileImg)*/
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