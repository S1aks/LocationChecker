package com.s1aks.locchecker.ui.markers

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.s1aks.locchecker.R
import com.s1aks.locchecker.databinding.FragmentMarkersBinding
import com.s1aks.locchecker.domain.entities.MapPosition
import com.s1aks.locchecker.ui.base.BaseFragment
import com.s1aks.locchecker.ui.edit_dialog.EditMarkerDialogFragment
import com.s1aks.locchecker.ui.edit_dialog.OnSaveClickListener
import com.s1aks.locchecker.ui.map.MapFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarkersFragment : BaseFragment<FragmentMarkersBinding>(FragmentMarkersBinding::inflate),
    OnItemClickListener, OnSaveClickListener {
    private val markersViewModel: MarkersViewModel by viewModel()
    private val adapter: MarkersAdapter by lazy {
        MarkersAdapter(menuInflater = requireActivity().menuInflater, this)
    }
    private var marker: MapPosition? = null

    override fun readArguments(bundle: Bundle) {
    }

    override fun initView() {
        registerForContextMenu(binding.recyclerView)
        binding.recyclerView.adapter = adapter
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.bar_title_markers)
    }

    override fun initListeners() {
    }

    override fun initObservers() {
        lifecycleScope.launch {
            markersViewModel.data
                .observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
        }
        markersViewModel.getAllMarkers()
    }

    override fun onSaveClicked(title: String, information: String) {
        marker?.let {
            it.title = title
            it.information = information
            adapter.updateItem(it)
            markersViewModel.saveMarker(marker!!)
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_save_marker_text),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        marker = adapter.currentList[adapter.itemPosition]
        when (item.itemId) {
            R.id.edit -> {
                EditMarkerDialogFragment(
                    marker, this
                ).show(requireActivity().supportFragmentManager, "")
            }
            R.id.delete -> {
                adapter.removeItem()
                marker?.let { markersViewModel.deleteMarker(it.id) }
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onItemClicked(itemId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, MapFragment.newInstance(itemId))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = MarkersFragment()
    }
}