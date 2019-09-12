package com.plantplanet.android.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plantplanet.android.R
import com.plantplanet.android.activities.HomeActivity
import com.plantplanet.android.activities.MapsActivity
import com.plantplanet.android.models.History
import com.plantplanet.android.utils.DatabaseHelper
import kotlinx.android.synthetic.main.fragment_history.*
import java.io.File
import java.util.*

class HistoryFragment(private val history: History) : BottomSheetDialogFragment() {

    private lateinit var speech: TextToSpeech

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.historyHear -> hearHistory()
                R.id.historyDelete -> deleteHistory()
                R.id.historyLocate -> locateHistory()
            }
            dismiss()
            true
        }
        super.onActivityCreated(savedInstanceState)
    }

    private fun locateHistory() {
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra("disease", history.historyDisease)
        startActivity(intent)
    }

    private fun hearHistory() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Locale.getDefault().language == Locale.ENGLISH.language) {
            speech = TextToSpeech(context, TextToSpeech.OnInitListener {
                val text = history.historyDisease + history.historyPercentage
                speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            })
        } else if (Locale.getDefault().language == Locale.ENGLISH.language)
            Toast.makeText(activity, getString(R.string.language_not_supported), Toast.LENGTH_LONG).show()
        else
            Toast.makeText(activity, getString(R.string.not_supported), Toast.LENGTH_LONG).show()
    }

    private fun deleteHistory() {
        val file = File(activity!!.filesDir, history.historyImage)
        file.delete()
        val databaseHelper = DatabaseHelper(context!!)
        databaseHelper.deleteHistory(history)
        val activity = activity as HomeActivity
        activity.getHistory()
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }
}