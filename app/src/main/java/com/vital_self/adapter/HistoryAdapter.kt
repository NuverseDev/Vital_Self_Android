package com.vital_self.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vital_self.R
import com.vital_self.databinding.ItemHistoryListBinding
import com.vital_self.view.scan.MeasurementResult
import com.vital_self.utils.BindingViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val historyListener: HistoryListener
) : RecyclerView.Adapter<BindingViewHolder<ItemHistoryListBinding>>() {

    private var items: List<MeasurementResult> = emptyList()

    fun setItems(newItems: List<MeasurementResult>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemHistoryListBinding> {
        return BindingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_list, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemHistoryListBinding>, position: Int
    ) {
        val scanItem = items[position]
        holder.binding.tvDate.text = scanItem.date
        holder.binding.tvTime.text = formatTime(scanItem.time)
        holder.itemView.setOnClickListener {
            historyListener.onItemClicked(scanItem)
        }
        holder.binding.imgArrowRight.setOnClickListener {
            historyListener.onQRGenerateClick(scanItem)
        }
    }

    fun formatTime(inputTime: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // 24-hour format
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format with AM/PM

        val date: Date? = inputFormat.parse(inputTime)  // Convert to Date
        return if (date != null) outputFormat.format(date) else "Invalid Time"
    }
}

interface HistoryListener {
    fun onQRGenerateClick(data: MeasurementResult)
    fun onItemClicked(data: MeasurementResult)
}