package com.plantplanet.android.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.plantplanet.android.R
import com.plantplanet.android.adapters.CropsAdapter
import com.plantplanet.android.models.Crop
import com.plantplanet.android.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_crops.*

class CropsActivity : AppCompatActivity() {

    private lateinit var cropsList: List<Crop>
    private lateinit var cropsAdapter: CropsAdapter
    private var cropPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops)
        fetchCrops()
    }

    private fun fetchCrops() {
        FirebaseHelper().loadCollection(getString(R.string.firestore_crops), "cropName").addOnSuccessListener {
            cropsList = it.toObjects(Crop::class.java)
            if (cropsList.isNotEmpty()) {
                progressBar.visibility = View.GONE
                loadCrop()
                cropsAdapter = CropsAdapter(this, cropsList)
                cropsAdapter.checkPosition = cropPosition
                recyclerCrops.adapter = cropsAdapter
                recyclerCrops.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                buttonSave.setOnClickListener { saveCrop() }
            }
            else {
                Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
                finishAffinity()
            }
        }
    }

    private fun loadCrop() {
        val sharedPreferences = getSharedPreferences("crop", Context.MODE_PRIVATE)
        val saved = sharedPreferences.getString("reference", "")
        if (saved != "") {
            for (crop in cropsList) {
                if (crop.cropReference == saved) {
                    cropPosition = cropsList.indexOf(crop)
                    break
                }
            }
        }
    }

    private fun saveCrop() {
        val position = cropsAdapter.checkPosition
        if (position != null) {
            val sharedPreferences = getSharedPreferences("crop", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("reference", cropsList[position].cropReference!!)
            editor.putString("name", cropsList[position].cropName!!)
            editor.apply()
            finish()
            Toast.makeText(applicationContext, getString(R.string.saved_successfully), Toast.LENGTH_LONG).show()
        } else Toast.makeText(applicationContext, getString(R.string.choose_crop), Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        if (::cropsAdapter.isInitialized && cropsAdapter.checkPosition == null)
            Toast.makeText(applicationContext, getString(R.string.choose_crop), Toast.LENGTH_LONG).show()
        else
            finish()
    }
}
