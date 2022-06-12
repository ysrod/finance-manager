package br.unifor.cct.financemanagerfb.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.adapter.FinancesAdapter
import br.unifor.cct.financemanagerfb.entity.User
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LineChartActivity : AppCompatActivity() {

    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    private var mUserKey = ""

    private lateinit var mlineChart : LineChart
    val mRevenueData = ArrayList<Entry>()
    val mExpenseData = ArrayList<Entry>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mlineChart = findViewById(R.id.line_chart)

        val userRef = mDatabase.getReference("/users")
        userRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.first().getValue(User::class.java)
                    mUserKey = user?.id ?:""


                    var revenuePos = 1F
                    var expensePos = 1F
                    var revenueList = user?.finances?.values?.toList()?.filter{it.type}?.take(5)
                    var expenseList = user?.finances?.values?.toList()?.filter{!it.type}?.take(5)

                    if (revenueList != null) {
                        for (revenue in revenueList) {
                            mRevenueData.add(Entry(revenuePos, revenue.amount.toFloat()))
                            revenuePos+=1F

                        }
//                        Log.i("App","posição 2: ${mRevenueData[2].toString()}")
                    }

                    if (expenseList != null) {
                        for (expense in expenseList) {
                            mRevenueData.add(Entry(expensePos, expense.amount.toFloat()))
                            expensePos+=1F

                        }
                    }



                }
                override fun onCancelled(error: DatabaseError) {

                }

            })

        val lineRevenueSet = LineDataSet(mRevenueData, "Receitas")
        val lineExpenseSet = LineDataSet(mExpenseData, "Despesas")

        val iLineDataSets: ArrayList<ILineDataSet> = ArrayList()
        iLineDataSets.add(lineRevenueSet)
        iLineDataSets.add(lineExpenseSet)

        val lineData = LineData(iLineDataSets)
        mlineChart.setData(lineData)
        mlineChart.invalidate()


        //if you want set background color use below method
        //lineChart.setBackgroundColor(Color.RED);

        // set text if data are are not available
        mlineChart.setNoDataText("Não há dados.")


        lineRevenueSet.setColor(Color.GREEN)
        lineExpenseSet.setColor(Color.RED)

        lineRevenueSet.setCircleColor(Color.GREEN)
        lineExpenseSet.setCircleColor(Color.RED)

        chartCustomization(lineRevenueSet)
        chartCustomization(lineExpenseSet)

    }

    override fun onStart() {
        super.onStart()

    }


    private fun linePointRevenue(): ArrayList<Entry> {
        val dataSet = ArrayList<Entry>()
        dataSet.add(Entry(0F, 40F))
        dataSet.add(Entry(1F, 10F))
        dataSet.add(Entry(2F, 15F))
        dataSet.add(Entry(3F, 12F))
        dataSet.add(Entry(4F, 20F))

        return dataSet
    }

    private fun linePointExpense(): ArrayList<Entry> {
        val dataSet = ArrayList<Entry>()
        dataSet.add(Entry(0F, 10F))
        dataSet.add(Entry(1F, 40F))
        dataSet.add(Entry(2F, 12F))
        dataSet.add(Entry(3F, 15F))
        dataSet.add(Entry(4F, 50F))

        return dataSet
    }

    private fun chartCustomization(lineDataSet : LineDataSet){
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawCircleHole(true)
        lineDataSet.setLineWidth(5F)
        lineDataSet.setCircleRadius(10F)
        lineDataSet.setCircleHoleRadius(10F)
        lineDataSet.setValueTextSize(10F)
        lineDataSet.setValueTextColor(Color.BLACK)
    }




}

