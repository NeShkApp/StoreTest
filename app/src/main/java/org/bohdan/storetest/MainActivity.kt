package org.bohdan.storetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.bohdan.storetest.adapters.ProductAdapter
import org.bohdan.storetest.models.ProductModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogout: Button
    private lateinit var txtView: TextView
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var recViewAllGroceries: RecyclerView
    private lateinit var adapterAllGroceries: ProductAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnAddProducts: Button
    private lateinit var btnDeleteProducts: Button

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()
        recViewAllGroceries = findViewById(R.id.recViewAllProductsAct)
        recViewAllGroceries.layoutManager =LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAllGroceries = ProductAdapter(listOf())
        recViewAllGroceries.adapter = adapterAllGroceries

        btnAddProducts = findViewById(R.id.btnAddProductsAct)
        btnDeleteProducts = findViewById(R.id.btnDeleteProductsAct)
        fetchGroceries()

        btnAddProducts.setOnClickListener {
            addProducts()
        }

        btnDeleteProducts.setOnClickListener {
            deleteAllProducts()
        }

        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        txtView = findViewById(R.id.txtView)
        val user = Firebase.auth.currentUser
        if(user == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            user.let {
                val name = it.displayName
                val email = it.email
                val phoneNumber = it.phoneNumber
                val creationTimestamp = it.metadata?.creationTimestamp
                val formattedTimestamp = creationTimestamp?.let{
                    ts -> formatTimestamp(ts)
                }
                val photoUrl = it.photoUrl

                val userInfo = "Name: $name\nEmail: $email\nPhoto URL: $photoUrl\nPhone Number: $phoneNumber\nCreated: $formattedTimestamp"
                txtView.text = userInfo
            }

        }

    }

    private fun fetchGroceries() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = result.toObjects(ProductModel::class.java)
                adapterAllGroceries.updateProducts(products)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }


    private fun addProducts() {
        val products = listOf(
            ProductModel("Ice Cream",
                "Description 1",
                10.0,
                10,
                "https://upload.wikimedia.org/wikipedia/commons/d/da/Strawberry_ice_cream_cone_%285076899310%29.jpg",
                "Category 1",
                false),
            ProductModel("Car",
                "Description 2",
                20.0,
                20,
                "https://imageio.forbes.com/specials-images/imageserve/5d35eacaf1176b0008974b54/2020-Chevrolet-Corvette-Stingray/0x0.jpg?format=jpg&crop=4560,2565,x790,y784,safe&width=960",
                "Category 3",
                true),
            ProductModel("Milk",
                "Milk",
                30.0,
                15,
                "https://as1.ftcdn.net/v2/jpg/01/06/68/88/1000_F_106688812_rVoRFXazgIMEUJdvffG9p0XvP8Lntf0a.jpg",
                "Category 2",
                false),
            ProductModel("Apple",
                "Description 4",
                40.0,
                20,
                "https://static.wikia.nocookie.net/fruits-information/images/2/2b/Apple.jpg/revision/latest?cb=20180802112257",
                "Category 2",
                true),
            ProductModel("Oculus hand",
                "Oculus hand",
                50.0,
                8,
                "https://truespiritanimal.com/wp-content/uploads/2023/11/octopus-e985d752.webp",
                "Category 3",
                false)
        )

        for (product in products) {
            firestore.collection("products").add(product)
                .addOnSuccessListener {
                    Log.d("Firestore", "Product added with ID: ${it.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding product", e)
                }
        }

        fetchGroceries()
    }

    private fun deleteAllProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    firestore.collection("products").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Product successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting product", e)
                        }
                }

                fetchGroceries()
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }


}