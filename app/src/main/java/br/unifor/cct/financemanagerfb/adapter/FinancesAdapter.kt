package br.unifor.cct.financemanagerfb.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.entity.Finances

class FinancesAdapter (var finances:List<Finances>):RecyclerView.Adapter<FinancesAdapter.FinancesViewHolder>(){

    private var listener:FinancesItemListener?=null

    class FinancesViewHolder(view: View, listener:FinancesItemListener?):RecyclerView.ViewHolder(view){
        val amount:TextView = view.findViewById(R.id.finance_item_textview_amount)
        val description:TextView = view.findViewById(R.id.finance_item_textview_description)
        val date:TextView = view.findViewById(R.id.finance_item_textview_date)
        val type:View = view.findViewById(R.id.finance_item_view_type)

        init{
            view.setOnClickListener{
                listener?.setOnItemClickListener(it,adapterPosition)
            }
            view.setOnLongClickListener {
                listener?.setOnItemLongClickListener(it,adapterPosition)
                true
                //precisamos de booleano pq esse componente é executado em uma cadeia de
                // componentes, o true indica que ele deve parar de seguir essa cadeia
            }
        }

    }

    fun setOnFinanceItemListener(listener:FinancesItemListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.finance_item,parent,false)
        return FinancesViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: FinancesViewHolder, position: Int) {
        holder.amount.text = finances[position].amount.toString()
        holder.description.text = finances[position].description
        holder.date.text = finances[position].date
        if (finances[position].type) {
            holder.type.setBackgroundResource(R.drawable.revenue_view_layout)
        } else {
            holder.type.setBackgroundResource(R.drawable.expense_view_layout)
        }

    }

    override fun getItemCount(): Int {
        return finances.size
    }
}