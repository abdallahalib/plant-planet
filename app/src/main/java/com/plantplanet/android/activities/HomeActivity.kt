package com.plantplanet.android.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantplanet.android.R
import com.plantplanet.android.adapters.HistoryAdapter
import com.plantplanet.android.fragments.NavigationFragment
import com.plantplanet.android.utils.DatabaseHelper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.bottomAppBar
import kotlinx.android.synthetic.main.activity_home.recyclerHistory

class HomeActivity : AppCompatActivity() {

    private lateinit var cropReference: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(bottomAppBar)
        getCrop()
        getHistory()
        buttonCheck.setOnClickListener {
            val intent = Intent(this, CheckActivity::class.java)
            intent.putExtra("mode", "camera")
            startActivity(intent)
        }
    }

    fun getHistory() {
        val databaseHelper = DatabaseHelper(this)
        val historyList = databaseHelper.getHistory()
        if (historyList.isEmpty()) {
            textHistory.visibility = View.VISIBLE
            recyclerHistory.visibility = View.GONE
        } else {
            val historyAdapter = HistoryAdapter(this, supportFragmentManager, historyList)
            recyclerHistory.adapter = historyAdapter
            recyclerHistory.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun getCrop() {
        val sharedPreferences = getSharedPreferences("crop", Context.MODE_PRIVATE)
        cropReference = sharedPreferences.getString("reference", "")!!
        if (cropReference == "") {
            val intent = Intent(this, CropsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val navigationFragment = NavigationFragment()
                navigationFragment.show(supportFragmentManager, navigationFragment.tag)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        getHistory()
    }

    override fun onRestart() {
        super.onRestart()
        getHistory()
    }
}
