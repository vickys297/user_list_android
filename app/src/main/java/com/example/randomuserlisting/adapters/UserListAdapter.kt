package com.example.randomuserlisting.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.randomuserlisting.R
import com.example.randomuserlisting.adapters.viewHolder.EmptyStateView
import com.example.randomuserlisting.adapters.viewHolder.LargeImageView
import com.example.randomuserlisting.adapters.viewHolder.SmallImageView
import com.example.randomuserlisting.databinding.RecyclerUserListItemBinding
import com.example.randomuserlisting.databinding.RecyclerUserListItemSmallBinding
import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.utils.AppInterface

internal const val VIEW_LARGE_IMAGE = 0
internal const val VIEW_SMALL_IMAGE = 1
internal const val VIEW_EMPTY_STATE = 2
internal val TAG = UserListAdapter::class.java.canonicalName

class UserListAdapter(private val userListInterface: AppInterface.UserList) :
    PagingDataAdapter<UserModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserModel>() {
            override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem.phone == newItem.phone
            }

            override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (getItemViewType(position = holder.bindingAdapterPosition)) {
            VIEW_LARGE_IMAGE -> {
                item?.let {
                    (holder as LargeImageView).bind(item).apply {
                        holder.itemView.setOnClickListener {
                            userListInterface.onUserClick(item)
                        }
                    }
                }
            }
            VIEW_SMALL_IMAGE -> {
                item?.let {
                    (holder as SmallImageView).bind(item).apply {
                        holder.itemView.setOnClickListener {
                            userListInterface.onUserClick(item)
                        }
                    }
                }
            }
            VIEW_EMPTY_STATE -> {
                (holder as EmptyStateView).bind()
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == 0) VIEW_EMPTY_STATE else if (position % 2 == 0) VIEW_LARGE_IMAGE else VIEW_SMALL_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Log.i(TAG, "onCreateViewHolder: viewType -> $itemCount")
        return when (viewType) {
            VIEW_LARGE_IMAGE -> {
                LargeImageView(
                    RecyclerUserListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_SMALL_IMAGE -> {
                SmallImageView(
                    RecyclerUserListItemSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_EMPTY_STATE -> {
                EmptyStateView(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_user_list_empty, parent, false)
                )
            }

            else -> {
                throw Exception("No view found")
            }
        }
    }
}

