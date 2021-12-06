package com.chiki.sleeptracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chiki.sleeptracker.database.SleepNight
import com.chiki.sleeptracker.databinding.SleepNightHeaderBinding
import com.chiki.sleeptracker.databinding.SleepNightItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class SleepNightAdapter(private val onItemClicked: (DataItem)->Unit): ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback){

    private val adapterScope = CoroutineScope(Dispatchers.Default)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> SleepNightViewHolder.from(this,parent,onItemClicked)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is SleepNightViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }
    fun addHeaderAndSubmitList(list: List<SleepNight>?){
        adapterScope.launch {
            val items = when(list){
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header)+list.map { DataItem.SleepNightItem(it)}
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }

    class TextViewHolder private constructor(binding: SleepNightHeaderBinding):RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): TextViewHolder {
                return TextViewHolder(SleepNightHeaderBinding.inflate(LayoutInflater.from(parent.context), parent,false))
            }
        }
    }
    class SleepNightViewHolder private constructor(private val binding:SleepNightItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(sleepNight: SleepNight) {
            binding.sleepNight = sleepNight
            binding.executePendingBindings()
        }
        companion object{
            fun from(sleepNightAdapter: SleepNightAdapter, parent: ViewGroup, onItemClicked: (DataItem) -> Unit): SleepNightViewHolder {
                val viewHolder = SleepNightViewHolder(SleepNightItemBinding.inflate(LayoutInflater.from(parent.context), parent,false))
                viewHolder.itemView.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    onItemClicked(sleepNightAdapter.getItem(position))
                }
                return viewHolder
            }
        }
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
sealed class DataItem{
    abstract val id: Long

    data class SleepNightItem(val sleepNight: SleepNight): DataItem(){
        override val id = sleepNight.nightId
    }
    object Header:DataItem(){
        override val id = Long.MIN_VALUE
    }
}

