package com.eminokumus.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.eminokumus.drawingapp.databinding.ActivityMainBinding
import com.eminokumus.drawingapp.databinding.DialogBrushSizeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import kotlin.random.Random

private const val smallBrushSize = 10f
private const val mediumBrushSize = 20f
private const val largeBrushSize = 30f

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentColorImageButton: ImageButton? = null
    var customProgressDialog: Dialog? = null

    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                binding.backgroundImage.setImageURI(result.data?.data)
            }
        }

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
                    openGallery()
                } else {
                    if (permissionName == Manifest.permission.READ_MEDIA_IMAGES) {
                        Toast.makeText(
                            this,
                            "Permission denied.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBrushAndColor()

        setColorsOnClickListeners()
        setBrushImageButtonOnClickListener()
        setGalleryImageButtonOnClickListener()
        setUndoImageButtonOnClickListener()
        setSaveImageButtonOnClickListener()


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

    private fun setGalleryImageButtonOnClickListener() {
        binding.galleryImageButton.setOnClickListener {
            requestStoragePermission()
        }
    }

    private fun setUndoImageButtonOnClickListener() {
        binding.undoImageButton.setOnClickListener {
            binding.drawingView.onClickUndo()
        }
    }

    private fun setSaveImageButtonOnClickListener(){
        binding.saveImageButton.setOnClickListener {
            showProgressDialog()
            if (isReadStorageAllowed()){
                lifecycleScope.launch {
                    val bitmapFromView = getBitmapFromView(binding.drawingView)
                    saveBitmapFile(bitmapFromView)
                }
            }
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

    private fun isReadStorageAllowed(): Boolean{
        val result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_MEDIA_IMAGES)

        return result == PackageManager.PERMISSION_GRANTED
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
        ) {
            showRationaleDialog(
                "Kids Drawing App",
                "Kids Drawing App" + " needs to access your External Storage"
            )
        } else {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
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

    private fun showProgressDialog(){
        customProgressDialog = Dialog(this)
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        customProgressDialog?.show()
    }

    private fun cancelProgressDialog(){
        if (customProgressDialog != null){
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    private fun openGallery() {
        val pickIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(pickIntent)
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val background = view.background
        if (background != null){
            background.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return returnedBitmap
    }

    private suspend fun saveBitmapFile(bitmap : Bitmap?): String{
        var filePath = ""
        withContext(Dispatchers.IO){
            if(bitmap != null){
                try{
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    val file = createFile()
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(bytes.toByteArray())
                    fileOutputStream.close()

                    filePath = file.absolutePath
                    runOnUiThread {
                        cancelProgressDialog()
                        popUpImageShareIfSaveSuccessful(filePath)
                    }
                }catch (e: Exception){
                    filePath = ""
                    e.printStackTrace()
                }
            }
        }
        return filePath
    }

    private fun popUpImageShareIfSaveSuccessful(filePath: String) {
        if (filePath.isNotEmpty()) {
            Toast.makeText(
                this@MainActivity,
                "File saved successfully: $filePath",
                Toast.LENGTH_SHORT
            ).show()
            shareImage(filePath)
        } else {
            Toast.makeText(
                this@MainActivity,
                "Something went wrong while saving the file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createFile() = File(
        externalCacheDir?.absoluteFile.toString()
                + File.separator + "KidsDrawingApp_" + System.currentTimeMillis() / 1000 + ".png"
    )

    private fun shareImage(filePath: String){
        MediaScannerConnection.scanFile(this, arrayOf(filePath), null){ path, uri ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }



}