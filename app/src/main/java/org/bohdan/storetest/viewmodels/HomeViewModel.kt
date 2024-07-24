package org.bohdan.storetest.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import org.bohdan.storetest.models.BannerModel
import org.bohdan.storetest.models.CategoryModel
import org.bohdan.storetest.models.ProductModel

class HomeViewModel : ViewModel() {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    private val _banners = MutableLiveData<List<BannerModel>>()
    val banners: LiveData<List<BannerModel>> = _banners

    private val _items = MutableLiveData<List<CategoryModel>>()
    val items: LiveData<List<CategoryModel>> = _items

    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>> = _products

    init{
        loadBanners()
        loadCategories()
        loadGroceries()
    }

    private fun loadGroceries() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener {result ->
                val groceries = result.toObjects(ProductModel::class.java)
                _products.value = groceries
            }
            .addOnFailureListener{exception ->
                Log.w("Firestore", "Error getting GROCERIES: ", exception)
            }
    }

    fun addProducts(products: List<ProductModel>) {
        for (product in products) {
            firestore.collection("products").add(product)
                .addOnSuccessListener {
                    Log.d("Firestore", "Product added with ID: ${it.id}")
                    loadGroceries()
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding product", e)
                }
        }
    }

    fun deleteAllProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    firestore.collection("products").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Product successfully deleted!")
                            loadGroceries()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting product", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }

    fun loadCategories() {
        firestore.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val categories = result.toObjects(CategoryModel::class.java)
                _items.value = categories
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting CATEGORIES: ", exception)
            }
    }
    fun loadBanners() {
        val storageRef = firebaseStorage.reference
        storageRef.listAll().addOnSuccessListener { listResult ->
            processListResult(listResult)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun processListResult(listResult: ListResult) {
        val bannerList = mutableListOf<BannerModel>()
        val items = listResult.items

        if (items.isNotEmpty()) {
            for (item in items) {
                item.downloadUrl.addOnSuccessListener { uri ->
                    val banner = BannerModel(url = uri.toString())
                    bannerList.add(banner)

                    if (bannerList.size == items.size) {
                        _banners.value = bannerList
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            }
        } else {
            _banners.value = bannerList
        }
    }


}
