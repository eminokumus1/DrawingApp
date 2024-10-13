package com.eminokumus.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import com.eminokumus.drawingapp.databinding.ActivityMainBinding
import com.eminokumus.drawingapp.databinding.DialogBrushSizeBinding

private const val smallBrushSize = 10f
private const val mediumBrushSize = 20f
private const val largeBrushSize = 30f

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentColorImageButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.setSizeForBrush(mediumBrushSize)
        currentColorImageButton = binding.paintColorsLinearLayout[1] as ImageButton
        setPalletColorToPressed(currentColorImageButton!!)

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
        setBrushDialogButtonsOnClickListeners(brushBinding, brushDialog)
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

    private fun setPalletColorToPressed(colorButton: ImageButton){
        colorButton.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selected)
        )
    }
    private fun setPalletColorToNormal(colorButton: ImageButton){
        colorButton.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_normal)
        )
    }
}