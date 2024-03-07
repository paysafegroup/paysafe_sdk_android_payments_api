/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.util

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.paysafe.android.core.domain.exception.PaysafeException

class ErrorHandlingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val title = arguments?.getString(ERROR_HANDLING_DIALOG_TITLE_KEY)
        val message = arguments?.getString(ERROR_HANDLING_DIALOG_MSG_KEY)
        val actionText = arguments?.getString(ERROR_HANDLING_DIALOG_ACTION_TEXT_KEY)

        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(actionText) { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.add(this, tag)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    companion object {
        private const val DEFAULT_TITLE = "Error"
        private const val DEFAULT_POSITIVE_BUTTON_TEXT = "OK"
        internal const val TAG = "ErrorHandlingDialogTag"
        internal const val ERROR_HANDLING_DIALOG_TITLE_KEY = "errorHandlingDialogTitle"
        internal const val ERROR_HANDLING_DIALOG_MSG_KEY = "errorHandlingDialogMsg"
        internal const val ERROR_HANDLING_DIALOG_ACTION_TEXT_KEY = "errorHandlingDialogActionText"

        fun newInstance(
            exception: Exception,
            title: String = DEFAULT_TITLE,
            positiveButtonText: String = DEFAULT_POSITIVE_BUTTON_TEXT
        ): ErrorHandlingDialog {
            var message = exception.message ?: ""
            if (exception is PaysafeException) {
                message = exception.displayMessage
            }
            return creatingInstance(title, message, positiveButtonText)
        }

        private fun creatingInstance(
            title: String,
            message: String,
            positiveButtonText: String
        ): ErrorHandlingDialog {
            val fragment = ErrorHandlingDialog()
            val args = Bundle().apply {
                putString(ERROR_HANDLING_DIALOG_TITLE_KEY, title)
                putString(ERROR_HANDLING_DIALOG_MSG_KEY, message)
                putString(ERROR_HANDLING_DIALOG_ACTION_TEXT_KEY, positiveButtonText)
            }
            fragment.arguments = args
            return fragment
        }
    }
}