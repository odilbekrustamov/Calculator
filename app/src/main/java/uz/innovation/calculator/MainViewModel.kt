package uz.innovation.calculator

import android.app.AlertDialog
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.floor

class MainViewModel: ViewModel() {
    private val _viewNumber = MutableStateFlow("0")
    val viewNumber get() = _viewNumber.asStateFlow()

    private val _viewOperator = MutableStateFlow("")
    val viewOperator get() = _viewOperator.asStateFlow()

    private var mathExpression = ""
    private var newOperator = false
    private var lastNumber = ""
    private var lastOperator = ""
    private var memoryValue: String? = null

    fun addToMemory() {
        memoryValue = try {
            val currentExpression = _viewNumber.value.removeLeadingSpaces().toString()
            val currentValue = memoryValue?.toDoubleOrNull() ?: 0.0
            val subtractedValue = (currentValue + currentExpression.toDouble())
            newOperator = false
            lastOperator = ""
            mathExpression = ""
            lastNumber = ""
            _viewOperator.value = "M+"
            subtractedValue.toString()
        } catch (e: Exception) {
            null
        }
    }

    fun subtractFromMemory() {
        memoryValue = try {
            val currentExpression = _viewNumber.value.removeLeadingSpaces().toString()
            val currentValue = memoryValue?.toDoubleOrNull() ?: 0.0
            val subtractedValue = (currentValue - currentExpression.toDoubleOrNull()!!)
            newOperator = false
            lastOperator = ""
            mathExpression = ""
            lastNumber = ""
            _viewOperator.value = "M-"
            subtractedValue.toString()
        } catch (e: Exception) {
            null
        }
    }

    fun clearMemory() {
        memoryValue = null
    }

    fun handleMR() {
        memoryValue?.let {
            if (mathExpression.endsWithOperator()) {
                newOperator = true
                lastNumber = it
            } else {
                newOperator = false
                lastOperator = ""
                mathExpression = memoryValue as String
            }
            _viewNumber.value = memoryValue!!.formatNumber()
        }
    }

    fun handleDigit(n: String) {
        if (!newOperator) {
            newOperator = true
            _viewNumber.value = ""
        }
        val string = _viewNumber.value
        if (n == ".") {
            if (string == "0" || string == "00") {
                _viewNumber.value = "0."
            } else {
                var lastDot = false
                for (i in string) {
                    if (i.isDigit())
                        continue
                    lastDot = i == '.'
                }
                if (lastDot) {
                    return
                } else
                    _viewNumber.value += n
            }
        } else if (n == "0") {
            if (string == "0") {
                _viewNumber.value = n
            } else {
                _viewNumber.value += n
            }
        } else if (n == "00") {
            if (string != "0" && string != "") {
                _viewNumber.value += n
            } else {
                _viewNumber.value = "0"
            }
        } else {
            if (string == "0") {
                _viewNumber.value = n
            } else {
                _viewNumber.value += n
            }
        }
        lastNumber = _viewNumber.value
    }

    fun addOperator(c: Char) {
        if (newOperator) {
            newOperator = false
            if (c == '%') {
                lastNumber = _viewNumber.value + '%'
                newOperator = true
                var res = reevaluate(mathExpression)
                res = reevaluate(res + lastOperator + res + "*" + _viewNumber.value + "/100")
                newOperator = false
                mathExpression = res
                _viewNumber.value = res.formatNumber()
            } else if ((lastOperator == "+" || lastOperator == "-") && (c.toString() == "*" || c.toString() == "/")) {
                mathExpression = mathExpression + lastOperator + _viewNumber.value
                lastOperator = c.toString()
            } else if (lastOperator == "*" || lastOperator == "/") {
                mathExpression = mathExpression + lastOperator + _viewNumber.value
                val res = reevaluate(mathExpression.extractLastSegment())
                _viewNumber.value = res.formatNumber()
                lastOperator = c.toString()
            } else {
                mathExpression = mathExpression + lastOperator + _viewNumber.value
                val res = reevaluate(mathExpression)
                _viewNumber.value = res.formatNumber()
                lastOperator = c.toString()
            }
        }
        _viewOperator.value = c.toString()
    }

    private fun reevaluate(mathExpr: String): String {
        return try {
            val expression = ExpressionBuilder(mathExpr).build()
            val result = expression.evaluate()
            if (floor(result) == result)
                result.toString()
            else
                result.toString()
        } catch (e: Exception) {
            "0"
        }
    }

    fun equate() {
        Log.d("tetstt", "equate: ${mathExpression + lastOperator + lastNumber}")
        val res = reevaluate(mathExpression + lastOperator + lastNumber)
        newOperator = false
        mathExpression = res
        _viewNumber.value = res.formatNumber()
        _viewOperator.value = "="
    }

    fun reset() {
        _viewNumber.value = "0"
        newOperator = false
        lastOperator = ""
        mathExpression = ""
        lastNumber = ""
        _viewOperator.value = ""
    }

    fun showDialog(requireActivity: FragmentActivity) {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity, R.style.DialogTheme)
        alertDialogBuilder.setMessage("Максимальная сумма ввода \n 999 999 999,99")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            resetValues()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun resetValues() {
        _viewNumber.value = "0"
        mathExpression = ""
        newOperator = false
        lastNumber = ""
        lastOperator = ""
        memoryValue = null
    }
}