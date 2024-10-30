package com.larrykin.chemistpos.home.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllProductsResult
import com.larrykin.chemistpos.home.data.Income
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.data.SaleItem
import com.larrykin.chemistpos.home.data.Sales
import com.larrykin.chemistpos.home.data.SalesHistory
import com.larrykin.chemistpos.home.domain.IncomeRepository
import com.larrykin.chemistpos.home.domain.MedicineRepository
import com.larrykin.chemistpos.home.domain.ProductRepository
import com.larrykin.chemistpos.home.domain.SalesHistoryRepository
import com.larrykin.chemistpos.home.domain.SalesRepository
import com.larrykin.chemistpos.home.domain.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class StockResult {
    data object Success : StockResult()
    data class Error(val message: String) : StockResult()
}

@HiltViewModel
class StockViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val medicineRepository: MedicineRepository,
    private val supplierRepository: SupplierRepository,
    private val salesRepository: SalesRepository,
    private val salesHistoryRepository: SalesHistoryRepository,
    private val incomeRepository: IncomeRepository
) : ViewModel() {
    // medicinesNames
    private val _medicineNames = MutableStateFlow<List<String>>(emptyList())
    val medicineNames: StateFlow<List<String>> get() = _medicineNames

    //companyNames
    private val _companyNames = MutableStateFlow<List<String>>(emptyList())
    val companyNames: StateFlow<List<String>> get() = _companyNames

    //supplierNames
    private val _supplierNames = MutableStateFlow<List<String>>(emptyList())
    val supplierNames: StateFlow<List<String>> get() = _supplierNames

    //? Cart
    private val _cart = MutableStateFlow<List<Product>>(emptyList())
    val cart: StateFlow<List<Product>> get() = _cart

    //expected amount
    private val _expectedAmount = MutableStateFlow(0.0)
    val expectedAmount: StateFlow<Double> get() = _expectedAmount

    //quantity map
    private val _quantityMap = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val quantityMap: StateFlow<Map<Product, Int>> get() = _quantityMap

    //sales
    private val _sales = MutableStateFlow<List<Sales>>(emptyList())
    val sales: StateFlow<List<Sales>> get() = _sales

    init {
        viewModelScope.launch {
            _medicineNames.value = medicineRepository.getAllMedicineNames()
            _companyNames.value = medicineRepository.getAllCompanyNames()
            _supplierNames.value = supplierRepository.getSupplierNames()
            fetchSales()
        }
    }

    //function to update quantity
    fun updateQuantity(product: Product, quantity: Int) {
        _quantityMap.value = _quantityMap.value.toMutableMap().apply {
            this[product] = quantity
        }
        setExpectedAmount(_cart.value.sumOf {
            it.retailSellingPrice * (_quantityMap.value[it] ?: 1)
        })
    }

    //function to set the expected amount
    fun setExpectedAmount(amount: Double) {
        _expectedAmount.value = amount
    }

    // Function to add a product to the cart
    fun addToCart(product: Product) {
        _cart.value = _cart.value + product
        _expectedAmount.value += product.retailSellingPrice
    }

    // Function to remove a product from the cart
    fun removeFromCart(product: Product, quantity: Int) {
        _cart.value = _cart.value - product
    }

    // Function to add a product to the stock
    suspend fun addProduct(product: Product, onResult: (StockResult) -> Unit) {
        viewModelScope.launch {
            try {
                val existingProduct =
                    productRepository.getProductByNameCompanyFormulationExpiryDate(
                        product.name, product.company, product.formulation, product.expiryDate
                    )
                Log.d("existingProduct", existingProduct.toString())
                if (existingProduct != null) {
                    val updatedProduct = existingProduct.copy(
                        minStock = product.minStock,
                        minMeasure = product.minMeasure,
                        quantityAvailable = existingProduct.quantityAvailable + product.quantityAvailable,
                        buyingPrice = product.buyingPrice,
                        retailSellingPrice = product.retailSellingPrice,
                        wholesaleSellingPrice = product.wholesaleSellingPrice,
                        supplierName = product.supplierName,
                        updatedAt = Date(),
                        description = if (product.description?.isBlank() == true) existingProduct
                            .description
                        else product.description
                    )
                    val result = productRepository.updateProduct(updatedProduct)
                    if (result != null) {
                        onResult(StockResult.Success)
                        calculateCurrentStockWorth()
                    } else {
                        onResult(StockResult.Error("Failed to update product"))
                    }
                } else {
                    val result = productRepository.insertProduct(product)
                    if (result != null && result > 0) {
                        onResult(StockResult.Success)
                    } else {
                        onResult(StockResult.Error("Failed to add product"))
                    }
                }
            } catch (e: Exception) {
                onResult(StockResult.Error(e.message ?: "An error occurred"))
            }
        }
    }

    // Function to get all products from the stock
    suspend fun getAllProducts(onResult: (StockResult, List<Product>) -> Unit) {
        viewModelScope.launch {
            try {
                productRepository.getAllProducts().collect { result ->
                    when (result) {
                        is GetAllProductsResult.Success -> {
                            onResult(StockResult.Success, result.products)
                        }

                        is GetAllProductsResult.Error -> {
                            onResult(StockResult.Error(result.message), emptyList())
                        }
                    }
                }
            } catch (e: Exception) {
                onResult(StockResult.Error(e.message ?: "An error occurred"), emptyList())
            }
        }
    }

    // Function to delete a product from the stock
    suspend fun deleteProduct(productId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = productRepository.deleteProduct(productId)
                if (result) {
                    getAllProducts { stockResult, _ ->
                        onResult(stockResult is StockResult.Success)
                    }
                    calculateCurrentStockWorth()
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // Function to update a product in the stock
    suspend fun updateProduct(product: Product, onResult: (StockResult) -> Unit) {
        viewModelScope.launch {
            try {
                val result = productRepository.updateProduct(product)
                if (result != null) {
                    getAllProducts { stockResult, _ ->
                        onResult(stockResult)
                    }
                    calculateCurrentStockWorth()
                } else {
                    onResult(StockResult.Error("Failed to update product"))
                }
            } catch (e: Exception) {
                onResult(StockResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    //save the sales
    fun saveSales(
        items: List<SaleItem>,
        totalPrice: Double,
        expectedAmount: Double,
        cash: Double,
        mpesa: Double,
        discount: Double,
        credit: Double,
        seller: String,
        onResult: (Boolean) -> Unit
    ) {
        val sale = Sales(
            items = items,
            totalPrice = totalPrice,
            expectedAmount = expectedAmount,
            cash = cash,
            mpesa = mpesa,
            discount = discount,
            credit = credit,
            seller = seller,
            date = Date()
        )
        viewModelScope.launch {
            val result = salesRepository.insertSale(sale)
            if (result != null && result > 0) {
                subtractFromStock(items) { stockSuccess ->
                    onResult(stockSuccess)
                    viewModelScope.launch {
                        insertSalesHistory(sale)
                        updateIncome(sale)
                    }
                }
            } else {
                onResult(false)
            }
        }
    }

    // clear cart
    fun clearCart() {
        _cart.value = emptyList()
        _quantityMap.value = emptyMap()
        _expectedAmount.value = 0.0
    }

    //subtract sales from stock
    fun subtractFromStock(items: List<SaleItem>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            var success = true
            items.forEach { saleItem ->
                val product = productRepository.getProductById(saleItem.productId)
                product?.let {
                    val newQuantity = it.quantityAvailable - (it.minMeasure * saleItem.quantity)
                    if (newQuantity >= 0) {
                        val updatedProduct = it.copy(quantityAvailable = newQuantity)
                        val updateResult = productRepository.updateProduct(updatedProduct)
                        if (updateResult == null) {
                            success = false
                        }
                    } else {
                        success = false
                    }
                } ?: run {
                    success = false
                }
            }
            onResult(success)
        }
    }

    // fetch sales
    fun fetchSales() {
        viewModelScope.launch {
            salesRepository.getAllSales().collect { salesList ->
                _sales.value = salesList
            }
        }
    }

    //get product by id
    suspend fun getProductById(productId: Int): Product? {
        return productRepository.getProductById(productId)
    }

    //! insert sales history
    suspend fun insertSalesHistory(sale: Sales) {
        val salesHistory = SalesHistory(
            cash = sale.cash,
            mpesa = sale.mpesa,
            discount = sale.discount,
            credit = sale.credit,
            servicesCash = 0.0,
            servicesMpesa = 0.0,
            date = Date()
        )
        Log.d("MyLogs", "Inserting sales history: $salesHistory")
        val result = salesHistoryRepository.insertSalesHistory(salesHistory)
        if (result != null && result > 0) {
            Log.d("MyLogs", "Sale History inserted successfully")
        } else {
            Log.d("MyLogs", "Error in inserting salesHistory")
        }
    }

    //! update income
    suspend fun updateIncome(sales: Sales) {

        //check if there are incomes
        val result = incomeRepository.getFirstIncome()
        if (result != null) {
            //create income
            val income = Income(
                cash = sales.cash,
                mpesa = sales.mpesa,
                stockWorth = result.stockWorth,
                servicesMpesa =  0.0,
                servicesCash =  0.0,
                profit =  0.0,
                loss =  0.0
            )
            val updateResult = incomeRepository.updateIncome(income)
            if (updateResult == 1) {
                Log.d("MyLogs", "Income updated successfully")
                calculateCurrentStockWorth()
            } else {
                Log.d("MyLogs", "Error in updating income")
            }
        } else {
            //create income
            val income = Income(
                cash = sales.cash,
                mpesa = sales.mpesa,
                stockWorth = 0.0,
                servicesMpesa = 0.0,
                servicesCash = 0.0,
                profit = 0.0,
                loss = 0.0
            )
            val insertResult = incomeRepository.insertIncome(income)
            if (insertResult != null) {
                Log.d("MyLogs", "Income inserted successfully")
            } else {
                Log.d("MyLogs", "Error in inserting income")
            }
        }
    }

    suspend fun calculateCurrentStockWorth() {
        productRepository.getAllProducts().collect { result ->
            when (result) {
                is GetAllProductsResult.Success -> {
                    val totalWorth = result.products.sumOf { it.buyingPrice * it.quantityAvailable }
                    Log.d("MyLogs", "Current stock worth: $totalWorth")
                    updateStockWorth(totalWorth)
                }

                is GetAllProductsResult.Error -> {
                    Log.d("MyLogs", "Error calculating stock worth: ${result.message}")
                }
            }
        }
    }

    suspend fun updateStockWorth(stockWorth: Double) {
        // Check if there are incomes
        val result = incomeRepository.getFirstIncome()
        if (result != null) {
            val income = Income(
                cash =  0.0,
                mpesa =  0.0,
                stockWorth = stockWorth,
                servicesMpesa =  0.0,
                servicesCash =  0.0,
                profit =  0.0,
                loss =  0.0
            )
            val updateResult = incomeRepository.updateIncome(income)
            if (updateResult == 1) {
                Log.d("MyLogs", "Income stockWorth updated successfully")
            } else {
                Log.d("MyLogs", "Error in updating income stockWorth")
            }
        } else {
            val income = Income(
                cash = 0.0,
                mpesa = 0.0,
                stockWorth = stockWorth,
                servicesMpesa = 0.0,
                servicesCash = 0.0,
                profit = 0.0,
                loss = 0.0
            )
            val insertResult = incomeRepository.insertIncome(income)
            if (insertResult != null) {
                Log.d("MyLogs", "Income stockWorth inserted successfully")
            } else {
                Log.d("MyLogs", "Error in inserting income stockWorth")
            }
        }
    }

}