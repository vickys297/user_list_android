package com.example.randomuserlisting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.randomuserlisting.adapters.viewHolder.UserListLoadStateViewHolder
import com.example.randomuserlisting.databinding.RecyclerLoadStateBinding


class UserListLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<UserListLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: UserListLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState).apply {

            if (loadState is LoadState.Error) {
                val layoutParams = StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams.isFullSpan = true
                holder.itemView.layoutParams = layoutParams
            }


            holder.binding.buttonRetry.setOnClickListener {
                retry.invoke()
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): UserListLoadStateViewHolder {
        return UserListLoadStateViewHolder(
            RecyclerLoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}

