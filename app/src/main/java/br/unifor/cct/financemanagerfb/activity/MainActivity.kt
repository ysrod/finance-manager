package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import br.unifor.cct.financemanagerfb.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var mMainRevenueButton : Button
    private lateinit var mMainExpenseButton: Button
    private lateinit var mMainRevenueList : RecyclerView
    private lateinit var mMainExpenseList: RecyclerView

    private lateinit var mDatabase : FirebaseDatabase
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

//        mMainRevenueList = findViewById(R.id.main_recyclerview_revenue)
//        mMainExpenseList = findViewById(R.id.main_recyclerview_expense)
        mMainRevenueButton = findViewById(R.id.main_button_revenue)
        mMainExpenseButton = findViewById(R.id.main_button_expense)

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
    }

    override fun onStart() {
        super.onStart()

    }
}