package com.s1aks.locchecker.ui.edit_dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.s1aks.locchecker.R
import com.s1aks.locchecker.databinding.DialogAddMarkerBinding
import com.s1aks.locchecker.domain.entities.MapPosition

class EditMarkerDialogFragment(
    private val marker: MapPosition?,
    private val saveClickListener: OnSaveClickListener
) : DialogFragment() {
    private var _binding: DialogAddMarkerBinding? = null
    private val binding
        get() = _binding
            ?: throw RuntimeException(getString(R.string.fragment_binding_exception_message))

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddMarkerBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(getString(R.string.dialog_title))
            .setView(binding.root)
            .setPositiveButton(
                getString(R.string.dialog_save_button_text)
            ) { _, _ ->
                saveClickListener.onSaveClicked(
                    binding.markerTitle.text.toString(),
                    binding.markerInformation.text.toString()
                )
            }
            .setNegativeButton(getString(R.string.dialog_cancel_button_text), null)
            .create()
            .apply {
                marker?.let {
                    binding.markerTitle.setText(it.title)
                    binding.markerInformation.setText(it.information)
                }
                binding.markerTitle.addTextChangedListener {
                    this.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                        it?.isNotBlank() ?: false
                }
            }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}

interface OnSaveClickListener {
    fun onSaveClicked(title: String, information: String)
}