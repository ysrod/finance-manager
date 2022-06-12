package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.adapter.FinancesAdapter
import br.unifor.cct.financemanagerfb.entity.Finances
import br.unifor.cct.financemanagerfb.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var mMainRevenueButton : Button
    private lateinit var mMainExpenseButton: Button
    private lateinit var mMainGraphButton : Button
    private lateinit var mMainRevenueList : RecyclerView
    private lateinit var mMainExpenseList: RecyclerView
    private lateinit var mMainPlaceholderExpense : TextView
    private lateinit var mMainPlaceholderRevenue : TextView
    private lateinit var mWelcome : TextView
    private lateinit var mTotal : TextView

    private lateinit var mRevenueAdapter : FinancesAdapter
    private lateinit var mExpenseAdapter : FinancesAdapter

    private lateinit var mDatabase : FirebaseDatabase
    private lateinit var mAuth : FirebaseAuth

    private var mDateFormat = SimpleDateFormat("dd/MM/yyyy")

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
        mMainGraphButton = findViewById(R.id.main_button_graphs)
        mMainPlaceholderExpense = findViewById(R.id.textView)
        mMainPlaceholderRevenue = findViewById(R.id.textView2)
        mWelcome = findViewById(R.id.main_textView_welcome)
        mTotal = findViewById(R.id.main_textView_total)

        mMainRevenueList.layoutManager = LinearLayoutManager(this)
        mMainExpenseList.layoutManager = LinearLayoutManager(this)

        mMainRevenueButton.setOnClickListener{
            val it = Intent(this,FinanceActivity::class.java)
            it.putExtra("financeType",true)
            startActivity(it)
        }
        mMainExpenseButton.setOnClickListener{
            val it = Intent(this,FinanceActivity::class.java)
            it.putExtra("financeType",false)
            startActivity(it)
        }

        mMainGraphButton.setOnClickListener{
            val it = Intent(this, LineChartActivity::class.java)
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
        val userRef : DatabaseReference = mDatabase.getReference("/users")

        userRef
            .orderByChild("email")
            .equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.first().getValue(User::class.java)
                    mUserKey = user?.id ?: ""

                    val nome = user!!.name
                    mWelcome.text = "Bem vinde, ${nome}"

                    val finances = user
                        .finances
                        .values
                        .toList()
                        .sortedByDescending{
                            mDateFormat.parse(it.date)
                        }

                    val lastRevenues = mutableListOf<Finances>()
                    val lastExpenses = mutableListOf<Finances>()

                    var total = 0.0
                    for(finance in finances) {
                        if(finance.type) {
                            if (lastRevenues.size < 5) {
                                lastRevenues.add(finance)
                            }
                            total += finance.amount
                        } else {
                            if (lastExpenses.size < 5) {
                                lastExpenses.add(finance)
                            }
                            total -= finance.amount
                        }
                    }

                    mTotal.text = "Total: R$%.2f".format(total)

                    mRevenueAdapter = FinancesAdapter(lastRevenues)
                    mMainRevenueList.adapter = mRevenueAdapter

                    mExpenseAdapter = FinancesAdapter(lastExpenses)
                    mMainExpenseList.adapter = mExpenseAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}