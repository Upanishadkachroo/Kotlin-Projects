package com.example.ml_app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.ml_app.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private var bitmap: Bitmap? = null
    private var currentPhotoPath: String = ""

    private val REQUEST_CAMERA_PERMISSION = 200
    private val GALLERY_REQUEST_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.textViewResult)

        val buttonPickImage = findViewById<Button>(R.id.buttonPickImage)
        val buttonDetect = findViewById<Button>(R.id.buttonDetect)

        buttonPickImage.setOnClickListener {
            showImagePickerDialog()
        }

        buttonDetect.setOnClickListener {
            bitmap?.let {
                runModel(it)
            } ?: run {
                resultTextView.text = "Please select an image first."
            }
        }

        requestPermissions()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Image")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.e("CameraError", "Error creating file: ${ex.message}")
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "$packageName.provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir("Pictures")!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = when (requestCode) {
                GALLERY_REQUEST_CODE -> data?.data
                CAMERA_REQUEST_CODE -> Uri.fromFile(File(currentPhotoPath))
                else -> null
            }

            imageUri?.let {
                bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                }

                imageView.setImageBitmap(bitmap)
                resultTextView.text = "Image selected. Tap 'Run Detection'."
            }
        }
    }

    private fun runModel(bitmap: Bitmap) {
        try {
            val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val byteBuffer = convertBitmapToByteBuffer(resized)

            val model = ModelUnquant.newInstance(this)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val outputArray = outputFeature0.floatArray

            // 🔥 Load labels from assets
            val labels = application.assets.open("labels.txt").bufferedReader().readLines()

            // 🔥 Get max index
            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
            val predictedLabel = if (maxIndex in labels.indices) labels[maxIndex] else "Unknown"
            val confidence = outputArray[maxIndex] * 100

            val resultText = "Prediction: $predictedLabel\nConfidence: ${"%.2f".format(confidence)}%"
            resultTextView.text = resultText

            model.close()

        } catch (e: Exception) {
            resultTextView.text = "Error running model: ${e.localizedMessage}"
            Log.e("ModelError", e.stackTraceToString())
        }
    }


    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)
        var pixelIndex = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val pixel = intValues[pixelIndex++]
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f)
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)
                byteBuffer.putFloat((pixel and 0xFF) / 255.0f)
            }
        }
        return byteBuffer
    }
}
