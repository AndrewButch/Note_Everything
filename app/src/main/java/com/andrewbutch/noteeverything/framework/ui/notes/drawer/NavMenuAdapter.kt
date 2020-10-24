package com.andrewbutch.noteeverything.framework.ui.notes.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import kotlinx.android.synthetic.main.nav_drawer_item.view.*

class NavMenuAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NoteList>() {

        override fun areItemsTheSame(oldItem: NoteList, newItem: NoteList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteList, newItem: NoteList): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return NoteListVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.nav_drawer_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteListVH -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<NoteList>) {
        differ.submitList(list)
    }

    class NoteListVH
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: NoteList) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            title.text = item.title
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: NoteList)
    }
}
