package com.example.playlistmaker.ui.library


import android.os.Bundle
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.presentation.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : BaseActivity() {

    private lateinit var binding: ActivityLibraryBinding

    private val tabTitles = arrayOf("Избранные треки", "Плейлисты" )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.searchScreenToolbar)
        supportActionBar?.apply {
            title = getString(R.string.library)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.searchScreenToolbar.setNavigationOnClickListener {
            finish()
        }

        val adapter = LibraryViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }


}
