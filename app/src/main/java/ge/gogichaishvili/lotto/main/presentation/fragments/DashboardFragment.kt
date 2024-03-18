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
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.FragmentDashboardBinding
import ge.gogichaishvili.lotto.main.enums.PlayerStatusEnum
import ge.gogichaishvili.lotto.main.enums.RoomSateEnums
import ge.gogichaishvili.lotto.main.models.Room
import ge.gogichaishvili.lotto.main.models.User

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var roomId: String? = null
    private var playerStatus: PlayerStatusEnum? = null

    private var firebaseUser: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var databaseReferenceForRooms: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var userReference: DatabaseReference? = null
    private var uid: String? = null

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
        val playerStatusCode = arguments?.getInt("playerStatus") ?: PlayerStatusEnum.UNKNOWN.value
        playerStatus = PlayerStatusEnum.getEnumByCode(playerStatusCode)

        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Users")
        databaseReferenceForRooms = database?.reference!!.child("Rooms")
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser
        uid = firebaseUser?.uid!!
        userReference = databaseReference?.child(uid!!)

        if (playerStatus == PlayerStatusEnum.JOINER) {
            addPlayerToRoom(uid!!)
        }

        loadProfile()
        waitForOpponent()

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
        }

        binding.sendBtn.setOnClickListener {
           sendData()
        }

        getData ()
    }


    private fun sendData () {
       // databaseReferenceForRooms!!.child(roomId!!).setValue(Room("room8", true, "123", RoomSateEnums.OPEN))
    }

    private fun sendCommand(playerId: String, command: String) {
        //roomRef.child("commands").child(playerId).setValue(command)
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

                    //binding.tvAnswer.text = sb
                    //val user  = p0.getValue(User::class.java)
                }
            }
        )
    }



    private fun sendChatMessage(senderId: String, receiverId: String, message: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        databaseReference?.child("Chat")?.push()?.setValue(hashMap)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun readChatMessage(senderId: String, receiverId: String) {
      val reference = FirebaseDatabase.getInstance().getReference("Chat")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(User::class.java)
                    val chatList = ArrayList<User>()
                    chatList.add(chat!!)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addPlayerToRoom(playerId: String) {
        val playersRef = databaseReferenceForRooms?.child(roomId!!)?.child("players")

        playersRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val playersCount = dataSnapshot.childrenCount.toInt()
                val newPlayerIndex = playersCount + 1
                playersRef.child(newPlayerIndex.toString()).setValue(playerId)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun waitForOpponent() {
        databaseReferenceForRooms?.child(roomId!!)?.child("players")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val players = dataSnapshot.children.mapNotNull { it.value as? String }
                if (players.size == 2) {
                    val otherPlayerId = players.firstOrNull { it != uid }
                    if (otherPlayerId != null) {
                        loadOpponentProfile(otherPlayerId)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) { }
        })
    }

    private fun loadOpponentProfile(otherPlayerId: String) {
        databaseReference?.child(otherPlayerId)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                Glide.with(requireActivity())
                    .load(p0.child("photo").value.toString())
                    .placeholder(R.drawable.male)
                    .error(R.drawable.male)
                    .into(binding.ivOpponent)

                binding.ivOpponent.visibility = View.VISIBLE
                binding.tvPlayerTwoName.text = p0.child("firstname").value.toString()
                binding.tvPlayerTwoScore.text = p0.child("coin").value.toString()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun loadProfile() {
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                Glide.with(requireActivity())
                    .load(p0.child("photo").value.toString())
                    .placeholder(R.drawable.male)
                    .error(R.drawable.male)
                    .into(binding.ivPlayer)

                binding.tvPlayerOneName.text = p0.child("firstname").value.toString()
                binding.tvPlayerOneScore.text = p0.child("coin").value.toString()
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