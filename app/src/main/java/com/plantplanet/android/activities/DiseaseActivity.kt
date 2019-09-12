package com.plantplanet.android.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.plantplanet.android.R
import com.plantplanet.android.models.Disease
import com.plantplanet.android.utils.FirebaseHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_disease.*
import java.io.File
import java.util.*

class DiseaseActivity : AppCompatActivity() {

    private lateinit var speech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease)
        getDisease()
        speech = TextToSpeech(this, null)
    }

    private fun getDisease() {
        val diseaseName = intent.getStringExtra("disease")
        if (diseaseName == null) showDisease()
        else {
            layoutDisease.visibility = View.GONE
            progressDisease.visibility = View.VISIBLE
            FirebaseHelper().searchDocument(getString(R.string.firestore_library), "diseaseName", diseaseName).addOnSuccessListener {
                val diseasesList = it.toObjects(Disease::class.java)
                if (diseasesList.isEmpty()) noInternet()
                else {
                    disease = diseasesList[0]
                    showDisease()
                }
            }
        }
    }

    private fun noInternet() {
        Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
        finish()
    }

    private fun hearDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.hear)
        builder.setItems(arrayOf(getString(R.string.symptoms), getString(R.string.comments), getString(R.string.management))) { dialogInterface, i ->
            hearDisease(i)
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun hearDisease(i: Int) {
        val text = when (i) {
            0 -> disease.diseaseSymptoms!!
            1 -> disease.diseaseComments!!
            else -> disease.diseaseManagement!!
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        else
            Toast.makeText(applicationContext, R.string.not_supported, Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        speech.stop()
        super.onStop()
    }

    private fun showDisease() {
        progressDisease.visibility = View.GONE
        layoutDisease.visibility = View.VISIBLE
        val file = File(filesDir, disease.diseaseImage!!)
        if (file.exists()) Picasso.get().load(file).fit().centerCrop().into(imageDisease)
        else FirebaseHelper().loadFile(this, "diseases", disease.diseaseImage!!).addOnSuccessListener {
            Picasso.get().load(file).fit().centerCrop().into(imageDisease)
        }
        textDisease.text = disease.diseaseName!!
        textType.text = disease.diseaseCategory
        textSymptoms.text = disease.diseaseSymptoms
        textComments.text = disease.diseaseComments
        textManagement.text = disease.diseaseManagement
        buttonHear.setOnClickListener {
            if (Locale.getDefault().language == Locale.ENGLISH.language)
                hearDialog()
            else Toast.makeText(applicationContext, R.string.language_not_supported, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        lateinit var disease: Disease
    }
}
