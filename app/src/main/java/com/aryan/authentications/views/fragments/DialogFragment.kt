package com.aryan.authentications.views.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.aryan.authentications.R

class DialogFragment(private val onConfirmListener:()->Unit) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { dialog, id ->
                    onConfirmListener.invoke()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // User cancelled the dialog
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}