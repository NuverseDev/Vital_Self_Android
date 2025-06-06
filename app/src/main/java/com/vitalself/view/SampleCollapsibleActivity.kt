package com.vitalself.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vitalself.databinding.ItemMeasurementBinding
import com.vitalself.databinding.SampleCollapsingLayoutBinding
import com.google.android.material.appbar.AppBarLayout


//class CollapsibleActivity : AppCompatActivity() {
//
//    private lateinit var binding: SampleCollapsingLayoutBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = SampleCollapsingLayoutBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val items = List(30) { "Item ${it + 1}" }
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = SimpleAdapter(items)
//
//        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//            if (Math.abs(verticalOffset) == appBarLayout.totalScrollRange) {
//                // Fully Collapsed - Fade In
//                binding.pageTitle.animate()
//                    .alpha(1f)
//                    .setDuration(500)
//                    .start()
//            } else if (verticalOffset == 0) {
//                // Fully Expanded - Fade Out
//                binding.pageTitle.animate()
//                    .alpha(0f)
//                    .setDuration(100)
//                    .start()
//            } else {
//                // Middle scroll - Fade Out (optional)
//                binding.pageTitle.animate()
//                    .alpha(0f)
//                    .setDuration(500)
//                    .start()
//            }
//        })
//
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
//}
//
//
//class SimpleAdapter(private val items: List<String>) :
//    RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemMeasurementBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemMeasurementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//    }
//}
//
