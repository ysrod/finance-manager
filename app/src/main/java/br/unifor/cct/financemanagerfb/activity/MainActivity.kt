package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.adapter.FinancesAdapter
import br.unifor.cct.financemanagerfb.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private lateinit var mMainRevenueButton : Button
    private lateinit var mMainExpenseButton: Button
    private lateinit var mMainRevenueList : RecyclerView
    private lateinit var mMainExpenseList: RecyclerView
    private lateinit var mMainPlaceholderExpense : TextView
    private lateinit var mMainPlaceholderRevenue : TextView

    private lateinit var mDatabase : FirebaseDatabase
    private lateinit var mAuth : FirebaseAuth

    private var mUserKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mMainRevenueList = findViewById(R.id.main_recyclerview_revenue)
        mMainExpenseList = findViewById(R.id.main_recyclerview_expense)
        mMainRevenueButton = findViewById(R.id.main_button_revenue)
        mMainExpenseButton = findViewById(R.id.main_button_expense)
        mMainPlaceholderExpense = findViewById(R.id.textView)
        mMainPlaceholderRevenue = findViewById(R.id.textView2)

        mMainRevenueButton.setOnClickListener{
            Log.i("App","cliquei")
            val it = Intent(this,FinanceActivity::class.java)
            it.putExtra("financeType",true)
            startActivity(it)
        }
        mMainExpenseButton.setOnClickListener{
            val it = Intent(this,FinanceActivity::class.java)
            it.putExtra("financeType",false)
            startActivity(it)
        }

        mMainPlaceholderExpense.setOnClickListener{
            val it = Intent(this, ExpenseActivity::class.java)
            startActivity(it)
        }

        mMainPlaceholderRevenue.setOnClickListener{
            val it = Intent(this,RevenueActivity::class.java)
            startActivity(it)
        }


    }

    override fun onStart() {
        super.onStart()
        val userRef = mDatabase.getReference("/users")
        userRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.first().getValue(User::class.java)
                    mUserKey = user?.id ?:""
//                     = FinancesAdapter(user?.finances?.values?.toList()?.filter {
//                        it.type}!!)
//                    mRevenueAdapter.setOnFinanceItemListener(this@RevenueActivity)
//                    mRevenueList.adapter = mRevenueAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}