package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.adapter.FinancesAdapter
import br.unifor.cct.financemanagerfb.adapter.FinancesItemListener
import br.unifor.cct.financemanagerfb.entity.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat


//classe que lista as despesas
class ExpenseActivity : AppCompatActivity(), FinancesItemListener {

    private lateinit var mExpenseList : RecyclerView
    private lateinit var mExpenseAdd: FloatingActionButton
    private lateinit var mExpenseAdapater : FinancesAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    private var mUserKey = ""

    private var mDateFormat = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mExpenseList = findViewById(R.id.expense_recyclerview)
        mExpenseList.layoutManager = LinearLayoutManager(this)

        mExpenseAdd = findViewById(R.id.expense_fab_add)
        mExpenseAdd.setOnClickListener{
            val it = Intent(this,FinanceActivity::class.java)
            it.putExtra("financeType", false)
            startActivity(it)
            finish()
        }


    }

    override fun onStart() {
        super.onStart()
        val userRef = mDatabase.getReference("/users")
        userRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.first().getValue(User::class.java)
                    mUserKey = user?.id ?:""
                    mExpenseAdapater = FinancesAdapter(
                        user
                            ?.finances
                            ?.values
                            ?.toList()
                            ?.sortedByDescending{mDateFormat.parse(it.date)}
                            ?.filter{!it.type}!!)
                    mExpenseAdapater.setOnFinanceItemListener(this@ExpenseActivity)
                    mExpenseList.adapter = mExpenseAdapater
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    override fun setOnItemClickListener(view: View, position: Int) {
        val it = Intent(this, FinanceActivity::class.java)
        it.putExtra("userKey", mUserKey)
        it.putExtra("financeKey", mExpenseAdapater.finances[position].id)
        it.putExtra("financeType", false)
        startActivity(it)
        finish()
    }

    override fun setOnItemLongClickListener(view: View, position: Int) {
        val expense = mExpenseAdapater.finances[position]

        val dialog = AlertDialog.Builder(this)
            .setTitle("Finance Manager")
            .setMessage("Você tem certeza que quer excluir a despesa '${expense.description}'?")
            .setCancelable(false)
            .setPositiveButton("SIM") {dialog, _ ->

                val expenseRef = mDatabase.reference
                    .child("/users")
                    .child(mUserKey)
                    .child("/finances")
                    .child(expense.id)

                expenseRef.removeValue()
                dialog.dismiss()
            }
            .setNegativeButton("NÃO") {dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}