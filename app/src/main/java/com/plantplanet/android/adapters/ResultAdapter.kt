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
import com.plantplanet.android.models.Result
import com.plantplanet.android.utils.FirebaseHelper
import com.squareup.picasso.Picasso
import java.io.File

class ResultAdapter(private val context: Context, private val resultList: List<Result>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (resultList.size > 4) 4
        else resultList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = File(context.filesDir, resultList[position].resultImage!!)
        if (file.exists()) Picasso.get().load(file).fit().centerCrop().into(holder.resultImage)
        else FirebaseHelper().loadFile(context, "diseases", resultList[position].resultImage!!).addOnSuccessListener {
            Picasso.get().load(file).fit().centerCrop().into(holder.resultImage)
        }
        holder.resultName.text = resultList[position].resultName
        holder.resultPercentage.text = resultList[position].resultPercentage
        if (resultList[position].resultName != "Healthy" && resultList[position].resultName != "صحي") {
            holder.resultLayout.setOnClickListener {
                val intent = Intent(context, DiseaseActivity::class.java)
                intent.putExtra("disease", resultList[position].resultName)
                context.startActivity(intent)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resultName = view.findViewById<TextView>(R.id.textTitle)!!
        val resultPercentage = view.findViewById<TextView>(R.id.textSubtitle)!!
        val resultImage = view.findViewById<ImageView>(R.id.imageList)!!
        val resultLayout = view.findViewById<ConstraintLayout>(R.id.layoutList)!!
    }
}