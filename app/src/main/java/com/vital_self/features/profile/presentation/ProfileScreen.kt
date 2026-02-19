package com.vital_self.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.R
import com.vital_self.core.ui.components.buttons.PrimaryButton
import com.vital_self.core.ui.components.common.ErrorMessage
import com.vital_self.core.ui.components.dropdowns.VitalDropdown
import com.vital_self.core.ui.components.textfields.VitalTextField
import com.vital_self.core.ui.components.toolbar.VitalToolbar
import com.vital_self.core.ui.theme.InterFamily
import com.vital_self.core.ui.theme.SecondaryFrontColor
import com.vital_self.core.ui.theme.ThemeColor
import com.vital_self.core.ui.theme.White

// Enum for tracking which field has error

enum class ProfileFieldError {
    NONE, NAME, AGE, HEIGHT, WEIGHT, GENDER
}

// State class for profile screen
data class ProfileScreenState(
    val name: String = "",
    val age: String = "",
    val height: String = "",
    val heightUnit: String = "cm",
    val weight: String = "",
    val weightUnit: String = "kg",
    val gender: String = "",
    val isSmoker: Boolean = false,
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val errorField: ProfileFieldError = ProfileFieldError.NONE,
    val errorMessage: String = "Please fill in all required fields",
    val isDoctor: Boolean = false
)

// Events sealed class
sealed class ProfileScreenEvent {
    data class NameChanged(val value: String) : ProfileScreenEvent()
    data class AgeChanged(val value: String) : ProfileScreenEvent()
    data class HeightChanged(val value: String) : ProfileScreenEvent()
    data class HeightUnitChanged(val value: String) : ProfileScreenEvent()
    data class WeightChanged(val value: String) : ProfileScreenEvent()
    data class WeightUnitChanged(val value: String) : ProfileScreenEvent()
    data class GenderChanged(val value: String) : ProfileScreenEvent()
    data class SmokerStatusChanged(val value: Boolean) : ProfileScreenEvent()
    data object SaveClicked : ProfileScreenEvent()
    data object BackClicked : ProfileScreenEvent()
    data object DismissError : ProfileScreenEvent()
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreenContent(
        state = ProfileScreenState(),
        onEvent = {}
    )
}

@Composable
fun ProfileScreenContent(
    state: ProfileScreenState,
    onEvent: (ProfileScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Profile"
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Toolbar
        VitalToolbar(
            title = title,
            onBackClick = { onEvent(ProfileScreenEvent.BackClicked) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Text(
                    text = "Tell us about yourself!",
                    fontSize = 20.sp,
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Doctor Badge (non-editable)
                if (state.isDoctor) {
                    DoctorBadge()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Full Name
                FieldLabel(label = "Full name")
                Spacer(modifier = Modifier.height(4.dp))
                VitalTextField(
                    value = state.name,
                    onValueChange = { onEvent(ProfileScreenEvent.NameChanged(it)) },
                    hint = "Enter your full name",
                    leadingIcon = painterResource(id = R.drawable.ic_person),
                    imeAction = ImeAction.Next,
                    isError = state.errorField == ProfileFieldError.NAME
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Age
                FieldLabel(label = "Age")
                Spacer(modifier = Modifier.height(4.dp))
                VitalTextField(
                    value = state.age,
                    onValueChange = { onEvent(ProfileScreenEvent.AgeChanged(it)) },
                    hint = "Enter your age",
                    leadingIcon = painterResource(id = R.drawable.ic_calendar_month),
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    isError = state.errorField == ProfileFieldError.AGE
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Height with unit
                FieldLabel(label = "Height")
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VitalTextField(
                        value = state.height,
                        onValueChange = { onEvent(ProfileScreenEvent.HeightChanged(it)) },
                        hint = "Enter your height",
                        leadingIcon = painterResource(id = R.drawable.ic_height),
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f),
                        isError = state.errorField == ProfileFieldError.HEIGHT
                    )
                    VitalDropdown(
                        selectedValue = state.heightUnit,
                        options = listOf("cm", "ft"),
                        onValueChange = { onEvent(ProfileScreenEvent.HeightUnitChanged(it)) },
                        modifier = Modifier.width(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Weight with unit
                FieldLabel(label = "Weight")
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VitalTextField(
                        value = state.weight,
                        onValueChange = { onEvent(ProfileScreenEvent.WeightChanged(it)) },
                        hint = "Enter your weight",
                        leadingIcon = painterResource(id = R.drawable.ic_weight),
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(1f),
                        isError = state.errorField == ProfileFieldError.WEIGHT
                    )
                    VitalDropdown(
                        selectedValue = state.weightUnit,
                        options = listOf("kg", "lb"),
                        onValueChange = { onEvent(ProfileScreenEvent.WeightUnitChanged(it)) },
                        modifier = Modifier.width(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gender
                FieldLabel(label = "Sex")
                Spacer(modifier = Modifier.height(4.dp))
                VitalDropdown(
                    selectedValue = state.gender,
                    options = listOf("male", "female", "Other"),
                    onValueChange = { onEvent(ProfileScreenEvent.GenderChanged(it)) },
                    hint = "Select your gender",
                    leadingIcon = painterResource(id = R.drawable.ic_gender),
                    isError = state.errorField == ProfileFieldError.GENDER
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Smoker status
                FieldLabel(label = "Are you a smoker?")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = state.isSmoker,
                                onClick = { onEvent(ProfileScreenEvent.SmokerStatusChanged(true)) },
                                role = Role.RadioButton
                            )
                            .padding(end = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.isSmoker,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ThemeColor,
                                unselectedColor = SecondaryFrontColor
                            )
                        )
                        Text(
                            text = "Yes",
                            fontFamily = InterFamily,
                            fontSize = 16.sp,
                            color = SecondaryFrontColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = !state.isSmoker,
                                onClick = { onEvent(ProfileScreenEvent.SmokerStatusChanged(false)) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !state.isSmoker,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ThemeColor,
                                unselectedColor = SecondaryFrontColor
                            )
                        )
                        Text(
                            text = "No",
                            fontFamily = InterFamily,
                            fontSize = 16.sp,
                            color = SecondaryFrontColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                ErrorMessage(
                    message = state.errorMessage,
                    isVisible = state.showError
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Save button at bottom
            PrimaryButton(
                text = "Save",
                onClick = { onEvent(ProfileScreenEvent.SaveClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            )
        }
    }
}

@Composable
private fun FieldLabel(label: String) {
    Text(
        text = label,
        fontSize = 16.sp,
        fontFamily = InterFamily,
        color = SecondaryFrontColor,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun DoctorBadge() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ThemeColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = "Doctor",
            tint = ThemeColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Account Type",
                fontSize = 12.sp,
                fontFamily = InterFamily,
                color = SecondaryFrontColor.copy(alpha = 0.7f)
            )
            Text(
                text = "Doctor",
                fontSize = 16.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                color = ThemeColor
            )
        }
    }
}
