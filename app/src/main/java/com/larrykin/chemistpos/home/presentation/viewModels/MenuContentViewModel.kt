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

    fun syncData(adminEmail: String) {
        viewModelScope.launch {
            try {
                if (adminEmail.isEmpty()) {
                    Log.e("MyLogs", "Admin email is null or empty, cannot sync data")
                    return@launch
                }
                Log.d("MyLogs", "Admin email in SyncData method: $adminEmail")
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
                        .await().documents.mapNotNull {
                            it.toObject(getDataClass(subCollection))
                        }

                    if (firestoreData.isEmpty()) {
                        Log.d(
                            "MyLogs",
                            "No data found in Firestore for subCollection: $subCollection"
                        )
                    }

                    // Merge local and Firestore data
                    val mergedData = mergeData(localData, firestoreData, subCollection)

                    // Update Firestore with merged data
                    for (data in mergedData) {
                        val idFieldName = if (data is User) "email" else "id"
                        val id = data::class.java.getDeclaredField(idFieldName)
                            .apply { isAccessible = true }.get(data)
                        if (id is Int && id > 0 || id is String && id.isNotEmpty()) {
                            userDataDocRef.collection(subCollection).document(id.toString())
                                .set(data).await()
                            Log.d("MyLogs", "Data synced: $data")
                        } else {
                            Log.d("MyLogs", "Skipped syncing data with empty ID: $data")
                        }
                    }

                    // Update local database with merged data
                    updateLocalDatabase(subCollection, mergedData)
                }
            } catch (e: Exception) {
                Log.e("MyLogs", "Error syncing data: ${e.message}", e)
            }
        }
    }

    private fun getDataClass(subCollection: String): Class<*> {
        return when (subCollection) {
            "income" -> Income::class.java
            "medicines" -> Medicine::class.java
            "products" -> Product::class.java
            "sales" -> Sales::class.java
            "sales_history" -> SalesHistory::class.java
            "services" -> Service::class.java
            "services_offered" -> ServicesOffered::class.java
            "suppliers" -> Supplier::class.java
            "users" -> User::class.java
            else -> Any::class.java
        }
    }

    private suspend fun getLocalData(subCollection: String): List<Any> {
        return when (subCollection) {
            "income" -> incomeDao.getAllIncome().first()
                .map {
                    Income(
                        id = it.id,
                        timestamp = it.timestamp,
                        cash = it.cash,
                        mpesa = it.mpesa,
                        stockWorth = it.stockWorth,
                        servicesCash = it.servicesCash,
                        servicesMpesa = it.servicesMpesa,
                        profit = it.profit,
                        loss = it.loss
                    )
                }

            "medicines" -> medicineDao.getAllMedicines().first()
                .map {
                    Medicine(
                        id = it.id,
                        timestamp = it.timestamp,
                        name = it.name,
                        company = it.company
                    )
                }

            "products" -> productDao.getAllProducts().first()
                .map {
                    Product(
                        id = it.id,
                        timestamp = it.timestamp,
                        name = it.name,
                        company = it.company,
                        formulation = it.formulation,
                        minStock = it.minStock,
                        minMeasure = it.minMeasure,
                        quantityAvailable = it.quantityAvailable,
                        buyingPrice = it.buyingPrice,
                        retailSellingPrice = it.retailSellingPrice,
                        wholesaleSellingPrice = it.wholesaleSellingPrice,
                        supplierName = it.supplierName,
                        dateAdded = it.dateAdded,
                        updatedAt = it.updatedAt,
                        addedBy = it.addedBy,
                        expiryDate = it.expiryDate,
                        description = it.description
                    )
                }

            "sales" -> salesDao.getAllSales().first()
                .map {
                    Sales(
                        id = it.id,
                        timestamp = it.timestamp,
                        items = it.items,
                        totalPrice = it.totalPrice,
                        expectedAmount = it.expectedAmount,
                        cash = it.cash,
                        mpesa = it.mpesa,
                        discount = it.discount,
                        credit = it.credit,
                        seller = it.seller,
                        date = it.date
                    )
                }

            "sales_history" -> salesHistoryDao.getAllSalesHistory().first()
                .map {
                    SalesHistory(
                        id = it.id,
                        timestamp = it.timestamp,
                        cash = it.cash,
                        mpesa = it.mpesa,
                        discount = it.discount,
                        credit = it.credit,
                        servicesCash = it.servicesCash,
                        servicesMpesa = it.servicesMpesa,
                        date = it.date
                    )
                }

            "services" -> servicesDao.getAllServices().first()
                .map {
                    Service(
                        id = it.id,
                        timestamp = it.timestamp,
                        name = it.name,
                        description = it.description,
                        price = it.price
                    )
                }

            "services_offered" -> servicesDao.getAllServicesOffered().first()
                .map {
                    ServicesOffered(
                        id = it.id,
                        timestamp = it.timestamp,
                        name = it.name,
                        servitor = it.servitor,
                        description = it.description,
                        cash = it.cash,
                        mpesa = it.mpesa,
                        totalPrice = it.totalPrice,
                        expectedAmount = it.expectedAmount,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }

            "suppliers" -> supplierDao.getAllSuppliers().first()
                .map {
                    Supplier(
                        id = it.id,
                        timestamp = it.timestamp,
                        name = it.name,
                        phone = it.phone,
                        email = it.email,
                        medicines = it.medicines
                    )
                }

            "users" -> userDao.getAllUsers().first()
                .map {
                    User(
                        email = it.email,
                        timestamp = it.timestamp,
                        username = it.username,
                        password = it.password,
                        phoneNumber = it.phoneNumber,
                        chemistName = it.chemistName,
                        role = it.role,
                        createdAt = it.createdAt,
                        profilePictureUrl = it.profilePictureUrl
                    )
                }

            else -> emptyList()
        }
    }

    private inline fun <reified T : Any> mergeData(
        localData: List<T>,
        firestoreData: List<T>,
        subCollection: String
    ): List<T> {
        val mergedData = mutableListOf<T>()
        val idFieldName = if (subCollection == "users") "email" else "id"
        val firestoreDataMap = firestoreData.associateBy {
            it::class.java.getDeclaredField(idFieldName).apply { isAccessible = true }.get(it)
        }
        Log.d("MyLogs", "Firestore data map: $firestoreDataMap")

        for (localItem in localData) {
            val localId =
                localItem::class.java.getDeclaredField(idFieldName).apply { isAccessible = true }
                    .get(localItem)
            val localTimestamp =
                localItem::class.java.getDeclaredField("timestamp").apply { isAccessible = true }
                    .get(localItem) as Long
            val firestoreItem = firestoreDataMap[localId]
            if (firestoreItem == null || localTimestamp > firestoreItem::class.java.getDeclaredField(
                    "timestamp"
                ).apply { isAccessible = true }.get(firestoreItem) as Long
            ) {
                mergedData.add(localItem)
                Log.d("MyLogs", "Local data added: $localItem")
            } else {
                mergedData.add(firestoreItem)
                Log.d("MyLogs", "Firestore data added: $firestoreItem")
            }
        }

        for (firestoreItem in firestoreData) {
            val firestoreId =
                firestoreItem::class.java.getDeclaredField(idFieldName)
                    .apply { isAccessible = true }
                    .get(firestoreItem)
            if (!localData.any {
                    it::class.java.getDeclaredField(idFieldName).apply { isAccessible = true }
                        .get(it) == firestoreId
                }) {
                mergedData.add(firestoreItem)
                Log.d("MyLogs", "Firestore data added: $firestoreItem")
            }
        }

        return mergedData
    }

    private suspend fun updateLocalDatabase(subCollection: String, mergedData: List<Any>) {
        Log.d("MyLogs", "Updating local database: $subCollection")
        when (subCollection) {
            "income" -> incomeDao.updateAll(mergedData.mapNotNull {
                it as? Income
            })

            "medicines" -> medicineDao.updateAll(mergedData.mapNotNull {
                it as? Medicine
            })

            "products" -> productDao.updateAll(mergedData.mapNotNull {
                it as? Product
            })

            "sales" -> salesDao.updateAll(mergedData.mapNotNull {
                it as? Sales
            })

            "sales_history" -> salesHistoryDao.updateAll(mergedData.mapNotNull {
                it as? SalesHistory
            })

            "services" -> servicesDao.updateAllServices(mergedData.mapNotNull {
                it as? Service
            })

            "services_offered" -> servicesDao.updateAllServicesOffered(mergedData.mapNotNull {
                it as? ServicesOffered
            })

            "suppliers" -> supplierDao.updateAll(mergedData.mapNotNull {
                it as? Supplier
            })

            "users" -> userDao.updateAll(mergedData.mapNotNull {
                it as? User
            })
        }
    }

    suspend fun getAdminEmailAndStatus(): Pair<String?, String?> {
        return try {
            when (val allUsersResult = repository.getAllUsers().firstOrNull()) {
                is GetAllUsersResult.Success -> {
                    val adminUser = allUsersResult.users.find { it.role == Role.ADMIN }
                    Log.d("MyLogs", "Admin email: ${adminUser?.email}")
                    val email = adminUser?.email
                    if (email != null) {
                        val firestore = FirebaseFirestore.getInstance()
                        val userDocRef = firestore.collection("users").document(email)
                        val document = userDocRef.get().await()
                        val subscriptionStatus = document.getString("subscription_status")
                        Log.d("MyLogs", "Subscription status: $subscriptionStatus")
                        Pair(email, subscriptionStatus)
                    } else {
                        Pair(null, null)
                    }
                }

                is GetAllUsersResult.Error -> {
                    Log.e("MyLogs", "Error fetching users: ${allUsersResult.message}")
                    Pair(null, null)
                }

                else -> {
                    Log.e("MyLogs", "Unknown error fetching users")
                    Pair(null, null)
                }
            }
        } catch (e: Exception) {
            Log.e("MyLogs", "Exception fetching admin email: ${e.message}")
            Pair(null, null)
        }
    }
}