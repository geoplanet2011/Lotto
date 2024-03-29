package ge.gogichaishvili.lotto.app.tools

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import ge.gogichaishvili.lotto.R

import java.util.*

class DatePickerHelper(context: Context, isSpinnerType: Boolean = false) {

    private var dialog: DatePickerDialog
    private var callback: Callback? = null

    private val listener = OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
        callback?.onDateSelected(dayOfMonth, monthOfYear, year)
    }

    init {

        //my custom style
        val style = if (isSpinnerType) R.style.SpinnerDatePickerDialog else 0

        //val style = if (isSpinnerType) android.R.style.Theme_Holo_Light_Dialog_MinWidth else 0


        val cal = Calendar.getInstance()

        dialog = DatePickerDialog(
            context, style, listener,
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )

        //if set "Theme_Holo_Light_Dialog_MinWidth", then set transparent background
        //dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun showDialog(dayofMonth: Int, month: Int, year: Int, callback: Callback?) {
        this.callback = callback
        dialog.datePicker.init(year, month, dayofMonth, null)
        dialog.show()
    }

    fun setMinDate(minDate: Long) {
        dialog.datePicker.minDate = minDate
    }

    fun setMaxDate(maxDate: Long) {
        dialog.datePicker.maxDate = maxDate
    }

    interface Callback {
        fun onDateSelected(dayofMonth: Int, month: Int, year: Int)
    }
}