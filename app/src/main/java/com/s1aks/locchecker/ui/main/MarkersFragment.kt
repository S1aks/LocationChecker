package com.s1aks.locchecker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.s1aks.locchecker.databinding.FragmentMarkersBinding

class MarkersFragment : Fragment() {
    private var _binding: FragmentMarkersBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("ViewBinding access error!")
    private lateinit var viewModel: MarkersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkersBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MarkersViewModel::class.java]
        return binding.root
    }

    companion object {
        fun newInstance() = MarkersFragment()
    }
}