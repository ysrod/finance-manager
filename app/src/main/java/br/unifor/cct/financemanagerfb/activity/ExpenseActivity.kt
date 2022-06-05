package br.unifor.cct.financemanagerfb.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


//classe que lista as despesas
class ExpenseActivity : AppCompatActivity() {

    private lateinit var mExpenseList : RecyclerView
    private lateinit var mExpenseAdd: FloatingActionButton
    private lateinit var mFinanceAdapater : FinanceActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)
    }
}