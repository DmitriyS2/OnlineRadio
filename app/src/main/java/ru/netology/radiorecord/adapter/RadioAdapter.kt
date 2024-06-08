package ru.netology.radiorecord.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.radiorecord.R
import ru.netology.radiorecord.databinding.ItemRadioBinding
import ru.netology.radiorecord.dto.Station

interface Listener {
    fun highlight(station: Station)
}

class RadioAdapter(private val listener: Listener) :
    ListAdapter<Station, RadioAdapter.RadioHolder>(RadioDiffUtil()) {

    class RadioHolder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view) {
        private val binding = ItemRadioBinding.bind(view)

        fun bind(item: Station) = with(binding) {
            nameRadio.text = item.title
            nameRadio.setTextColor(if (item.isChecked) (itemView.context.getColor(R.color.orange)) else Color.BLACK)

            Glide.with(imageRadio)
                .load(item.icon_fill_colored)
                .into(imageRadio)

            cardView1.background
                .setTint(if (item.isChecked) (itemView.context.getColor(R.color.orange)) else Color.WHITE)

            cardView2.setOnClickListener {
                listener.highlight(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio, parent, false)
        return RadioHolder(view, listener)
    }

    override fun onBindViewHolder(holder: RadioHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RadioDiffUtil : DiffUtil.ItemCallback<Station>() {

    override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Station, newItem: Station): Any =
        Payload()

}

data class Payload(
    val id: Int? = null,
)