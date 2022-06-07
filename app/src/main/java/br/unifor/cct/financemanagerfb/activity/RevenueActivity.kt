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
import br.unifor.cct.financemanagerfb.entity.Finances
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


//classe que lista as receitas
class RevenueActivity : AppCompatActivity(), FinancesItemListener {

    private lateinit var mRevenueList : RecyclerView
    private lateinit var mRevenueAdd : FloatingActionButton
    private lateinit var mRevenueAdapter : FinancesAdapter

    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    private var mUserKey = ""

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
        }


    }

    override fun onStart() {
        super.onStart()
        val userRef = mDatabase.getReference("/users")
        userRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
            .addChildEventListener(object:ChildEventListener{

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    val financesRef = userRef.child(snapshot.key!!).child("/finances")

                    financesRef.addValueEventListener(object:ValueEventListener{

                        override fun onDataChange(snapshot: DataSnapshot) {

                            val finances : MutableList<Finances> = mutableListOf()

                            snapshot.children.forEach {
                                val finance = it.getValue(Finances::class.java)!!
                                if (finance.type){ //se for receita
                                    finances.add(finance)
                                }

                            }

                            mRevenueAdapter = FinancesAdapter(finances)
                            mRevenueAdapter.setOnFinanceItemListener(this@RevenueActivity)
                            mRevenueList.adapter = mRevenueAdapter
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    override fun setOnItemClickListener(view: View, position: Int) {
        val userRef = mDatabase.getReference("/users")
        userRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userKey = snapshot.key ?: ""
                    val revenueKey = mRevenueAdapter.finances[position].id

                    val it = Intent(this@RevenueActivity,FinanceActivity::class.java)
                    it.putExtra("userKey", userKey)
                    it.putExtra("revenueKey",revenueKey)
                    startActivity(it)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    override fun setOnItemLongClickListener(view: View, position: Int) {
        val revenue = mRevenueAdapter.finances[position]

        val dialog = AlertDialog.Builder(this)
            .setTitle("Finance Manager")
            .setMessage("Você tem certeza que quer excluir '${revenue.description}?'")
            .setCancelable(false)
            .setPositiveButton("SIM") {dialog, _ ->

                val revenue = mRevenueAdapter.finances[position]
                val revenueRef = mDatabase.reference
                    .child("/users")
                    .child(mUserKey)
                    .child("/finances")
                    .child(revenue.id)

                revenueRef.removeValue()
            }
            .setNegativeButton("NÃO") {dialog, _ ->
                dialog.dismiss()
            }

        dialog.show()
    }


}