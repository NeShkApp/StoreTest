package org.bohdan.storetest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import org.bohdan.storetest.adapters.BannerAdapter
import org.bohdan.storetest.databinding.FragmentHomeBinding
import org.bohdan.storetest.models.Banner
import org.bohdan.storetest.viewmodels.BannerViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BannerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(BannerViewModel::class.java)
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

    private fun setupBanners(images: List<Banner>) {
        binding.promotionsViewPager.adapter = BannerAdapter(images, binding.promotionsViewPager)
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
}
