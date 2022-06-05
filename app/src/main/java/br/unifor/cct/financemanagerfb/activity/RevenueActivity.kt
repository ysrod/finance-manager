package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.adapter.FinancesAdapter
import br.unifor.cct.financemanagerfb.entity.Finances
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RevenueActivity : AppCompatActivity() {

    private lateinit var mRevenueList : RecyclerView
    private lateinit var mRevenueAdd : FloatingActionButton
    private lateinit var mRevenueAdapter : FinancesAdapter

    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

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
            it.putExtra("financeType", false)
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
                                if (finance.type){
                                    finances.add(finance)
                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}