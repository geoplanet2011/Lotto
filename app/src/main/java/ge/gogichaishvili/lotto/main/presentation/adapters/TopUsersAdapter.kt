package ge.gogichaishvili.lotto.main.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.gogichaishvili.lotto.R
import ge.gogichaishvili.lotto.databinding.ViewUserItemBinding
import ge.gogichaishvili.lotto.main.models.OnlineUser

class TopUsersAdapter(private val context: Context, private val userList: List<OnlineUser>) :
    RecyclerView.Adapter<TopUsersAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding =
            ViewUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val user = userList[position]
        with(holder.binding) {
            tvNumber.text = "${position + 1}"
            tvName.text = user.firstname
            tvMoney.text = user.coin.toString()
            Glide.with(context)
                .load(user.photo)
                .placeholder(R.drawable.male)
                .error(R.drawable.male)
                .into(ivPlayer)
        }
    }

    override fun getItemCount(): Int = userList.size

    class RoomViewHolder(val binding: ViewUserItemBinding) : RecyclerView.ViewHolder(binding.root)

}
