package com.vital_self.features.history.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vital_self.R
import com.vital_self.databinding.ItemHistoryListBinding
import com.vital_self.features.scan.presentation.scan.MeasurementResult
import com.vital_self.legacy.adapters.BindingViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class to hold both MeasurementResult and scanId
data class HistoryItem(
    val scanId: Int?,
    val measurementResult: MeasurementResult
)

class HistoryAdapter(
    private val historyListener: HistoryListener
) : RecyclerView.Adapter<BindingViewHolder<ItemHistoryListBinding>>() {

    private var items: List<HistoryItem> = emptyList()

    fun setItems(newItems: List<HistoryItem>) {
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
        val historyItem = items[position]
        val scanItem = historyItem.measurementResult
        holder.binding.tvDate.text = scanItem.date
        holder.binding.tvTime.text = formatTime(scanItem.time)
        holder.itemView.setOnClickListener {
            historyListener.onItemClicked(historyItem.scanId, scanItem)
        }
        holder.binding.imgArrowRight.setOnClickListener {
            historyListener.onQRGenerateClick(historyItem.scanId, scanItem)
        }
    }

    fun formatTime(inputTime: String): String {
        if (inputTime.isBlank()) return ""

        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format with AM/PM

        // Try different input formats
        val inputFormats = listOf(
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()),  // 24-hour with seconds
            SimpleDateFormat("HH:mm", Locale.getDefault()),     // 24-hour without seconds
            SimpleDateFormat("hh:mm:ss a", Locale.getDefault()), // 12-hour with seconds
            SimpleDateFormat("hh:mm a", Locale.getDefault())    // 12-hour without seconds
        )

        for (format in inputFormats) {
            try {
                val date = format.parse(inputTime)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                // Try next format
            }
        }

        return inputTime // Return original if no format matches
    }
}

interface HistoryListener {
    fun onQRGenerateClick(scanId: Int?, data: MeasurementResult)
    fun onItemClicked(scanId: Int?, data: MeasurementResult)
}