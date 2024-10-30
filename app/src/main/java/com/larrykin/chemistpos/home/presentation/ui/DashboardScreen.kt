package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "SALES BY DATE", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Total Cash : $cash", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Total Mpesa: $mpesa", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Total Discount: $discount", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Total Credit: $credit", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Service Cash: $servicesCash", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Service Mpesa: $servicesMpesa",
                style = MaterialTheme.typography.bodyMedium
            )
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "INCOME", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Total Cash : $cash", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Total Mpesa: $mpesa", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Stock Worth: $stockWorth", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Service Cash: $servicesCash", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Service Mpesa: $servicesMpesa",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = "Profit Amount: $profit", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Loss Amount: $loss", style = MaterialTheme.typography.bodyMedium)

        }
    }

}
