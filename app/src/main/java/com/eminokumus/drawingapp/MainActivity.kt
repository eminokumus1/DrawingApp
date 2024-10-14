package com.eminokumus.drawingapp

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.eminokumus.drawingapp.databinding.ActivityMainBinding
import com.eminokumus.drawingapp.databinding.DialogBrushSizeBinding
import java.lang.StringBuilder
import kotlin.random.Random

private const val smallBrushSize = 10f
private const val mediumBrushSize = 20f
private const val largeBrushSize = 30f

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentColorImageButton: ImageButton? = null

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBrushAndColor()

        setBrushImageButtonOnClickListener()
        setColorsOnClickListeners()


        println("randomcolorHex: ${generateRandomColor()}")
    }

    private fun initializeBrushAndColor() {
        binding.drawingView.setSizeForBrush(mediumBrushSize)
        currentColorImageButton = binding.paintColorsLinearLayout[1] as ImageButton
        setPalletColorToPressed(currentColorImageButton!!)
    }

    private fun setBrushImageButtonOnClickListener() {
        binding.brushImageButton.setOnClickListener {
            showBrushSizeChooserDialog()
        }
    }

    private fun showBrushSizeChooserDialog() {
        val brushBinding = DialogBrushSizeBinding.inflate(layoutInflater)
        val brushDialog = Dialog(this)
        brushDialog.setContentView(brushBinding.root)
        brushDialog.setTitle("Brush size: ")
        setBrushDialogButtonsOnClickListeners(brushBinding, brushDialog)
        brushDialog.show()
    }

    private fun setBrushDialogButtonsOnClickListeners(
        brushBinding: DialogBrushSizeBinding,
        brushDialog: Dialog
    ) {
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

    private fun setColorsOnClickListeners() {
        binding.run {
            skinColorImageButton.setOnClickListener { setColorOnClickListener(skinColorImageButton) }
            blackColorImageButton.setOnClickListener { setColorOnClickListener(blackColorImageButton) }
            redColorImageButton.setOnClickListener { setColorOnClickListener(redColorImageButton) }
            greenColorImageButton.setOnClickListener { setColorOnClickListener(greenColorImageButton) }
            yellowColorImageButton.setOnClickListener { setColorOnClickListener(yellowColorImageButton) }
            seaBlueColorImageButton.setOnClickListener { setColorOnClickListener(seaBlueColorImageButton) }
            setRandomColorOnClickListener()
            whiteColorImageButton.setOnClickListener { setColorOnClickListener(whiteColorImageButton) }
        }
    }

    private fun setPalletColorToPressed(colorButton: ImageButton) {
        colorButton.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selected)
        )
    }

    private fun setPalletColorToNormal(colorButton: ImageButton) {
        colorButton.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_normal)
        )
    }

    private fun setColorOnClickListener(colorButton: ImageButton) {
        binding.drawingView.setColor(colorButton.tag.toString())
        setPalletColorToPressed(colorButton)
        setPalletColorToNormal(currentColorImageButton!!)
        currentColorImageButton = colorButton

    }

    private fun setRandomColorOnClickListener(){
        binding.randomColorImageButton.setOnClickListener{
            val randomColor = generateRandomColor()
            binding.drawingView.setColor(randomColor)
            setPalletColorToPressed(it as ImageButton)
            setPalletColorToPressed(currentColorImageButton!!)
            currentColorImageButton = it
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun generateRandomColor(): String{
        val randomColorInt = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        val randomColorHex = randomColorInt.toHexString(HexFormat.Default)
        return StringBuilder("#").append(randomColorHex).toString()
    }

}