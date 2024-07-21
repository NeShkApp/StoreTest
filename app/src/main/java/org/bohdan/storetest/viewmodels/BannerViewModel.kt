//package org.bohdan.storetest.viewmodels
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import org.bohdan.storetest.models.Banner
//
//class BannerViewModel: ViewModel() {
//
//    private val firebaseDatabase= FirebaseDatabase.getInstance()
//    private val _banner = MutableLiveData<List<Banner>>()
//    val banners: LiveData<List<Banner>> = _banner
//    fun loadBanners () {
//        val ref=firebaseDatabase.getReference( "banners")
//        ref.addValueEventListener (object: ValueEventListener {
//            override fun onDataChange (snapshot: DataSnapshot) {
//                val lists = mutableListOf<Banner>()
//                for (childSnapshot in snapshot.children) {
//                    val banner= childSnapshot.getValue(Banner::class.java)
//                    if(banner!=null){
//                        lists.add(banner)
//                    }
//                    _banner.value = lists
//                }
//            }
//            override fun onCancelled (error: DatabaseError) {
//
//            }
//        })
//    }
//}
package org.bohdan.storetest.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import org.bohdan.storetest.models.Banner

class BannerViewModel : ViewModel() {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val _banners = MutableLiveData<List<Banner>>()
    val banners: LiveData<List<Banner>> = _banners

    fun loadBanners() {
        val storageRef = firebaseStorage.reference
        storageRef.listAll().addOnSuccessListener { listResult ->
            processListResult(listResult)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun processListResult(listResult: ListResult) {
        val bannerList = mutableListOf<Banner>()
        val items = listResult.items

        if (items.isNotEmpty()) {
            for (item in items) {
                item.downloadUrl.addOnSuccessListener { uri ->
                    val banner = Banner(url = uri.toString())
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
