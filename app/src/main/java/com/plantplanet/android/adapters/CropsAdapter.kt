package com.plantplanet.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plantplanet.android.R
import com.plantplanet.android.models.Crop
import com.plantplanet.android.utils.FirebaseHelper
import com.squareup.picasso.Picasso
import java.io.File

class CropsAdapter(private val context: Context, private val cropsList: List<Crop>) : RecyclerView.Adapter<CropsAdapter.ViewHolder>() {

    var checkPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_crop, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cropsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = File(context.filesDir, cropsList[position].cropImage!!)
        if (file.exists()) Picasso.get().load(file).fit().centerCrop().into(holder.cropImage)
        else FirebaseHelper().loadFile(context, "crops", cropsList[position].cropImage!!).addOnSuccessListener {
            Picasso.get().load(file).fit().centerCrop().into(holder.cropImage)
        }
        holder.cropName.text = cropsList[position].cropName
        if (checkPosition == position) holder.cropRadio.isChecked = true
        else if (checkPosition != position) holder.cropRadio.isChecked = false
        holder.cropRadio.setOnClickListener {
            checkPosition = position
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cropName = view.findViewById<TextView>(R.id.textCrop)!!
        val cropImage = view.findViewById<ImageView>(R.id.imageCrop)!!
        val cropRadio = view.findViewById<RadioButton>(R.id.radioCrop)!!
    }
}