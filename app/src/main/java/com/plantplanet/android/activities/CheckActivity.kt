package com.plantplanet.android.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.plantplanet.android.R
import com.plantplanet.android.utils.CustomModel
import com.plantplanet.android.models.Result
import com.plantplanet.android.utils.FirebaseHelper
import com.squareup.picasso.Picasso
import io.fotoapparat.Fotoapparat
import kotlinx.android.synthetic.main.activity_check.*
import java.text.DecimalFormat

class CheckActivity : AppCompatActivity() {

    private lateinit var camera: Fotoapparat
    private lateinit var cropReference: String
    private lateinit var cropName: String
    private lateinit var customModel: CustomModel
    private lateinit var bitmap: Bitmap
    private lateinit var resultList: List<Result>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        getCrop()
        getMode()
    }

    private fun getCrop() {
        val sharedPreferences = getSharedPreferences("crop", Context.MODE_PRIVATE)
        cropReference = sharedPreferences.getString("reference", "")!!
        cropName = sharedPreferences.getString("name", "")!!.toLowerCase()
    }

    private fun getMode() {
        val image = intent.getStringExtra("mode")
        if (image == "import") {
            importImage()
        } else {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        else
            startCamera()
    }

    private fun importImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    private fun startCamera() {
        camera = Fotoapparat(this, cameraView)
        camera.start()
        buttonCheck.hide()
        buttonCapture.show()
        imagePreview.visibility = View.GONE
        buttonCapture.isClickable = true
        buttonCapture.setOnClickListener { captureImage() }
    }

    private fun showImage(data: Uri) {
        val stream = contentResolver.openInputStream(data)
        bitmap = BitmapFactory.decodeStream(stream)
        imagePreview.visibility = View.VISIBLE
        Picasso.get().load(data).fit().centerCrop().into(imagePreview)
        buttonCapture.hide()
        buttonCheck.show()
        buttonCheck.setOnClickListener { isPlant() }
    }

    private fun captureImage() {
        buttonCapture.isClickable = false
        buttonCapture.hide()
        progressCheck.visibility = View.VISIBLE
        camera.takePicture().toBitmap().whenAvailable {
            camera.stop()
            bitmap = it!!.bitmap
            val degrees = -it.rotationDegrees.toFloat()
            val matrix = Matrix().apply { postRotate(degrees) }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            progressCheck.visibility = View.GONE
            imagePreview.visibility = View.VISIBLE
            imagePreview.setImageBitmap(bitmap)
            buttonCheck.show()
            buttonCheck.setOnClickListener { isPlant() }
        }
    }

    private fun cropToSquare(): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newWidth = if (height > width) width else height
        val newHeight = if (height > width) height - (height - width) else height
        var cropWidth = (width - height) / 2
        cropWidth = if (cropWidth < 0) 0 else cropWidth
        var cropHeight = (height - width) / 2
        cropHeight = if (cropHeight < 0) 0 else cropHeight
        return Bitmap.createBitmap(bitmap, cropWidth, cropHeight, newWidth, newHeight)
    }

    private fun isPlant() {
        val square = cropToSquare()
        buttonCheck.isClickable = false
        buttonCheck.hide()
        progressCheck.visibility = View.VISIBLE
        val isPlant = CustomModel(bitmap, "plant", 224, 2)
        isPlant.configureCloudModel().addOnSuccessListener {
            isPlant.runInterpreter().addOnSuccessListener {
                val result = it.getOutput<Array<FloatArray>>(0)[0]
                val index = result.indexOf(result.max()!!)
                if (index == 0) checkImage(square)
                else if (index != 0 && result.max()!! < 0.7f) checkImage(square)
                else {
                    Toast.makeText(applicationContext, getString(R.string.unknown_disease), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } .addOnFailureListener { noInternet() }
    }

    private fun checkImage(bitmap: Bitmap) {
        FirebaseHelper().loadCollection(cropName, "resultIndex").addOnSuccessListener {
            resultList = it.toObjects(Result::class.java)
            if (resultList.isEmpty()) noInternet()
            else {
                customModel = CustomModel(bitmap, cropReference, 299, resultList.size)
                customModel.configureCloudModel().addOnSuccessListener { getResult() }
                    .addOnFailureListener { noInternet() }
            }
        }
    }

    private fun getResult() {
        customModel.runInterpreter().addOnSuccessListener {
            val floatArray = it.getOutput<Array<FloatArray>>(0)[0]
            for (i in 0 until floatArray.size) {
                resultList[i].resultFloat = floatArray[i]
                resultList[i].resultPercentage = DecimalFormat("##.##%").format(floatArray[i])
            }
            showResults()
        }
    }

    private fun noInternet() {
        Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showResults() {
        val intent = Intent(this, ResultActivity::class.java)
        ResultActivity.resultList = resultList
        ResultActivity.bitmap = bitmap
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startCamera()
        else
            Toast.makeText(applicationContext, getString(R.string.camera_required), Toast.LENGTH_SHORT).show()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK)
            showImage(data!!.data!!)
        else finish()
    }

    override fun onBackPressed() {
        val mode = intent.getStringExtra("mode")
        when {
            mode == "camera" && buttonCapture.isShown -> finish()
            mode == "camera" && buttonCheck.isShown -> startCamera()
            mode == "import" && buttonCheck.isShown -> finish()
        }
    }
}