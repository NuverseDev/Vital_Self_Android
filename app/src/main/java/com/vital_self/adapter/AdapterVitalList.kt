import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.vital_self.R
import com.vital_self.databinding.ItemMeasurementBinding
import com.vital_self.model.Model

class AdapterVitalList(private val items: ArrayList<Model.VitalsData>, val context: Context) :
    RecyclerView.Adapter<AdapterVitalList.HealthStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthStatusViewHolder {
        val binding = ItemMeasurementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return HealthStatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HealthStatusViewHolder, position: Int) {
        val item = items[position]
        val value = item.vitalValue
        holder.bind(item)
//        if (value?.isEmpty() == true || value?.isBlank() == true || value?.contains("N.A") == true){
//            holder.itemView.visibility = View.GONE
//            // Optional: to avoid occupying space
//            val layoutParams = holder.itemView.layoutParams
//            layoutParams.height = 0
//            holder.itemView.layoutParams = layoutParams
//        }else{
//            holder.itemView.visibility = View.VISIBLE
//        }

    }

    override fun getItemCount(): Int = items.size

    inner class HealthStatusViewHolder(private val binding: ItemMeasurementBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(healthStatus: Model.VitalsData) {

            binding.vitalName.text = healthStatus.vitalName
           val value =  when(healthStatus.vitalValue){
                "HIGH" -> "High"
                "LOW" -> "Low"
                "MEDIUM" -> "Medium"
               "NORMAL" -> "Normal"
               else -> healthStatus.vitalValue
            }
            Log.d("TAG", "bind: name ${healthStatus.vitalName} value ${healthStatus.vitalValue}")

            binding.vitalValue.text = value
            binding.vitalIcon.setImageDrawable(healthStatus.vitalIcon?.let {
                AppCompatResources.getDrawable(context,
                    it
                )
            })

            when (healthStatus.emojiStatus) {
                Constant.GOOD -> {
                    binding.emojiStatus.setImageResource(R.drawable.ic_face_normal)
                    binding.emojiStatus.visibility = View.VISIBLE
                }
                Constant.NORMAL_GOOD -> {
                    binding.emojiStatus.setImageResource(R.drawable.ic_face_nutral)
                    binding.emojiStatus.visibility = View.VISIBLE
                }
                Constant.NOT_GOOD -> {
                    binding.emojiStatus.setImageResource(R.drawable.ic_face_high)
                    binding.emojiStatus.visibility = View.VISIBLE
                }
                Constant.BAD -> {
                    binding.emojiStatus.setImageResource(R.drawable.ic_face_very_high)
                    binding.emojiStatus.visibility = View.VISIBLE
                }
                else -> binding.emojiStatus.visibility = View.GONE
            }

            when (healthStatus.confidenceLevel) {
                "LOW" -> binding.insideConfidence.setCardBackgroundColor(context.getColor(R.color.low_dot_color))
                "MEDIUM" -> binding.insideConfidence.setCardBackgroundColor(context.getColor(R.color.medium_dot_color))
                "HIGH" -> binding.insideConfidence.setCardBackgroundColor(context.getColor(R.color.good_dot_color))
                else -> binding.insideConfidence.visibility = View.GONE  // Hide the confidence indicator when no level is set
            }
            binding.vitalConfidence.apply {
                visibility = if (healthStatus.confidenceLevel != null) View.VISIBLE else View.GONE
                binding.insideConfidence.visibility = visibility
                binding.confidenceIndicator.visibility = visibility
                text = healthStatus.confidenceLevel?.lowercase()?.let { "Confidence Level: $it" }
            }
            if (healthStatus.vitalUnit != null){
                binding.vitalUnit.visibility = View.VISIBLE
                binding.vitalUnit.text = healthStatus.vitalUnit
            }else{
                binding.vitalUnit.visibility = View.GONE
            }

            binding.vitalStatus.apply {
                text = when {
                    healthStatus.vitalName.contains("ASCVD") -> "Your ${healthStatus.vitalName} is ${healthStatus.vitalValue}"
                    healthStatus.vitalName.contains("Heart age") -> "Your ${healthStatus.vitalName} is ${healthStatus.vitalValue} Years"
                    else -> when (healthStatus.vitalStatus) {
                        Constant.HIGH -> "Your ${healthStatus.vitalName} is High"
                        Constant.NORMAL -> "Your ${healthStatus.vitalName} is Normal"
                        Constant.LOW -> "Your ${healthStatus.vitalName} is Low"
                        Constant.MEDIUM -> "Your ${healthStatus.vitalName} is Medium"
                        Constant.UNKNOWN -> " "
                        else -> ""
                    }
                }
                visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
            }


            binding.vitalDescription.text = healthStatus.vitalDetail
            // Toggle visibility based on expansion state
            binding.descContainer.visibility = if (healthStatus.isExpanded == true) View.VISIBLE else View.GONE
            binding.descContainer.visibility = if (healthStatus.isExpanded == true) View.VISIBLE else View.GONE
            binding.expandButton.setImageResource(
                if (healthStatus.isExpanded == true) R.drawable.ic_expand else R.drawable.ic_collapse
            )
            binding.viewMore.text = if (healthStatus.isExpanded == true) "view less" else "view more"
            // Set click listener to expand/collapse
            binding.expandButton.setOnClickListener {
                Log.d("TAG", "bind: vital name ${healthStatus.vitalName}  status ${healthStatus.vitalStatus} ")
                healthStatus.isExpanded = !healthStatus.isExpanded!!
                notifyItemChanged(adapterPosition)
            }
            binding.viewMore.setOnClickListener {
                healthStatus.isExpanded = !healthStatus.isExpanded!!
                notifyItemChanged(adapterPosition)
            }

            binding.contentLayout.setOnClickListener {
//                showProductDetailsDialog(context,healthStatus)
            }
        }

    }

}