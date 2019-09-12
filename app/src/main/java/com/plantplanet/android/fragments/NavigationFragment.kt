package com.plantplanet.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plantplanet.android.R
import android.content.Intent
import com.plantplanet.android.activities.CheckActivity
import com.plantplanet.android.activities.CropsActivity
import com.plantplanet.android.activities.LibraryActivity
import com.plantplanet.android.activities.MapsActivity
import kotlinx.android.synthetic.main.fragment_navigation.*

class NavigationFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigationMaps -> startActivity(Intent(context, MapsActivity::class.java))
                R.id.navigationCrops -> startActivity(Intent(context, CropsActivity::class.java))
                R.id.navigationLibrary -> startActivity(Intent(context, LibraryActivity::class.java))
                R.id.navigationImport -> importImage()
            }
            dismiss()
            true
        }
    }

    private fun importImage() {
        val intent = Intent(context, CheckActivity::class.java)
        intent.putExtra("mode", "import")
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }
}