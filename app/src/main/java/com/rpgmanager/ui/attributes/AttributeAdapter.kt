package com.rpgmanager.ui.attributes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rpgmanager.R
import com.rpgmanager.data.model.Attribute
import com.rpgmanager.databinding.ItemAttributeBinding

class AttributeAdapter(
    private val onEditClick: (Attribute) -> Unit,
    private val onDeleteClick: (Attribute) -> Unit
) : ListAdapter<Attribute, AttributeAdapter.AttributeViewHolder>(AttributeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        val binding = ItemAttributeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AttributeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AttributeViewHolder(private val binding: ItemAttributeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(attribute: Attribute) {
            binding.tvAttrName.text = attribute.name

            // Display value based on type
            binding.tvAttrValue.text = when (attribute.attributeType) {
                "BOOLEAN" -> if (attribute.value == "true")
                    binding.root.context.getString(R.string.bool_yes)
                else
                    binding.root.context.getString(R.string.bool_no)
                else -> attribute.value
            }

            binding.tvAttrType.text = when (attribute.attributeType) {
                "NUMBER" -> binding.root.context.getString(R.string.type_number)
                "TEXT" -> binding.root.context.getString(R.string.type_text)
                "BOOLEAN" -> binding.root.context.getString(R.string.type_boolean)
                else -> attribute.attributeType
            }

            binding.tvAttrNote.text = attribute.note
            binding.tvAttrNote.visibility =
                if (attribute.note.isNotEmpty()) android.view.View.VISIBLE
                else android.view.View.GONE

            binding.btnEditAttr.setOnClickListener { onEditClick(attribute) }
            binding.btnDeleteAttr.setOnClickListener { onDeleteClick(attribute) }
        }
    }

    class AttributeDiffCallback : DiffUtil.ItemCallback<Attribute>() {
        override fun areItemsTheSame(oldItem: Attribute, newItem: Attribute) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Attribute, newItem: Attribute) =
            oldItem == newItem
    }
}
