package com.plantplanet.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantplanet.android.R
import com.plantplanet.android.adapters.LibraryAdapter
import com.plantplanet.android.models.Disease
import com.plantplanet.android.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_library.*

class LibraryActivity : AppCompatActivity() {

    private lateinit var diseasesList: List<Disease>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        setSupportActionBar(bottomAppBar)
        fetchDiseases()
        configureSearch()
        buttonSort.setOnClickListener { sortDialog() }
    }

    private fun sortDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sort)
        builder.setItems(arrayOf(getString(R.string.name), getString(R.string.category), getString(R.string.crop))) { dialogInterface, i ->
            sortDiseases(i)
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun sortDiseases(i: Int) {
        progressBar.visibility = View.VISIBLE
        var sortList: List<Disease>
        val collection = when (i) {
            0 -> FirebaseHelper().loadCollection(getString(R.string.firestore_library), "diseaseName")
            1 -> FirebaseHelper().loadCollection(getString(R.string.firestore_library), "diseaseCategory")
            else -> FirebaseHelper().loadCollection(getString(R.string.firestore_library), "diseaseCrop")
        }
        collection.addOnSuccessListener {
            sortList = it.toObjects(Disease::class.java)
            showDiseases(sortList)
            progressBar.visibility = View.GONE
        }
    }

    private fun configureSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchList = ArrayList<Disease>()
                for (disease in diseasesList) {
                    if (disease.diseaseName!!.toLowerCase().contains(newText!!.toLowerCase()))
                        searchList.add(disease)
                }
                showDiseases(searchList)
                return true
            }
        })
    }

    private fun fetchDiseases() {
        buttonSort.isClickable = false
        FirebaseHelper().loadCollection(getString(R.string.firestore_library), "diseaseName").addOnSuccessListener {
            progressBar.visibility = View.GONE
            diseasesList = it.toObjects(Disease::class.java)
            if (diseasesList.isEmpty())
                noInternet()
            else
                showDiseases(diseasesList)
        }
    }

    private fun showDiseases(diseases: List<Disease>) {
        buttonSort.isClickable = true
        val libraryAdapter = LibraryAdapter(this, diseases)
        recyclerLibrary.adapter = libraryAdapter
        recyclerLibrary.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
    }

    private fun noInternet() {
        Toast.makeText(applicationContext, getString(R.string.internet_required), Toast.LENGTH_LONG).show()
        finish()
    }
}