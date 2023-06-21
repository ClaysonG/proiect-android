package com.example.partyfinder.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    interface DateSelectionListener {
        fun onDateSelected(year: Int, month: Int, day: Int)
    }

    private var dateSelectionListener: DateSelectionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            dateSelectionListener = context as DateSelectionListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DateSelectionListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

        dateSelectionListener?.onDateSelected(year, month, day)
    }
}