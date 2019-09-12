package com.plantplanet.android.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.plantplanet.android.R
import com.plantplanet.android.activities.DiseaseActivity
import com.plantplanet.android.models.Disease
import com.plantplanet.android.utils.FirebaseHelper
import com.squareup.picasso.Picasso
import java.io.File

class LibraryAdapter(private val context: Context, private val diseasesList: List<Disease>) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diseasesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.diseaseName.text = diseasesList[position].diseaseName
        holder.diseaseCategory.text = diseasesList[position].diseaseCategory
        holder.diseaseLayout.setOnClickListener {
            val intent = Intent(context, DiseaseActivity::class.java)
            DiseaseActivity.disease = diseasesList[position]
            context.startActivity(intent)
        }
        val file = File(context.filesDir, diseasesList[position].diseaseImage!!)
        if (file.exists()) Picasso.get().load(file).fit().centerCrop().into(holder.diseaseImage)
        else {
            val storage = FirebaseHelper().loadFile(context, "diseases", diseasesList[position].diseaseImage!!)
            storage.addOnSuccessListener { Picasso.get().load(file).fit().centerCrop().into(holder.diseaseImage) }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val diseaseName = view.findViewById<TextView>(R.id.textTitle)!!
        val diseaseCategory = view.findViewById<TextView>(R.id.textSubtitle)!!
        val diseaseImage = view.findViewById<ImageView>(R.id.imageList)!!
        val diseaseLayout = view.findViewById<ConstraintLayout>(R.id.layoutList)!!
    }
}