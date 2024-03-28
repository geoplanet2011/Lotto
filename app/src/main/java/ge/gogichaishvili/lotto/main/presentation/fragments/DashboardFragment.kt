package ge.gogichaishvili.lotto.main.presentation.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.app.tools.Utils
import ge.gogichaishvili.lotto.app.tools.koreqtulMicemitBrunvashiGadayvana
import ge.gogichaishvili.lotto.databinding.FragmentDashboardBinding
import ge.gogichaishvili.lotto.main.enums.GameOverStatusEnum
import ge.gogichaishvili.lotto.main.enums.PlayerStatusEnum
import ge.gogichaishvili.lotto.main.enums.RoomSateEnums
import ge.gogichaishvili.lotto.main.models.LottoDrawResult
import ge.gogichaishvili.lotto.main.models.User
import ge.gogichaishvili.lotto.main.presentation.fragments.base.BaseFragment
import ge.gogichaishvili.lotto.main.presentation.viewmodels.DashboardViewModel
import java.util.Timer
import java.util.TimerTask

class DashboardFragment : BaseFragment<DashboardViewModel>(DashboardViewModel::class) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var timer: Timer = Timer()

    private var roomId: String? = null
    private var playerStatus: PlayerStatusEnum? = null
    private var bet: Long? = null
    private var balance: Long? = null

    private var firebaseUser: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var databaseReferenceForRooms: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var userReference: DatabaseReference? = null
    private var uid: String? = null

    private var isDraw: Boolean = false
    private var isOpponentWin: Boolean = false
    private var isOpponentLineCompleted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        roomId = arguments?.getString("roomId") ?: ""
        val playerStatusCode = arguments?.getInt("playerStatus") ?: PlayerStatusEnum.UNKNOWN.value
        playerStatus = PlayerStatusEnum.getEnumByCode(playerStatusCode)
        bet = arguments?.getString("bet")?.toLongOrNull()

        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Users")
        databaseReferenceForRooms = database?.reference!!.child("Rooms")
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser
        uid = firebaseUser?.uid!!
        userReference = databaseReference?.child(uid!!)

        binding.llCards.post {
            mViewModel.generateCard(requireContext(), binding.llCards)
        }

        loadProfile()

        if (playerStatus == PlayerStatusEnum.JOINER) {
            addPlayerToRoom(uid!!)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            waitForOpponent()
        }, 2000)

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
                        sb.append("${i.key} $name")
                    }

                    val user = p0.getValue(User::class.java)
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

    private fun loadProfile() {
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (isAdded) {
                    Glide.with(requireActivity())
                        .load(p0.child("photo").value.toString())
                        .placeholder(R.drawable.male)
                        .error(R.drawable.male)
                        .into(binding.ivPlayer)

                    binding.tvPlayerOneName.text = p0.child("firstname").value.toString()
                    binding.tvPlayerOneScore.text = p0.child("coin").value.toString()
                    balance = p0.child("coin").value.toString().toLongOrNull()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun waitForOpponent() {
        databaseReferenceForRooms?.child(roomId!!)?.child("players")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (isAdded) {
                        val players = dataSnapshot.children.mapNotNull { it.value as? String }
                        if (players.size == 2) {
                            binding.progressBar.visibility = View.GONE
                            closeRoom()
                            val otherPlayerId = players.firstOrNull { it != uid }
                            if (otherPlayerId != null) {
                                loadOpponentProfile(otherPlayerId)
                                startGame()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun loadOpponentProfile(otherPlayerId: String) {
        databaseReference?.child(otherPlayerId)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (isAdded) {
                    Glide.with(requireActivity())
                        .load(p0.child("photo").value.toString())
                        .placeholder(R.drawable.male)
                        .error(R.drawable.male)
                        .into(binding.ivOpponent)

                    binding.ivOpponent.visibility = View.VISIBLE
                    binding.tvPlayerTwoName.text = p0.child("firstname").value.toString()
                    binding.tvPlayerTwoScore.text = p0.child("coin").value.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun startGame() {

        if (playerStatus == PlayerStatusEnum.CREATOR) {
            getLottoStones()
        }
        getLottoResultFromServer()
    }

    private fun getLottoStones() {
        val period = mViewModel.getGameSpeed()
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread(Runnable {
                    mViewModel.getNumberFromBag()
                })
            }

        }, 0, period)
    }

    private fun getLottoStonesFromServer() {
        databaseReferenceForRooms?.child(roomId!!)?.child("stones")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stonesType = object : GenericTypeIndicator<List<Int>>() {}
                    val stonesList: List<Int> = snapshot.getValue(stonesType) ?: emptyList()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getLottoResultFromServer() {
        databaseReferenceForRooms?.child(roomId!!)?.child("lottoResult")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.getValue(LottoDrawResult::class.java)
                    if (result != null) {
                        mViewModel.getNumberFromServer(result)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun handleLottoDrawResult(result: LottoDrawResult) {
        if (result.isEmpty) {
            println("bag is empty")
            sendCommand("Draw")
            finishRoom()
        } else {
            if (result.numbers.isNotEmpty()) {
                val newNumber = result.numbers.last()
                addLottoStoneButton(newNumber)
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun addLottoStoneButton(number: Int) {
        val lottoStone = Button(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(180, 180).apply {
                setMargins(0, 0, 0, 0)
            }
            setBackgroundResource(R.drawable.st)
            setPadding(0, 0, 0, 25)
            text = number.toString()
            textSize = 18f
            setTextColor(Color.rgb(112, 40, 31))
            visibility = View.INVISIBLE
        }
        if (binding.llStones.childCount == 3) {
            removeOldestLottoStone()
        }
        binding.llStones.addView(lottoStone, 0)
        animateLottoStoneAppearance(lottoStone)
        if (mViewModel.isSoundEnabled()) {
            playSoundForNumber(number)
        }
    }

    private fun removeOldestLottoStone() {
        val lastLottoNumberID = binding.llStones.childCount - 1
        val lastLottoNumberView = binding.llStones.getChildAt(lastLottoNumberID)
        lastLottoNumberView.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.scale_down
            ).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}
                    override fun onAnimationRepeat(p0: Animation?) {}
                    override fun onAnimationEnd(p0: Animation?) {
                        binding.llStones.removeView(lastLottoNumberView)
                    }
                })
            })
    }

    private fun animateLottoStoneAppearance(lottoStone: Button) {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    lottoStone.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {}
            })
        }
        lottoStone.startAnimation(animation)
    }

    @SuppressLint("DiscouragedApi")
    private fun playSoundForNumber(number: Int) {
        var soundFileName = ""
        when (mViewModel.getSelectedLanguage()) {
            "en" -> {
                soundFileName = "a$number"
            }

            "ru" -> {
                soundFileName = "r$number"
            }

            "ka" -> {
                soundFileName = "g$number"
            }
        }
        val resID = resources.getIdentifier(soundFileName, "raw", activity?.packageName)
        Utils.playSound(activity, resID)
    }

    private fun resetGame() {
        timer.cancel()
        timer.purge()
        mViewModel.resetManagers()
        //mViewModel.redrawCard(requireContext(), binding.llCards)
        binding.llStones.removeAllViews()
        //isDraw = false
        //isOpponentWin = false
        //isOpponentLineCompleted = false
    }

    override fun bindObservers() {

        mViewModel.dismissLiveData.observe(viewLifecycleOwner) {
            if (isAdded) {
                navigateBackToRoomList()
            }
        }

        mViewModel.requestStateStonesLiveData.observe(viewLifecycleOwner) {
            sendLottoDrawResultToOpponent(it)
        }

        mViewModel.requestStateLiveData.observe(viewLifecycleOwner) {
            handleLottoDrawResult(it)

            if (it.numbers.isNotEmpty()) {

                if (mViewModel.isHintEnabled()) {
                    mViewModel.lottoCardManager.setHints(it.numbers, binding.llCards)
                }

                val removedNumbers =
                    mViewModel.lottoCardManager.previousNumbers - it.numbers.toSet()

                mViewModel.lottoCardManager.setLoss(removedNumbers, binding.llCards)

                mViewModel.lottoCardManager.previousNumbers = it.numbers

            }

            checkOpponentGameCompletion()
        }

        mViewModel.lineCompletionEvent.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                getString(R.string.line_is_filled),
                Toast.LENGTH_SHORT
            ).show()
            if (playerStatus == PlayerStatusEnum.CREATOR) {
                sendCommand("Creator line is filled")
            } else {
                sendCommand("Joiner line is filled")
            }
        }

        mViewModel.cardCompletionEvent.observe(viewLifecycleOwner) {
            calculateNewBalance(GameOverStatusEnum.PLAYER_WIN)
            if (playerStatus == PlayerStatusEnum.CREATOR) {
                sendCommand("CREATOR_WIN")
            } else {
                sendCommand("JOINER_WIN")
            }
            mViewModel.checkGameResult(GameOverStatusEnum.PLAYER_WIN, requireContext())
            resetGame()
            finishRoom()
        }

    }

    private fun sendStoneNumberToOpponent(stoneNumbers: List<Int>) {
        databaseReferenceForRooms?.child(roomId!!)?.child("stones")?.setValue(stoneNumbers)
            ?.addOnSuccessListener {
                println("List Update successful")
            }?.addOnFailureListener { e ->
                println("List Update failed: ${e.message}")
            }
    }

    private fun sendLottoDrawResultToOpponent(result: LottoDrawResult) {
        databaseReferenceForRooms?.child(roomId!!)?.child("lottoResult")?.setValue(result)
            ?.addOnSuccessListener {
                println("LottoDrawResult update successful")
            }
            ?.addOnFailureListener { e ->
                println("LottoDrawResult update failed: ${e.message}")
            }
    }

    private fun sendCommand(command: String) {
        val updates = hashMapOf<String, Any>(
            "commandKey" to command
        )
        databaseReferenceForRooms?.child(roomId!!)?.child("commands")?.updateChildren(updates)
            ?.addOnSuccessListener {
            }?.addOnFailureListener {}
    }

    private fun checkOpponentGameCompletion() {
        databaseReferenceForRooms?.child(roomId!!)?.child("commands")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val commandKeyValue = snapshot.child("commandKey").value as? String
                        when {

                            commandKeyValue.equals("CREATOR_WIN") -> {
                                if (playerStatus == PlayerStatusEnum.JOINER && !isOpponentWin) {
                                    isOpponentWin = true
                                    calculateNewBalance(GameOverStatusEnum.OPPONENT_WIN)
                                    mViewModel.checkGameResult(
                                        GameOverStatusEnum.OPPONENT_WIN,
                                        requireContext()
                                    )
                                    resetGame()
                                    finishRoom()
                                }
                            }

                            commandKeyValue.equals("JOINER_WIN") -> {
                                if (playerStatus == PlayerStatusEnum.CREATOR && !isOpponentWin) {
                                    isOpponentWin = true
                                    calculateNewBalance(GameOverStatusEnum.OPPONENT_WIN)
                                    mViewModel.checkGameResult(
                                        GameOverStatusEnum.OPPONENT_WIN,
                                        requireContext()
                                    )
                                    resetGame()
                                    finishRoom()
                                }
                            }

                            commandKeyValue.equals("Creator line is filled") -> {
                                if (playerStatus == PlayerStatusEnum.JOINER && !isOpponentLineCompleted) {
                                    Toast.makeText(
                                        requireContext(),
                                        "${
                                            binding.tvPlayerTwoName.text.toString()
                                                .koreqtulMicemitBrunvashiGadayvana()
                                        } ${getString(R.string.opponent_line_is_filled)}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isOpponentLineCompleted = true
                                }
                            }

                            commandKeyValue.equals("Joiner line is filled") -> {
                                if (playerStatus == PlayerStatusEnum.CREATOR && !isOpponentLineCompleted) {
                                    Toast.makeText(
                                        requireContext(),
                                        "${
                                            binding.tvPlayerTwoName.text.toString()
                                                .koreqtulMicemitBrunvashiGadayvana()
                                        } ${getString(R.string.opponent_line_is_filled)}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isOpponentLineCompleted = true
                                }
                            }

                            commandKeyValue.equals("Draw") -> {
                                if (!isDraw) {
                                    isDraw = true
                                    calculateNewBalance(GameOverStatusEnum.Draw)
                                    mViewModel.checkGameResult(
                                        GameOverStatusEnum.Draw,
                                        requireContext()
                                    )
                                    resetGame()
                                    finishRoom()
                                }
                            }

                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun calculateNewBalance(gameOverStatus: GameOverStatusEnum) {

        if (bet != null) {

            when (gameOverStatus) {

                GameOverStatusEnum.PLAYER_WIN -> {
                    if (balance != null && bet != null) {
                        balance = balance!! + bet!! * 2
                    }
                }

                GameOverStatusEnum.OPPONENT_WIN -> {
                    balance = balance!! - bet!!
                }

                GameOverStatusEnum.Draw -> {
                    if (balance != null && bet != null) {
                        balance = balance!! + bet!!
                    }
                }

            }

            binding.tvPlayerOneScore.text = balance.toString()

            userReference?.child("coin")?.setValue(balance.toString())
                ?.addOnSuccessListener {
                }
                ?.addOnFailureListener {
                }
        }
    }

    private fun closeRoom() {
        databaseReferenceForRooms?.child(roomId!!)?.child("state")?.setValue(RoomSateEnums.CLOSE)
            ?.addOnSuccessListener {
            }
            ?.addOnFailureListener {
            }
    }

    private fun finishRoom() {
        databaseReferenceForRooms?.child(roomId!!)?.child("state")?.setValue(RoomSateEnums.FINISH)
            ?.addOnSuccessListener {
                deleteRoomIfFinished(roomId!!)
            }
            ?.addOnFailureListener {
            }
    }

    private fun deleteRoomIfFinished(roomId: String) {
        try {
            val roomRef = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId)
            roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val state = dataSnapshot.child("state").getValue(String::class.java)
                        if (state == RoomSateEnums.FINISH.name) {
                            roomRef.removeValue()
                                .addOnSuccessListener {}
                                .addOnFailureListener {}
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        } catch (e: Exception) {
            println(e.message.toString())
        }
    }


    private fun navigateBackToRoomList() {
        requireActivity().runOnUiThread {
            if (isAdded && context != null) {
                parentFragmentManager.popBackStackImmediate(RoomListFragment::class.java.name, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}