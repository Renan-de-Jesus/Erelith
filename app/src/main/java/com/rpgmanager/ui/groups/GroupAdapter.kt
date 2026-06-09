package com.rpgmanager.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rpgmanager.R
import com.rpgmanager.data.model.Group
import com.rpgmanager.databinding.ItemGroupBinding
import java.io.File

class GroupAdapter(
    private val onItemClick: (Group) -> Unit,
    private val onEditClick: (Group) -> Unit,
    private val onDeleteClick: (Group) -> Unit
) : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: Group) {
            binding.tvGroupName.text = group.name
            binding.tvMasterName.text = binding.root.context.getString(
                R.string.master_label, group.masterName
            )
            binding.tvDescription.text = group.description
            binding.chipActive.text = if (group.isActive)
                binding.root.context.getString(R.string.status_active)
            else
                binding.root.context.getString(R.string.status_inactive)

            // ImageView with Glide
            if (!group.imagePath.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(File(group.imagePath))
                    .placeholder(R.drawable.ic_group_placeholder)
                    .circleCrop()
                    .into(binding.ivGroupImage)
            } else {
                binding.ivGroupImage.setImageResource(R.drawable.ic_group_placeholder)
            }

            binding.root.setOnClickListener { onItemClick(group) }
            binding.btnEdit.setOnClickListener { onEditClick(group) }
            binding.btnDelete.setOnClickListener { onDeleteClick(group) }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Group, newItem: Group) = oldItem == newItem
    }
}
