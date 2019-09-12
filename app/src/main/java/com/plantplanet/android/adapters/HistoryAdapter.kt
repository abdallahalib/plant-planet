package com.plantplanet.android.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.plantplanet.android.R
import com.plantplanet.android.activities.DiseaseActivity
import com.plantplanet.android.fragments.HistoryFragment
import com.plantplanet.android.models.History
import com.squareup.picasso.Picasso
import java.io.File

class HistoryAdapter(private val context: Context, private val supportedFragmentManager: FragmentManager, private val historyList: ArrayList<History>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = File(context.filesDir, historyList[position].historyImage)
        Picasso.get().load(file).fit().centerCrop().into(holder.historyImage)
        holder.historyName.text = historyList[position].historyDisease
        holder.historyProbability.text = historyList[position].historyPercentage
        holder.historyLayout.setOnLongClickListener {
            val navigationFragment = HistoryFragment(historyList[position])
            navigationFragment.show(supportedFragmentManager, navigationFragment.tag)
            true
        }
        holder.historyLayout.setOnClickListener {
            val intent = Intent(context, DiseaseActivity::class.java)
            intent.putExtra("disease", historyList[position].historyDisease)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val historyName = view.findViewById<TextView>(R.id.textTitle)!!
        val historyProbability = view.findViewById<TextView>(R.id.textSubtitle)!!
        val historyImage = view.findViewById<ImageView>(R.id.imageList)!!
        val historyLayout = view.findViewById<ConstraintLayout>(R.id.layoutList)!!
    }
}