package com.larrykin.chemistpos.home.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.data.UserDao
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import com.larrykin.chemistpos.home.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MenuContentViewModel @Inject constructor(
    private val repository: UserRepository,
    private val incomeDao: IncomeDao,
    private val medicineDao: MedicineDao,
    private val productDao: ProductDao,
    private val salesDao: SalesDao,
    private val salesHistoryDao: SalesHistoryDao,
    private val servicesDao: ServicesDao,
    private val supplierDao: SupplierDao,
    private val userDao: UserDao
) : ViewModel() {

    fun syncData() {
        viewModelScope.launch {
            val adminEmail = getAdminEmail().toString()
            val firestore = FirebaseFirestore.getInstance()
            val userDataDocRef = firestore.collection("UserData").document(adminEmail)

            val subCollections = listOf(
                "income",
                "medicines",
                "products",
                "sales",
                "sales_history",
                "services",
                "services_offered",
                "suppliers",
                "users"
            )

            for (subCollection in subCollections) {
                val localData = getLocalData(subCollection)
                val firestoreData = userDataDocRef.collection(subCollection).get()
                    .await().documents.map { it.toObject(Data::class.java)!! }

                // Merge local and Firestore data
                val mergedData = mergeData(localData, firestoreData)

                // Update Firestore with merged data
                for (data in mergedData) {
                    userDataDocRef.collection(subCollection).document(data.id).set(data).await()
                }

                // Update local database with merged data
                updateLocalDatabase(subCollection, mergedData)
            }
        }
    }

    private suspend fun getLocalData(subCollection: String): List<Data> {
        return when (subCollection) {
            "income" -> incomeDao.getAllIncome().first().map { Data(it.id.toString(), it.timestamp) }
            "medicines" -> medicineDao.getAllMedicines().first().map { Data(it.id.toString(), it.timestamp) }
            "products" -> productDao.getAllProducts().first().map { Data(it.id.toString(), it.timestamp) }
            "sales" -> salesDao.getAllSales().first().map { Data(it.id.toString(), it.timestamp) }
            "sales_history" -> salesHistoryDao.getAllSalesHistory().first().map { Data(it.id.toString(), it.timestamp) }
            "services" -> servicesDao.getAllServices().first().map { Data(it.id.toString(), it.timestamp) }
            "services_offered" -> servicesDao.getAllServicesOffered().first().map { Data(it.id.toString(), it.timestamp) }
            "suppliers" -> supplierDao.getAllSuppliers().first().map { Data(it.id.toString(), it.timestamp) }
            "users" -> userDao.getAllUsers().first().map { Data(it.email, it.timestamp) }
            else -> emptyList()
        }
    }

    data class Data(
        val id: String = "",
        val timestamp: Long = 0L,
        val cash: Double? = null,
        val mpesa: Double? = null,
        val stockWorth: Double? = null,
        val servicesCash: Double? = null,
        val servicesMpesa: Double? = null,
        val profit: Double? = null,
        val loss: Double? = null,
        val name: String? = null,
        val company: String? = null,
        val formulation: String? = null,
        val minStock: Int? = null,
        val minMeasure: Int? = null,
        val quantityAvailable: Int? = null,
        val buyingPrice: Double? = null,
        val retailSellingPrice: Double? = null,
        val wholesaleSellingPrice: Double? = null,
        val supplierName: String? = null,
        val dateAdded: Date? = null,
        val updatedAt: Date? = null,
        val addedBy: String? = null,
        val expiryDate: Date? = null,
        val description: String? = null,
        val totalPrice: Double? = null,
        val expectedAmount: Double? = null,
        val discount: Double? = null,
        val credit: Double? = null,
        val seller: String? = null,
        val date: Date? = null,
        val items: List<SaleItem>? = null,
        val medicineNames: List<String>? = null,
        val role: Role? = null,
        val createdAt: Date? = null,
        val profilePictureUrl: String? = null,
        val phoneNumber: Number? = null,
        val chemistName: String? = null,
        val username: String? = null,
        val password: String? = null,
        val email: String? = null
    )

    private fun mergeData(localData: List<Data>, firestoreData: List<Data>): List<Data> {
        val mergedData = mutableListOf<Data>()
        val firestoreDataMap = firestoreData.associateBy { it.id }

        for (localItem in localData) {
            val firestoreItem = firestoreDataMap[localItem.id]
            if (firestoreItem == null || localItem.timestamp > firestoreItem.timestamp) {
                mergedData.add(localItem)
            } else {
                mergedData.add(firestoreItem)
            }
        }

        for (firestoreItem in firestoreData) {
            if (!localData.any { it.id == firestoreItem.id }) {
                mergedData.add(firestoreItem)
            }
        }

        return mergedData
    }

    private suspend fun updateLocalDatabase(subCollection: String, mergedData: List<Data>) {
        when (subCollection) {
            "income" -> incomeDao.updateAll(mergedData.map {
                Income(
                    id = it.id.toInt(),
                    timestamp = it.timestamp,
                    cash = it.cash ?: 0.0,
                    mpesa = it.mpesa ?: 0.0,
                    stockWorth = it.stockWorth ?: 0.0,
                    servicesCash = it.servicesCash ?: 0.0,
                    servicesMpesa = it.servicesMpesa ?: 0.0,
                    profit = it.profit ?: 0.0,
                    loss = it.loss ?: 0.0
                )
            })

            "medicines" -> medicineDao.updateAll(mergedData.map {
                Medicine(
                    id = it.id.toInt(),
                    timestamp = it.timestamp,
                    name = it.name ?: "",
                    company = it.company ?: ""
                )
            })

            "products" -> productDao.updateAll(mergedData.map {
                Product(
                    id = it.id.toInt(),
                    timestamp = it.timestamp,
                    name = it.name ?: "",
                    company = it.company ?: "",
                    formulation = it.formulation ?: "",
                    minStock = it.minStock ?: 0,
                    minMeasure = it.minMeasure ?: 0,
                    quantityAvailable = it.quantityAvailable ?: 0,
                    buyingPrice = it.buyingPrice ?: 0.0,
                    retailSellingPrice = it.retailSellingPrice ?: 0.0,
                    wholesaleSellingPrice = it.wholesaleSellingPrice ?: 0.0,
                    supplierName = it.supplierName ?: "",
                    dateAdded = it.dateAdded ?: Date(),
                    updatedAt = it.updatedAt,
                    addedBy = it.addedBy ?: "",
                    expiryDate = it.expiryDate ?: Date(),
                    description = it.description
                )
            })

            "sales" -> salesDao.updateAll(mergedData.map {
                Sales(
                    id = it.id.toInt(),
                    timestamp = it.timestamp,
                    items = it.items?.map { itemData -> SaleItem(
                        productId = itemData.productId,
                        quantity = itemData.quantity,
                    ) } ?: emptyList(),
                    totalPrice = it.totalPrice ?: 0.0,
                    expectedAmount = it.expectedAmount ?: 0.0,
                    cash = it.cash ?: 0.0,
                    mpesa = it.mpesa ?: 0.0,
                    discount = it.discount ?: 0.0,
                    credit = it.credit ?: 0.0,
                    seller = it.seller ?: "",
                    date = it.date ?: Date()
                )
            })

            "sales_history" -> salesHistoryDao.updateAll(mergedData.map {
                SalesHistory(
                    id = it.id.toInt(),
                    timestamp = it.timestamp,
                    cash = it.cash ?: 0.0,
                    mpesa = it.mpesa ?: 0.0,
                    discount = it.discount ?: 0.0,
                    credit = it.credit ?: 0.0,
                    servicesCash = it.servicesCash ?: 0.0,
                    servicesMpesa = it.servicesMpesa ?: 0.0,
                    date = it.date ?: Date()
                )
            })

            "services" -> servicesDao.updateAllServices(mergedData.map {
                Service(
                    id = it.id.toInt(),
                    timestamp = it.timestamp,
                    name = it.name ?: "",
                    description = it.description ?: "",
                    price = it.totalPrice ?: 0.0
                )
            })

            "services_offered" -> servicesDao.updateAllServicesOffered(mergedData.map {
                ServicesOffered(
                    id = it.id.toInt(),
                    timestamp = it.timestamp,
                    name = it.name ?: "",
                    servitor = it.seller ?: "",
                    description = it.description ?: "",
                    cash = it.cash ?: 0.0,
                    mpesa = it.mpesa ?: 0.0,
                    totalPrice = it.totalPrice ?: 0.0,
                    expectedAmount = it.expectedAmount ?: 0.0,
                    createdAt = it.createdAt ?: Date(),
                    updatedAt = it.updatedAt
                )
            })

            "suppliers" -> supplierDao.updateAll(mergedData.map {
                Supplier(
                    id = it.id.toInt(),
                    name = it.name ?: "",
                    phone = it.phoneNumber?.toString() ?: "",
                    email = it.email ?: "",
                    medicines = it.medicineNames?.map { name -> name } ?: emptyList(),
                    timestamp = it.timestamp
                )
            })

            "users" -> userDao.updateAll(mergedData.map { data ->
                User(
                    email = data.id,
                    username = data.username ?: "",
                    password = data.password ?: "",
                    phoneNumber = data.phoneNumber ?: 0,
                    chemistName = data.chemistName ?: "",
                    role = data.role ?: Role.USER,
                    createdAt = data.createdAt ?: Date(),
                    profilePictureUrl = data.profilePictureUrl,
                    timestamp = data.timestamp
                )
            })
        }
    }

    private suspend fun getAdminEmail(): String? {
        return try {
            when (val allUsersResult = repository.getAllUsers().firstOrNull()) {
                is GetAllUsersResult.Success -> {
                    val adminUser = allUsersResult.users.find { it.role == Role.ADMIN }
                    adminUser?.email // Return the admin email if found, else null
                }
                is GetAllUsersResult.Error -> {
                    Log.e("MyLogs", "Error fetching users: ${allUsersResult.message}")
                    null
                }
                else -> {
                    Log.e("MyLogs", "Unknown error fetching users")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("MyLogs", "Exception fetching admin email: ${e.message}")
            null
        }
    }
}