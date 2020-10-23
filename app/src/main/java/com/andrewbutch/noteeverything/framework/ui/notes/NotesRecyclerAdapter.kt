package com.andrewbutch.noteeverything.framework.ui.notes

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import kotlinx.android.synthetic.main.note_item.view.*

class NotesRecyclerAdapter(
    private val interaction: Interaction? = null,
    private val imgChecked: Drawable? = null,
    private val imgUnchecked: Drawable? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return NoteVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.note_item,
                parent,
                false
            ),
            interaction,
            imgChecked,
            imgUnchecked
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteVH -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Note>) {
        differ.submitList(list)
    }

    class NoteVH
    constructor(
        itemView: View,
        private val interaction: Interaction?,
        private val imgChecked: Drawable?,
        private val imgUnchecked: Drawable?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Note) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            item_name.text = item.title
            if (item.completed) {
                img_checkbox.setImageDrawable(imgChecked)
                item_name.setPaintFlags(item_name.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                img_checkbox.setImageDrawable(imgUnchecked)
                item_name.setPaintFlags(item_name.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            }
            // set indicator color
            val color: String = item.color
            if (color != "null") {
                color_indicator.setBackgroundColor(Color.parseColor(color))
            } else {
                color_indicator.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            img_reorder.setOnClickListener {
                TODO()
            }


        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Note)
    }
}
