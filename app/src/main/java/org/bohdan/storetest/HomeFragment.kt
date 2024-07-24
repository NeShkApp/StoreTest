package org.bohdan.storetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.bohdan.storetest.adapters.BannerAdapter
import org.bohdan.storetest.adapters.CategoryAdapter
import org.bohdan.storetest.adapters.ProductAdapter
import org.bohdan.storetest.databinding.FragmentHomeBinding
import org.bohdan.storetest.models.BannerModel
import org.bohdan.storetest.models.ProductModel
import org.bohdan.storetest.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterAllGroceries: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        adapterAllGroceries = ProductAdapter(listOf())

        binding.btnLogout.setOnClickListener{
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), Login::class.java))
            requireActivity().finish()
        }

        binding.btnAddProducts.setOnClickListener {
            addProducts()
        }

        binding.btnDeleteProducts.setOnClickListener {
            deleteAllProducts()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        //RecView settings
        val recViewCategories = binding.recViewAllCategories
        val categoryAdapter = CategoryAdapter(emptyList())

        recViewCategories.layoutManager =LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recViewCategories.adapter = categoryAdapter

        viewModel.items.observe(viewLifecycleOwner) { items ->
            categoryAdapter.items = items
            categoryAdapter.notifyDataSetChanged()
            binding.progressBarCategories.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        val recViewProducts = binding.recViewAllProducts
        val productsAdapter = ProductAdapter(emptyList())

        recViewProducts.layoutManager =LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recViewProducts.adapter = productsAdapter

        viewModel.products.observe(viewLifecycleOwner) { items ->
            productsAdapter.groceries = items
            productsAdapter.notifyDataSetChanged()
            binding.progressBarAllProducts.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        initBanners()
    }

    private fun initBanners() {
        binding.progressBarBanners.visibility = View.VISIBLE
        viewModel.banners.observe(viewLifecycleOwner, Observer { banners ->
            banners?.let {
                setupBanners(it)
                binding.progressBarBanners.visibility = View.GONE
            }
        })
        viewModel.loadBanners()
    }

    private fun setupBanners(images: List<BannerModel>) {
        binding.promotionsViewPager.adapter = BannerAdapter(images)
        binding.promotionsViewPager.clipToPadding = false
        binding.promotionsViewPager.clipChildren = false
        binding.promotionsViewPager.offscreenPageLimit = 3
        binding.promotionsViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.promotionsViewPager.setPageTransformer(compositePageTransformer)

        if (images.isNotEmpty()) {
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.dotsIndicator.attachTo(binding.promotionsViewPager)
        } else {
            binding.dotsIndicator.visibility = View.GONE
        }
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

        viewModel.addProducts(products)
    }

    private fun deleteAllProducts() {
        viewModel.deleteAllProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
