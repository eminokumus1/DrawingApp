package com.eminokumus.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eminokumus.drawingapp.databinding.ActivityMainBinding
import com.eminokumus.drawingapp.databinding.DialogBrushSizeBinding

private const val smallBrushSize = 10f
private const val mediumBrushSize = 20f
private const val largeBrushSize = 30f

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.setSizeForBrush(20.toFloat())

        setBrushImageButtonOnClickListener()
    }

    private fun setBrushImageButtonOnClickListener(){
        binding.brushImageButton.setOnClickListener{
            showBrushSizeChooserDialog()
        }
    }

    private fun showBrushSizeChooserDialog(){
        val brushBinding = DialogBrushSizeBinding.inflate(layoutInflater)
        val brushDialog = Dialog(this)
        brushDialog.setContentView(brushBinding.root)
        brushDialog.setTitle("Brush size: ")
        brushBinding.smallBrushImageButton.setOnClickListener {
            binding.drawingView.setSizeForBrush(smallBrushSize)
            brushDialog.dismiss()
        }
        brushBinding.mediumBrushImageButton.setOnClickListener {
            binding.drawingView.setSizeForBrush(mediumBrushSize)
            brushDialog.dismiss()
        }
        brushBinding.largeBrushImageButton.setOnClickListener {
            binding.drawingView.setSizeForBrush(largeBrushSize)
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    private fun setBrushDialogButtonsOnClickListeners(brushBinding: DialogBrushSizeBinding, brushDialog: Dialog) {
        brushBinding.smallBrushImageButton.setOnClickListener {
            binding.drawingView.setSizeForBrush(smallBrushSize)
            brushDialog.dismiss()
        }
        brushBinding.mediumBrushImageButton.setOnClickListener {
            binding.drawingView.setSizeForBrush(mediumBrushSize)
            brushDialog.dismiss()
        }
        brushBinding.largeBrushImageButton.setOnClickListener {
            binding.drawingView.setSizeForBrush(largeBrushSize)
            brushDialog.dismiss()
        }
    }
}