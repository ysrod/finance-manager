package br.unifor.cct.financemanagerfb.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.entity.User
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class LineChartActivity : AppCompatActivity() {

    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    private var mUserKey = ""

    private lateinit var mlineChart : LineChart
    val mRevenueData = ArrayList<Entry>()
    val mExpenseData = ArrayList<Entry>()


    private var mDateFormat = SimpleDateFormat("dd/MM/yyyy")


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

                    var revenuePos = 1
                    var expensePos = 1

                    var revenueList = user
                        ?.finances
                        ?.values
                        ?.toList()
                        ?.sortedByDescending{mDateFormat.parse(it.date)}
                        ?.filter{it.type}
                        ?.take(5)
                    var expenseList = user?.finances
                        ?.values
                        ?.toList()
                        ?.sortedByDescending{mDateFormat.parse(it.date)}
                        ?.filter{!it.type}
                        ?.take(5)

                    if (revenueList != null) {
                        for (revenue in revenueList) {
                            mRevenueData.add(Entry(revenuePos.toFloat(),revenue.amount.toFloat()))
                            revenuePos++
                        }

                    }

                    if (expenseList != null) {
                        for (expense in expenseList) {
                            mExpenseData.add(Entry(expensePos.toFloat(),expense.amount.toFloat()))
                            expensePos++


                        }
                    }



                    //Organizando pelo eixo x
                    Collections.sort(mRevenueData, EntryXComparator())
                    Collections.sort(mExpenseData, EntryXComparator())

                    val lineRevenueSet = LineDataSet(mRevenueData, "Receitas")
                    lineRevenueSet.setAxisDependency(YAxis.AxisDependency.LEFT)
                    val lineExpenseSet = LineDataSet(mExpenseData, "Despesas")
                    lineExpenseSet.setAxisDependency(YAxis.AxisDependency.LEFT)


                    //customização
                    lineRevenueSet.setColor(Color.GREEN)
                    lineExpenseSet.setColor(Color.RED)

                    lineRevenueSet.setCircleColor(Color.GREEN)
                    lineExpenseSet.setCircleColor(Color.RED)

                    chartCustomization(lineRevenueSet)
                    chartCustomization(lineExpenseSet)

                    val iLineDataSets: ArrayList<ILineDataSet> = ArrayList()
                    iLineDataSets.add(lineRevenueSet)
                    iLineDataSets.add(lineExpenseSet)


                    //Legendas e eixos
                    val xAxis: XAxis = mlineChart.getXAxis()
                    xAxis.granularity = 1F
                    xAxis.labelCount = 6
                    xAxis.setTextSize(20F)

//                    var yaxis = mlineChart.getAxisLeft();
//                    yaxis.spaceTop = 35f


                    val legend : Legend = mlineChart.getLegend()
                    legend.setTextSize(20f)
                    legend.setTextColor(Color.BLACK)

                    val description = Description()
                    description.setText("")
                    mlineChart.setDescription(description)


                    //Recebendo dados
                    val lineData = LineData(iLineDataSets)
                    mlineChart.setData(lineData)
                    mlineChart.extraRightOffset = 30F
                    mlineChart.extraLeftOffset = 10F
                    mlineChart.extraTopOffset = 10F
                    mlineChart.setNoDataText("Procurando dados...")
                    mlineChart.notifyDataSetChanged()
                    mlineChart.invalidate()




                }
                override fun onCancelled(error: DatabaseError) {

                }

            })




    }


    // dados para teste
    private fun linePointRevenue(): ArrayList<Entry> {
        val dataSet = ArrayList<Entry>()
        dataSet.add(Entry(0F, 40F))
        dataSet.add(Entry(1F, 10F))
        dataSet.add(Entry(2F, 15F))
        dataSet.add(Entry(3F, 12F))


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
        lineDataSet.setValueTextSize(25F)
        lineDataSet.setValueTextColor(Color.BLACK)
    }




}

