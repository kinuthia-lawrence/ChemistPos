package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.core.presentation.ui.DatePicker
import com.larrykin.chemistpos.home.data.Income
import com.larrykin.chemistpos.home.data.SalesHistory
import com.larrykin.chemistpos.home.presentation.viewModels.DashboardViewModel
import java.util.Calendar
import java.util.Date

@Composable
fun DashboardScreen(
    loggedInUser: LoggedInUser,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    var fromDate by remember { mutableStateOf(Date()) }
    var toDate by remember { mutableStateOf(Date()) }
    val salesHistory: List<SalesHistory> by dashboardViewModel.salesHistory.collectAsState(emptyList())
    val income: Income by dashboardViewModel.income.collectAsState()


    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "HELLO ${loggedInUser.username.uppercase()},",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Welcome to ChemistPOS",
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // Date Pickers
            Row(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DatePicker(label = "From Date", selectedDate = fromDate) { date ->
                    fromDate = date
                }
                DatePicker(label = "To Date", selectedDate = toDate) { date ->
                    toDate = date
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Normalize dates
            val normalizedFromDate = Calendar.getInstance().apply {
                time = fromDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val normalizedToDate = Calendar.getInstance().apply {
                time = toDate
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            // Filter and sort sales
            val filteredSalesHistory =
                salesHistory.filter { saleHistory -> saleHistory.date in normalizedFromDate..normalizedToDate }

            // Define the sums
            val totalCash = filteredSalesHistory.sumOf { it.cash }
            val totalMpesa = filteredSalesHistory.sumOf { it.mpesa }
            val totalDiscount = filteredSalesHistory.sumOf { it.discount }
            val totalCredit = filteredSalesHistory.sumOf { it.credit }
            val totalServicesCash = filteredSalesHistory.sumOf { it.servicesCash }
            val totalServicesMpesa = filteredSalesHistory.sumOf { it.servicesMpesa }

            // Call the SalesByDateCard composable with the calculated sums
            SalesByDateCard(
                cash = totalCash,
                mpesa = totalMpesa,
                discount = totalDiscount,
                credit = totalCredit,
                servicesCash = totalServicesCash,
                servicesMpesa = totalServicesMpesa
            )
            Spacer(modifier = Modifier.height(16.dp))
            IncomeCard(
                cash = income.cash,
                mpesa = income.mpesa,
                stockWorth = income.stockWorth,
                servicesCash = income.servicesCash,
                servicesMpesa = income.servicesMpesa,
                profit = income.profit,
                loss = income.loss
            )
        }
    }
}

@Composable
fun SalesByDateCard(
    cash: Double,
    mpesa: Double,
    discount: Double,
    credit: Double,
    servicesCash: Double,
    servicesMpesa: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "SALES BY DATE",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val salesData = listOf(
                "Total Cash" to cash,
                "Total Mpesa" to mpesa,
                "Total Discount" to discount,
                "Total Credit" to credit,
                "Service Cash" to servicesCash,
                "Service Mpesa" to servicesMpesa
            )

            salesData.forEach { (label, amount) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = String.format("%.2f", amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeCard(
    cash: Double,
    mpesa: Double,
    stockWorth: Double,
    servicesMpesa: Double,
    servicesCash: Double,
    profit: Double,
    loss: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "INCOME",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val incomeData = listOf(
                "Total Cash" to cash,
                "Total Mpesa" to mpesa,
                "Stock Worth" to stockWorth,
                "Service Cash" to servicesCash,
                "Service Mpesa" to servicesMpesa,
                "Profit Amount" to profit,
                "Loss Amount" to loss
            )

            incomeData.forEach { (label, amount) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = String.format("%.2f", amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (label.contains("Loss")) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
