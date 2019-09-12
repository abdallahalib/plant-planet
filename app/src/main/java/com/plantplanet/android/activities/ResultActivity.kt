package com.plantplanet.android.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantplanet.android.R
import com.plantplanet.android.adapters.ResultAdapter
import com.plantplanet.android.models.History
import com.plantplanet.android.models.Result
import com.plantplanet.android.utils.DatabaseHelper
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.Collections.sort

class ResultActivity : AppCompatActivity() {

    private lateinit var speech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setSupportActionBar(bottomAppBar)
        showResults()
        buttonHear.setOnClickListener {
            if (Locale.getDefault().language == Locale.ENGLISH.language)
            textToSpeech()
            else Toast.makeText(applicationContext, R.string.language_not_supported, Toast.LENGTH_LONG).show()
        }
    }

    private fun showResults() {
        sort(resultList) { p0, p1 ->
            return@sort p1.resultFloat!!.compareTo(p0.resultFloat!!)
        }
        val resultAdapter = ResultAdapter(this, resultList)
        recyclerResult.adapter = resultAdapter
        recyclerResult.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (resultList[0].resultName != "Healthy" && resultList[0].resultName != "صحي") {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_result, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.resultSave -> saveResult()
            R.id.resultLocate -> locateResult()
        }
        item.isEnabled = false
        return true
    }

    private fun textToSpeech() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speech = TextToSpeech(this, TextToSpeech.OnInitListener {
                val text = resultList[0].resultName + resultList[0].resultPercentage
                speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            })
        }
        else
            Toast.makeText(applicationContext, getString(R.string.not_supported), Toast.LENGTH_LONG).show()
    }

    private fun locateResult() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("disease", resultList[0].resultName)
        startActivity(intent)
    }

    private fun saveResult() {
        val best = resultList[0]
        val path = System.currentTimeMillis().toString()
        val file = File(filesDir, path)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()
        val databaseHelper = DatabaseHelper(this)
        val history = History(historyId = null, historyDisease = best.resultName!!,
            historyImage = path, historyPercentage = best.resultPercentage!!)
        databaseHelper.addHistory(history)
    }

    override fun onStop() {
        if (::speech.isInitialized)
            speech.stop()
        super.onStop()
    }

    companion object {
        lateinit var bitmap: Bitmap
        lateinit var resultList: List<Result>
    }
}
