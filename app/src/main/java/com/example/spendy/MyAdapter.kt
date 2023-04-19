package com.example.spendy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val expenseLog: ArrayList<Expense>): RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.expense_list_item,
        parent,false)


        return MyViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val expense : Expense =  expenseLog[position]
        holder.Amount.text = expense.Amount
        holder.Category.text = expense.Category
        holder.Date.text = expense.Date

    }

    override fun getItemCount(): Int {

        return expenseLog.size

    }

    class MyViewHolder(itemView : View,listener: onItemClickListener): RecyclerView.ViewHolder(itemView){

        val Amount : TextView = itemView.findViewById(R.id.amountView)
        val Category : TextView = itemView.findViewById(R.id.categoryView)
        val Date : TextView = itemView.findViewById(R.id.dateView)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}