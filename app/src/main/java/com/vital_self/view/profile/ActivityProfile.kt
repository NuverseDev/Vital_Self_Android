package com.vital_self.view.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.biosensesignal.sdk.api.session.user_info.SmokingStatus
import com.vital_self.R
import com.vital_self.base.BaseActivity
import com.vital_self.databinding.ActivityUserDetailBinding
import com.vital_self.model.Model
import com.vital_self.adapter.SpinnerAdapter
import com.vital_self.utils.AnimationsHandler
import com.vital_self.utils.Pref
import java.math.BigDecimal
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivityProfile : BaseActivity() {

    lateinit var binding: ActivityUserDetailBinding

    private var gender: String? = null

    private var isExpand = MutableLiveData<Boolean>()
    private var heightExpand = MutableLiveData<Boolean>()
    private var weightExpand = MutableLiveData<Boolean>()

    private var smoker_status: SmokingStatus = SmokingStatus.NON_SMOKER

//    private lateinit var dateRestrictionManager: ScanRestrictionManager

    private var selectedWeightUnit = "kg"
    private var selectedHeightUnit = "cm"

    private var subject: Model.SubjectDetails? = null

    private val title by lazy { intent.getStringExtra(TITLE) }

    companion object {
        const val TITLE = ""
        fun startActivity(activity: Activity,title:String) {
            Intent(activity, ActivityProfile::class.java).apply {
                putExtra(TITLE,title)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
        fun startActivityFresh(activity: Activity,title:String) {
            Intent(activity, ActivityProfile::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(TITLE,title)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        binding.tool.tvTitle.text = title
        // Default NO smoking selection
        binding.smokerRadioGroup.check(R.id.no_smoker_button)

        // Load saved subject data from Pref
        loadSubjectData()

        setListener()
        initUi()
        observer()
        setUpToolbar(binding.tool)
//        dateRestrictionManager = ScanRestrictionManager(this)
//        dateRestrictionManager.initialize()
    }

    override fun onResume() {
        super.onResume()
        binding.linearLayout.visibility = View.VISIBLE
        binding.buttonNext.visibility = View.VISIBLE
        binding.expiry.visibility = View.VISIBLE
    }

    private fun loadSubjectData() {
        // Load subject data from your existing Pref utility
        subject = Pref.subjectDetails
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun initUi() {
        // Default values for units if not already set
        if (selectedHeightUnit.isBlank()) selectedHeightUnit = "cm"
        if (selectedWeightUnit.isBlank()) selectedWeightUnit = "kg"

        // Set default to non-smoker
        binding.smokerRadioGroup.check(R.id.no_smoker_button)

        subject?.let {
            binding.etFirstName.setText(it.name)
            binding.etAgeName.setText(it.age.toString())

            // Set height and weight with the correct units
            val height = it.height
            val weight = it.weight

            // Set the units from saved data
            selectedHeightUnit = it.heightUnit ?: "cm"
            selectedWeightUnit = it.weightUnit ?: "kg"

            // Display values in current units
            if (selectedHeightUnit == "cm") {
                binding.etHeight.setText(height?.toInt().toString())
            } else { // ft
                binding.etHeight.setText(String.format("%.2f", height?.div(30.48)))
            }

            if (selectedWeightUnit == "kg") {
                binding.etWeight.setText(String.format("%.1f", weight))
            } else { // lb
                binding.etWeight.setText(String.format("%.1f", weight?.times(2.20462)))
            }

            // Set gender
            when (it.sex) {
                Sex.MALE -> binding.etGender.setText("Male")
                Sex.FEMALE -> binding.etGender.setText("Female")
                else -> binding.etGender.setText("")
            }

            // Set smoker status but ensure it defaults to NON_SMOKER
            smoker_status = it.isSmoker ?: SmokingStatus.NON_SMOKER
            when(smoker_status) {
                SmokingStatus.SMOKER -> binding.smokerRadioGroup.check(R.id.yes_smoker_button)
                else -> binding.smokerRadioGroup.check(R.id.no_smoker_button) // Both NON_SMOKER and UNSPECIFIED default to NO
            }
        }

        isExpand.value = false
        weightExpand.value = false
        heightExpand.value = false
        binding.buttonNext.apply {
            this.isEnabled = true
            setBackgroundResource(R.drawable.bg_primary_button)
        }

        // Set the dropdown unit displays
        binding.etHeightUnit.setText(selectedHeightUnit, false)
        binding.etWeightUnit.setText(selectedWeightUnit, false)
    }

    private fun observer() {
        isExpand.observe(this) {
            binding.imgDropDown.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (it) R.drawable.ic_expand else R.drawable.ic_collapse
                )
            )
        }

        heightExpand.observe(this) {
            // You could implement similar dropdown indicators for height unit if needed
        }

        weightExpand.observe(this) {
            // You could implement similar dropdown indicators for weight unit if needed
        }
    }

    private fun setListener() {
        binding.tool.btnBack.setOnClickListener {
            finish()
        }

        binding.smokerRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            smoker_status = when (checkedId) {
                R.id.no_smoker_button -> SmokingStatus.NON_SMOKER
                R.id.yes_smoker_button -> SmokingStatus.SMOKER
                else -> SmokingStatus.UNSPECIFIED
            }
        }

        selectGender { gender ->
            this.gender = gender
        }

        selectHeightUnit { unit ->
            this.selectedHeightUnit = unit
        }

        selectWeightUnit { unit ->
            this.selectedWeightUnit = unit
        }

        binding.etGender.setOnClickListener {
            isExpand.value = !isExpand.value!!
        }

        // Clear errors on text changes
        binding.etFirstName.doAfterTextChanged { showHideError(binding.tvFirstNameError, false) }
        binding.etWeight.doAfterTextChanged { showHideError(binding.tvWeightError, false) }
        binding.etHeight.doAfterTextChanged { showHideError(binding.tvHeightError, false) }
        binding.etAgeName.doAfterTextChanged { showHideError(binding.tvAgeError, false) }

        binding.buttonNext.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val age = binding.etAgeName.text.toString().trim()
            val weightText = binding.etWeight.text.toString().trim()
            val gender = binding.etGender.text.toString().trim()
            val heightText = binding.etHeight.text.toString().trim()
            val heightUnit = selectedHeightUnit
            val weightUnit = selectedWeightUnit

            when (validate(firstName, age, weightText, heightText, gender, heightUnit, weightUnit)) {
                ValidUser.INVALID_FIRST_NAME -> {
                    showHideError(binding.tvFirstNameError, true)
                    return@setOnClickListener
                }

                ValidUser.INVALID_AGE -> {
                    showHideError(binding.tvAgeError, true)
                    return@setOnClickListener
                }

                ValidUser.INVALID_WEIGHT -> {
                    showHideError(binding.tvWeightError, true)
                    binding.tvWeightError.text = "Please enter your weight"
                    return@setOnClickListener
                }

                ValidUser.INVALID_HEIGHT -> {
                    showHideError(binding.tvHeightError, true)
                    binding.tvHeightError.text = "Please enter your height"
                    return@setOnClickListener
                }

                ValidUser.INVALID_GENDER -> {
                    showHideError(binding.tvGenderError, true)
                    return@setOnClickListener
                }

                ValidUser.INVALID_HEIGHT_UNIT -> {
                    showHideError(binding.tvHeightError, true)
                    binding.tvHeightError.text = "Please select the height unit"
                    return@setOnClickListener
                }

                ValidUser.INVALID_WEIGHT_UNIT -> {
                    showHideError(binding.tvWeightError, true)
                    binding.tvWeightError.text = "Please select the weight unit"
                    return@setOnClickListener
                }

                ValidUser.VALID -> {
                    // Convert to standard units for storage (heights in cm, weights in kg)
                    val convertedWeight = if (weightUnit == "lb") weightText.toDouble() * 0.453592 else weightText.toDouble()
                    val convertedHeight = if (heightUnit == "ft") heightText.toDouble() * 30.48 else heightText.toDouble()

                    val sex = when (gender.uppercase()) {
                        "MALE" -> Sex.MALE
                        "FEMALE" -> Sex.FEMALE
                        else -> Sex.UNSPECIFIED
                    }

                    val subjectData = Model.SubjectDetails(
                        sex = sex,
                        age = age.toDouble(),
                        weight = convertedWeight,
                        height = convertedHeight,
                        isSmoker = smoker_status,
                        name = firstName,
                        heightUnit = heightUnit,
                        weightUnit = weightUnit
                    )

                    // Save to Pref
                    Pref.subjectDetails = subjectData
                    finish()
                }
            }
        }
    }

    private fun validate(
        firstName: String,
        age: String,
        weight: String,
        height: String,
        gender: String,
        heightUnit: String,
        weightUnit: String
    ): ValidUser {
        return when {
            firstName.isBlank() -> ValidUser.INVALID_FIRST_NAME
            age.isBlank() -> ValidUser.INVALID_AGE
            height.isBlank() -> ValidUser.INVALID_HEIGHT
            heightUnit.isBlank() -> ValidUser.INVALID_HEIGHT_UNIT
            weightUnit.isBlank() -> ValidUser.INVALID_WEIGHT_UNIT
            weight.isBlank() -> ValidUser.INVALID_WEIGHT
            gender.isBlank() -> ValidUser.INVALID_GENDER
            else -> ValidUser.VALID
        }
    }

    private fun selectGender(callback: (String) -> Unit) {
        val list = arrayListOf("Male", "Female", "Other")
        val genderAdapter = SpinnerAdapter(this, R.layout.spinner_item, list)
        binding.etGender.setAdapter(genderAdapter)
        binding.etGender.setOnItemClickListener { _, _, position, _ ->
            val gender = genderAdapter.getItem(position)
            showHideError(binding.tvGenderError, false)
            callback(gender)
            binding.etGender.setText(gender)
            binding.tilGender.isErrorEnabled = false
        }
    }

    private fun selectHeightUnit(callback: (String) -> Unit) {
        val list = arrayListOf("cm", "ft")
        val heightAdapter = SpinnerAdapter(this, R.layout.spinner_item, list)
        binding.etHeightUnit.setAdapter(heightAdapter)

        binding.etHeightUnit.setOnItemClickListener { _, _, position, _ ->
            val selectedUnit = heightAdapter.getItem(position)

            // Only proceed if the unit has changed
            if (selectedUnit != selectedHeightUnit) {
                val currentText = binding.etHeight.text.toString()
                if (currentText.isNotEmpty()) {
                    val currentValue = currentText.toDoubleOrNull()
                    if (currentValue != null) {
                        val convertedValue = when {
                            selectedHeightUnit == "cm" && selectedUnit == "ft" -> roundOff(currentValue / 30.48, 2)
                            selectedHeightUnit == "ft" && selectedUnit == "cm" -> roundOff(currentValue * 30.48, 0)
                            else -> currentValue
                        }
                        if (selectedUnit == "cm") {
                            binding.etHeight.setText(convertedValue.toInt().toString())
                        } else {
                            binding.etHeight.setText(String.format("%.2f", convertedValue))
                        }
                    }
                }

                // Update current unit
                selectedHeightUnit = selectedUnit
            }

            showHideError(binding.tvHeightError, false)
            binding.etHeightUnit.setText(selectedUnit)
            callback(selectedUnit)
        }
    }

    private fun selectWeightUnit(callback: (String) -> Unit) {
        val list = arrayListOf("kg", "lb")
        val weightAdapter = SpinnerAdapter(this, R.layout.spinner_item, list)
        binding.etWeightUnit.setAdapter(weightAdapter)

        binding.etWeightUnit.setOnItemClickListener { _, _, position, _ ->
            val selectedUnit = weightAdapter.getItem(position)

            // Only convert if the unit has changed
            if (selectedUnit != selectedWeightUnit) {
                val currentText = binding.etWeight.text.toString()
                if (currentText.isNotEmpty()) {
                    val currentValue = currentText.toDoubleOrNull()
                    if (currentValue != null) {
                        val convertedValue = when {
                            selectedWeightUnit == "kg" && selectedUnit == "lb" -> roundOff(currentValue * 2.20462, 1)
                            selectedWeightUnit == "lb" && selectedUnit == "kg" -> roundOff(currentValue * 0.453592, 1)
                            else -> currentValue
                        }
                        binding.etWeight.setText(String.format("%.1f", convertedValue))
                    }
                }

                // Update the current unit
                selectedWeightUnit = selectedUnit
            }

            showHideError(binding.tvWeightError, false)
            binding.etWeightUnit.setText(selectedUnit)
            callback(selectedUnit)
        }
    }

    private fun showHideError(view: TextView, isErrorEnable: Boolean) {
        view.visibility = if (isErrorEnable) View.VISIBLE else View.GONE
    }

    private fun roundOff(value: Double, decimals: Int): Double {
        return BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP).toDouble()
    }
}

enum class ValidUser {
    INVALID_FIRST_NAME,
    INVALID_AGE,
    INVALID_WEIGHT,
    INVALID_HEIGHT,
    INVALID_HEIGHT_UNIT,
    INVALID_WEIGHT_UNIT,
    INVALID_GENDER,
    VALID
}


