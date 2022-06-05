package br.unifor.cct.financemanagerfb.adapter

import android.view.View

interface FinancesItemListener {

    fun setOnItemClickListener(view: View, position:Int)

    fun setOnItemLongClickListener(view: View, position:Int)

}