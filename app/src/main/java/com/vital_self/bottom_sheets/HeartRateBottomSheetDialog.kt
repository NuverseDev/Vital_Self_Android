package com.vital_self.bottom_sheets

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.vital_self.R
import com.vital_self.databinding.HeartRateBottomSheetDialogBinding
import com.vital_self.model.Model.VitalsData

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Bottom Sheet Dialog Fragment to display vitals information
 */
class VitalsBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: HeartRateBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var vitalsData: VitalsData

    companion object {
        private const val ARG_VITALS_DATA = "arg_vitals_data"


        fun newInstance(vitalsData: VitalsData): VitalsBottomSheetDialog {
            val fragment = VitalsBottomSheetDialog()
            val args = Bundle()

            // Handle different Android API levels for Parcelable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.putParcelable(ARG_VITALS_DATA, vitalsData)

            } else {
                @Suppress("DEPRECATION")
                args.putParcelable(ARG_VITALS_DATA, vitalsData)

            }

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HeartRateBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get vitals data from arguments based on API level
        vitalsData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_VITALS_DATA, VitalsData::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_VITALS_DATA)
        } ?: throw IllegalArgumentException("VitalsData must be provided")


        binding.lifecycleOwner = viewLifecycleOwner

        setupUIForVitalType()

        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }



    private fun setupUIForVitalType() {
        Log.d("TAG", "setupUIForVitalType: status ${vitalsData.vitalStatus} ")
        val status = when(vitalsData.vitalStatus){
            Constant.HIGH -> "High"
            Constant.NORMAL -> "Normal"
            Constant.LOW -> "Low"
            Constant.MEDIUM -> "Medium"
            else -> ""
        }
        val typeface = ResourcesCompat.getFont(requireActivity(), R.font.inter_bold)
        when(vitalsData.emojiStatus){
            Constant.GOOD -> {
                binding.textVeryLow.typeface = typeface
                binding.imgEmoji.setImageDrawable(AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_face_normal))
            }
            Constant.NORMAL_GOOD -> {
                binding.textLow.typeface = typeface
                binding.imgEmoji.setImageDrawable(AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_face_nutral))
            }
            Constant.NOT_GOOD -> {
                binding.textHigh.typeface = typeface
                binding.imgEmoji.setImageDrawable(AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_face_high))
            }
            Constant.BAD -> {
                binding.textVeryHigh.typeface = typeface
                binding.imgEmoji.setImageDrawable(AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_face_very_high))
            }
            else -> {
                binding.textVeryHigh.typeface = typeface
                binding.imgEmoji.visibility = View.GONE
            }
        }
        if (vitalsData.vitalUnit.isNullOrEmpty()){
            if (vitalsData.vitalName.contains("ASCVD")){
                binding.tvVitalName.text = "Your ${vitalsData.vitalName} is ${vitalsData.vitalValue}"
            }else{
                binding.tvVitalName.text = "Your ${vitalsData.vitalName} is ${status}"
            }
        }else{
            if ( vitalsData.vitalName.contains("Heart age")){
                binding.tvVitalName.text = "Your ${vitalsData.vitalName} is ${vitalsData.vitalValue} Years"
            }else{
                binding.tvVitalName.text = "Your ${vitalsData.vitalName} \n  ${vitalsData.vitalValue} ${vitalsData.vitalUnit} - ${status}"
            }

        }

        Log.d("TAG", "setupUIForVitalType: ${vitalsData.vitalDetail} ")
        binding.tvAdditionalInfo.text = vitalsData.vitalDetail

        when(vitalsData.dialogType.toString()){
                DIALOG_TYPE.BASIC.name -> {
                    binding.progressContainer.visibility = View.GONE
                    binding.emojisContainer.visibility = View.GONE
                }
                DIALOG_TYPE.RANGE.name -> {
                    binding.progressContainer.visibility = View.VISIBLE
                    binding.emojisContainer.visibility = View.GONE
                    setRangeLayout(vitalsData.range!!)

                }
                DIALOG_TYPE.STATE.name -> {
                    setEmojisLayout(vitalsData.no_of_state!!)
                    binding.progressContainer.visibility = View.GONE
                    binding.emojisContainer.visibility = View.VISIBLE
                }
            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRangeLayout(range: ArrayList<String>) {
        if (range.size >= 4) {
            setTextViewVisibility(binding.tvLowRange, range[0])
            setTextViewVisibility(binding.tvNormalLowRange, range[1])
            setTextViewVisibility(binding.tvNormalHighRange, range[2])
            setTextViewVisibility(binding.tvHighRange, range[3])
        }
    }

    private fun setTextViewVisibility(textView: TextView, value: String) {
        if (value.isBlank()) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = value
        }
    }

    private fun setEmojisLayout(noState:Int){
        when(noState){
            2 -> {
                binding.emojiVeryLow.visibility = View.VISIBLE
                binding.textVeryLow.visibility = View.VISIBLE
                binding.emojiVeryHigh.visibility = View.VISIBLE
                binding.textVeryHigh.visibility = View.VISIBLE

            }
            3 -> {
                binding.emojiVeryLow.visibility = View.VISIBLE
                binding.textVeryLow.visibility = View.VISIBLE
                binding.emojiVeryHigh.visibility = View.VISIBLE
                binding.textVeryHigh.visibility = View.VISIBLE
                binding.emojiHigh.visibility = View.VISIBLE
                binding.textHigh.text = "Medium"
                binding.textHigh.visibility = View.VISIBLE
            }
            5 ->{
                binding.emojiVeryLow.visibility = View.VISIBLE
                binding.textVeryLow.visibility = View.VISIBLE
                binding.emojiVeryHigh.visibility = View.VISIBLE
                binding.textVeryHigh.visibility = View.VISIBLE
                binding.emojiHigh.visibility = View.VISIBLE
                binding.textHigh.visibility = View.VISIBLE
                binding.emojiLow.visibility = View.VISIBLE
                binding.textLow.visibility = View.VISIBLE
                binding.emojiMedium.visibility = View.VISIBLE
                binding.textMedium.visibility = View.VISIBLE

            }
            else -> {}
        }
    }
}
