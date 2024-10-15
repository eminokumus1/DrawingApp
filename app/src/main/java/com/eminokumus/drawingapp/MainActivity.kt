package com.eminokumus.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    Toast.makeText(
                        this,
                        "Permission granted, now you can read the storage files.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (permissionName == Manifest.permission.READ_MEDIA_IMAGES){
                        Toast.makeText(
                            this,
                            "Permission denied.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

        }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBrushAndColor()

        setBrushImageButtonOnClickListener()
        setColorsOnClickListeners()
        setGalleryImageButtonOnClickListener()


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

    private fun setGalleryImageButtonOnClickListener(){
        binding.galleryImageButton.setOnClickListener {
            requestStoragePermission()
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
            yellowColorImageButton.setOnClickListener {
                setColorOnClickListener(
                    yellowColorImageButton
                )
            }
            seaBlueColorImageButton.setOnClickListener {
                setColorOnClickListener(
                    seaBlueColorImageButton
                )
            }
            setRandomColorOnClickListener()
            whiteColorImageButton.setOnClickListener { setColorOnClickListener(whiteColorImageButton) }
        }
    }

    private fun setColorOnClickListener(colorButton: ImageButton) {
        binding.drawingView.setColor(colorButton.tag.toString())
        setPalletColorToPressed(colorButton)
        setPalletColorToNormal(currentColorImageButton!!)
        currentColorImageButton = colorButton

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

    private fun setRandomColorOnClickListener() {
        binding.randomColorImageButton.setOnClickListener {
            val randomColor = generateRandomColor()
            binding.drawingView.setColor(randomColor)
            setPalletColorToPressed(it as ImageButton)
            setPalletColorToNormal(currentColorImageButton!!)
            currentColorImageButton = it
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun generateRandomColor(): String {
        val randomColorInt =
            Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        val randomColorHex = randomColorInt.toHexString(HexFormat.Default)
        return StringBuilder("#").append(randomColorHex).toString()
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        ){
            showRationaleDialog("Kids Drawing App",
                "Kids Drawing App" + " needs to access your External Storage")
        }else{
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
                // TODO - Add writing external storage permission
            ))
        }
    }

    private fun showRationaleDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

}