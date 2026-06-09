package com.rpgmanager.ui.groups

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.rpgmanager.R

class DeleteConfirmDialog(
    private val title: String,
    private val message: String,
    private val onConfirm: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.delete_confirm)) { _, _ -> onConfirm() }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
    }
}
