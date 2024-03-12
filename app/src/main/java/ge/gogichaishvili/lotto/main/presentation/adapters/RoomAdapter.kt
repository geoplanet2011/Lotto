package ge.gogichaishvili.lotto.main.presentation.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.ViewRoomItemBinding
import ge.gogichaishvili.lotto.main.models.Room
import ge.gogichaishvili.lotto.main.presentation.fragments.DashboardFragment

class RoomAdapter(private val context: Context, private val roomList: List<Room>) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding =
            ViewRoomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        with(holder.binding) {
            tvName.text = room.name
            passwordLogo.visibility =
                if (room.isLocked == true) View.VISIBLE else View.GONE

            root.setOnCreateContextMenuListener { contextMenu, _, _ ->
                contextMenu.add(position, 0, 0, context.getString(R.string.delete))
                contextMenu.add(position, 1, 0, context.getString(R.string.edit))
            }

            root.setOnClickListener {
                if (room.isLocked == true) {
                    alertWithInputText(context, room.password!!, room.name!!)
                } else {
                    navigateToDashboardFragment(room.name)
                }
            }
        }
    }

    override fun getItemCount(): Int = roomList.size

    class RoomViewHolder(val binding: ViewRoomItemBinding) : RecyclerView.ViewHolder(binding.root)

    private fun alertWithInputText(context: Context, roomPassword: String, roomName: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.notification))
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)

        val input = EditText(context).apply {
            hint = context.getString(R.string.enter_the_password)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        builder.setView(input)

        builder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
            val text = input.text.toString().trim()
            if (text == roomPassword.trim()) {
                navigateToDashboardFragment(roomName)

            } else {
                MediaPlayer.create(context, R.raw.wrong)?.start()
                Toast.makeText(
                    context,
                    context.getString(R.string.wrong_password),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }


    private fun navigateToDashboardFragment(roomName: String?) {
        roomName?.let {
            (context as? FragmentActivity)?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragmentContainerView, DashboardFragment().apply {
                    arguments = Bundle().apply { putString("roomId", it) }
                })
                addToBackStack(null)
                commit()
            }
        }
    }
}
