package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.adapter.FinancesAdapter
import br.unifor.cct.financemanagerfb.adapter.FinancesItemListener
import br.unifor.cct.financemanagerfb.entity.Finances
import br.unifor.cct.financemanagerfb.entity.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


//classe que lista as receitas
class RevenueActivity : AppCompatActivity(), FinancesItemListener {

    private lateinit var mRevenueList : RecyclerView
    private lateinit var mRevenueAdd : FloatingActionButton
    private lateinit var mRevenueAdapter : FinancesAdapter

    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    private var mUserKey = ""

    private var mDateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revenue)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mRevenueList = findViewById(R.id.revenue_recyclerview)
        mRevenueList.layoutManager = LinearLayoutManager(this)

        mRevenueAdd = findViewById(R.id.revenue_fab_add)
        mRevenueAdd.setOnClickListener{
            val it = Intent(this,FinanceActivity::class.java)
            it.putExtra("financeType", true)
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
                    mRevenueAdapter = FinancesAdapter(
                        user
                            ?.finances
                            ?.values
                            ?.toList()
                            ?.sortedByDescending{mDateFormat.parse(it.date)}
                            ?.filter{it.type}!!)
                    mRevenueAdapter.setOnFinanceItemListener(this@RevenueActivity)
                    mRevenueList.adapter = mRevenueAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    override fun setOnItemClickListener(view: View, position: Int) {
        val it = Intent(this, FinanceActivity::class.java)
        it.putExtra("userKey", mUserKey)
        it.putExtra("financeKey", mRevenueAdapter.finances[position].id)
        it.putExtra("financeType", true)
        startActivity(it)
        finish()
    }

    override fun setOnItemLongClickListener(view: View, position: Int) {
        val revenue = mRevenueAdapter.finances[position]

        val dialog = AlertDialog.Builder(this)
            .setTitle("Finance Manager")
            .setMessage("Voc?? tem certeza que quer excluir a receita '${revenue.description}?'")
            .setCancelable(false)
            .setPositiveButton("SIM") {dialog, _ ->

                val revenueRef = mDatabase.reference
                    .child("/users")
                    .child(mUserKey)
                    .child("/finances")
                    .child(revenue.id)

                revenueRef.removeValue()
                dialog.dismiss()
            }
            .setNegativeButton("N??O") {dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }


}