package com.example.randomuserlisting.adapters.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.randomuserlisting.R
import com.example.randomuserlisting.model.UserModel

class EmptyStateView(private val view: View) :
    RecyclerView.ViewHolder(view) {

    fun bind() {
        val textView = view.findViewById<TextView>(R.id.textView_empty_state)
        textView.text = view.context.getString(R.string.no_results_found)
    }
}