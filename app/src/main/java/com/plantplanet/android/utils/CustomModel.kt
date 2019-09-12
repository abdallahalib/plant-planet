package com.plantplanet.android.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*

class CustomModel(var bitmap: Bitmap, var modelName: String, var inputSize: Int, var outputSize: Int) {

    private fun inputOutputOptions(): FirebaseModelInputOutputOptions {
        return FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, inputSize, inputSize, 3))
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, outputSize))
            .build()
    }

    private fun configureInput(): Array<Array<Array<FloatArray>>> {
        val bitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val batchNum = 0
        val input = Array(1) { Array(inputSize) { Array(inputSize) { FloatArray(3) } } }
        for (x in 0 until bitmap.height) {
            for (y in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                input[batchNum][x][y][0] = (Color.red(pixel)) / 255.0f
                input[batchNum][x][y][1] = (Color.green(pixel)) / 255.0f
                input[batchNum][x][y][2] = (Color.blue(pixel)) / 255.0f
            }
        }
        return input
    }

    fun configureCloudModel(): Task<Void> {
        val cloudModel = FirebaseCustomRemoteModel.Builder(modelName).build()
        val conditions = FirebaseModelDownloadConditions.Builder().build()
        return FirebaseModelManager.getInstance().download(cloudModel, conditions)
    }

    fun runInterpreter(): Task<FirebaseModelOutputs> {
        val image = configureInput()
        val cloudModel = FirebaseCustomRemoteModel.Builder(modelName).build()
        val ioOptions = inputOutputOptions()
        val interpreterOptions = FirebaseModelInterpreterOptions.Builder(cloudModel).build()
        val interpreter = FirebaseModelInterpreter.getInstance(interpreterOptions)
        val modelInputs = FirebaseModelInputs.Builder().add(image).build()
        return interpreter!!.run(modelInputs, ioOptions)
    }
}