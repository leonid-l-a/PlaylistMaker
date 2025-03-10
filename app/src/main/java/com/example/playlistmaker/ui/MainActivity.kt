package com.example.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.main.NavigationEvent

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    private val viewModel: MainViewModel by lazy {
        Creator.provideMainViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.navigationEvent.observe(this, Observer { event ->
            when (event) {
                is NavigationEvent.ToSearchActivity -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                }
                is NavigationEvent.ToLibraryActivity -> {
                    startActivity(Intent(this, LibraryActivity::class.java))
                }
                is NavigationEvent.ToSettingsActivity -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
        })

        binding.buttonSearchActivity.setOnClickListener {
            viewModel.onSearchButtonClicked()
        }
        binding.buttonLibraryActivity.setOnClickListener {
            viewModel.onLibraryButtonClicked()
        }
        binding.buttonSettingsActivity.setOnClickListener {
            viewModel.onSettingsButtonClicked()
        }
    }
}
