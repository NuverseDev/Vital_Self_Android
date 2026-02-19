package com.vital_self.features.packages.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.features.packages.data.model.CreditPackage
import com.vital_self.features.packages.data.model.UserCredit
import com.vital_self.core.ui.components.toolbar.VitalToolbar
import com.vital_self.core.ui.theme.InterFamily
import com.vital_self.core.ui.theme.SecondaryFrontColor
import com.vital_self.core.ui.theme.ThemeColor
import com.vital_self.core.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Locale

// Screen State
data class PackagesScreenState(
    val creditPackages: List<CreditPackage> = emptyList(),
    val userPackages: List<UserCredit> = emptyList(),
    val isLoadingPackages: Boolean = false,
    val isLoadingUserPackages: Boolean = false,
    val packagesError: String? = null,
    val userPackagesError: String? = null,
    val showPurchaseDialog: Boolean = false,
    val selectedPackage: CreditPackage? = null,
    val isPurchasing: Boolean = false,
    val purchaseError: String? = null,
    val purchaseSuccess: Boolean = false
)

// Screen Events
sealed class PackagesScreenEvent {
    data object BackClicked : PackagesScreenEvent()
    data object RefreshPackages : PackagesScreenEvent()
    data object RefreshUserPackages : PackagesScreenEvent()
    data class PackageClicked(val creditPackage: CreditPackage) : PackagesScreenEvent()
    data object ConfirmPurchase : PackagesScreenEvent()
    data object DismissPurchaseDialog : PackagesScreenEvent()
    data object DismissSuccessDialog : PackagesScreenEvent()
}

@Preview
@Composable
private fun PPackageActivity(){
    PackagesScreenContent(state = PackagesScreenState(), onEvent = {})
}

@Composable
fun PackagesScreenContent(
    state: PackagesScreenState,
    onEvent: (PackagesScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Packages", "My Purchases")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Toolbar
            VitalToolbar(
                title = "Packages",
                onBackClick = { onEvent(PackagesScreenEvent.BackClicked) }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                containerColor = White,
                contentColor = ThemeColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = ThemeColor
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontFamily = InterFamily,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) ThemeColor else SecondaryFrontColor
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTabIndex) {
                0 -> PackagesTab(
                    packages = state.creditPackages,
                    isLoading = state.isLoadingPackages,
                    error = state.packagesError,
                    onPackageClick = { onEvent(PackagesScreenEvent.PackageClicked(it)) }
                )
                1 -> PurchasedPackagesTab(
                    userPackages = state.userPackages,
                    isLoading = state.isLoadingUserPackages,
                    error = state.userPackagesError
                )
            }
            }
        }

        // Purchase Confirmation Dialog
        if (state.showPurchaseDialog && state.selectedPackage != null) {
            PurchaseConfirmationDialog(
                creditPackage = state.selectedPackage,
                isPurchasing = state.isPurchasing,
                onConfirm = { onEvent(PackagesScreenEvent.ConfirmPurchase) },
                onDismiss = { onEvent(PackagesScreenEvent.DismissPurchaseDialog) }
            )
        }

        // Purchase Success Dialog
        if (state.purchaseSuccess) {
            PurchaseSuccessDialog(
                onDismiss = { onEvent(PackagesScreenEvent.DismissSuccessDialog) }
            )
        }

        // Loading overlay during purchase
        if (state.isPurchasing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ThemeColor)
            }
        }
    }
}

@Composable
private fun PurchaseConfirmationDialog(
    creditPackage: CreditPackage,
    isPurchasing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isPurchasing) onDismiss() },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Confirm Purchase",
                fontFamily = InterFamily,
                fontWeight = FontWeight.Bold,
                color = SecondaryFrontColor
            )
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to purchase the following package?",
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ThemeColor.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = creditPackage.packageName,
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ThemeColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = creditPackage.shortDescription,
                            fontFamily = InterFamily,
                            fontSize = 12.sp,
                            color = SecondaryFrontColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${creditPackage.totalCredits} Credits",
                                fontFamily = InterFamily,
                                fontSize = 14.sp,
                                color = SecondaryFrontColor
                            )
                            Text(
                                text = "$${String.format("%.2f", creditPackage.price)}",
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = ThemeColor
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isPurchasing,
                colors = ButtonDefaults.buttonColors(containerColor = ThemeColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Purchase",
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isPurchasing,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor
                )
            }
        }
    )
}

@Composable
private fun PurchaseSuccessDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Purchase Successful",
                fontFamily = InterFamily,
                fontWeight = FontWeight.Bold,
                color = ThemeColor
            )
        },
        text = {
            Text(
                text = "Your package has been purchased successfully. You can view it in the 'My Purchases' tab.",
                fontFamily = InterFamily,
                color = SecondaryFrontColor.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ThemeColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "OK",
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun PackagesTab(
    packages: List<CreditPackage>,
    isLoading: Boolean,
    error: String?,
    onPackageClick: (CreditPackage) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ThemeColor)
            }
        }
        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error,
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor,
                    textAlign = TextAlign.Center
                )
            }
        }
        packages.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No packages available",
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor
                )
            }
        }
        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(packages) { creditPackage ->
                    PackageCard(
                        creditPackage = creditPackage,
                        onClick = { onPackageClick(creditPackage) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun PackageCard(
    creditPackage: CreditPackage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Package name and price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = creditPackage.packageName,
                    fontSize = 20.sp,
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColor
                )
                Text(
                    text = "$${String.format("%.2f", creditPackage.price)}",
                    fontSize = 22.sp,
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryFrontColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Short description
            Text(
                text = creditPackage.shortDescription,
                fontSize = 14.sp,
                fontFamily = InterFamily,
                color = SecondaryFrontColor.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Credits and duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(label = "Credits", value = "${creditPackage.totalCredits}")
                InfoChip(label = "Valid", value = "${creditPackage.durationDays} days")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Long description
            Text(
                text = creditPackage.longDescription,
                fontSize = 12.sp,
                fontFamily = InterFamily,
                color = SecondaryFrontColor.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Buy button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ThemeColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Buy Now",
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PurchasedPackagesTab(
    userPackages: List<UserCredit>,
    isLoading: Boolean,
    error: String?
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ThemeColor)
            }
        }
        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error,
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor,
                    textAlign = TextAlign.Center
                )
            }
        }
        userPackages.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No purchased packages",
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor
                )
            }
        }
        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(userPackages) { userCredit ->
                    PurchasedPackageCard(userCredit = userCredit)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun PurchasedPackageCard(userCredit: UserCredit) {
    val packageInfo = userCredit.packageInfo

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Package name
            Text(
                text = packageInfo?.packageName ?: "Package",
                fontSize = 20.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.Bold,
                color = ThemeColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            packageInfo?.shortDescription?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontFamily = InterFamily,
                    color = SecondaryFrontColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Credits info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(label = "Available", value = "${userCredit.availableCredits}")
                InfoChip(label = "Total", value = "${userCredit.totalCredits}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Purchased",
                        fontSize = 10.sp,
                        fontFamily = InterFamily,
                        color = SecondaryFrontColor.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatDate(userCredit.purchaseDate),
                        fontSize = 12.sp,
                        fontFamily = InterFamily,
                        color = SecondaryFrontColor
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Expires",
                        fontSize = 10.sp,
                        fontFamily = InterFamily,
                        color = SecondaryFrontColor.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatDate(userCredit.expiryDate),
                        fontSize = 12.sp,
                        fontFamily = InterFamily,
                        color = SecondaryFrontColor
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ThemeColor.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.Bold,
                color = ThemeColor
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontFamily = InterFamily,
                color = SecondaryFrontColor.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString.take(10)
    }
}
