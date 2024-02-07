package uz.innovation.calculator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.innovation.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        clickController()
    }

    private fun initViews() {
        viewModel.viewNumber
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { number ->
                if (number.isWholePartSizeGreaterThanNine())
                    viewModel.showDialog(this)

                binding.tvExpression.text = number
            }
            .launchIn(lifecycleScope)

        viewModel.viewOperator
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                binding.tvOperator.text = it
            }
            .launchIn(lifecycleScope)
    }

    private fun clickController() {
        for (i in 0 until binding.grid.childCount) {
            val button = binding.grid.getChildAt(i) as AppCompatButton
            button.setOnClickListener {
                onButtonClick(button)
            }
        }
    }

    private fun onButtonClick(button: AppCompatButton) {
        val buttonText = button.text.toString()
        when {
            buttonText.isDigitsOnly() -> viewModel.handleDigit(buttonText)
            buttonText == "," -> viewModel.handleDigit(".")
            buttonText == "+" -> viewModel.addOperator('+')
            buttonText == "—" -> viewModel.addOperator('-')
            buttonText == "×" -> viewModel.addOperator('*')
            buttonText == "÷" -> viewModel.addOperator('/')
            buttonText == "%" -> viewModel.addOperator('%')
            buttonText == "=" -> viewModel.equate()
            buttonText == "MR" -> viewModel.handleMR()
            buttonText == "AC" -> viewModel.reset()
            buttonText == "M+" -> viewModel.addToMemory()
            buttonText == "M-" -> viewModel.subtractFromMemory()
            buttonText == "MC" -> viewModel.clearMemory()
        }
    }
}