package br.unifor.cct.financemanagerfb.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.entity.Finances
import br.unifor.cct.financemanagerfb.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


// classe para registrar as finanças
class FinanceActivity : AppCompatActivity() {


    private lateinit var mFinanceDescription : EditText
    private lateinit var mFinanceAmount : EditText
    private lateinit var mFinanceDate : TextView
    private lateinit var mFinanceSwitch : Switch
    private lateinit var mFinanceButton : Button

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDatabase : FirebaseDatabase

    private var mUserKey = ""
    private var mFinanceKey = ""
    private var mFinanceType = false

    private var cal = Calendar.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)

        mUserKey = intent.getStringExtra("userKey")?:""
        mFinanceKey = intent.getStringExtra("financeKey")?:""
        mFinanceType = intent.getBooleanExtra("financeType", false)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mFinanceDescription = findViewById(R.id.finance_edittext_description)
        mFinanceAmount = findViewById(R.id.finance_edittext_amount)
        mFinanceDate = findViewById(R.id.finance_edittext_date)
        mFinanceSwitch = findViewById(R.id.finance_switch)
        mFinanceButton = findViewById(R.id.finance_button)

        mFinanceSwitch.isChecked = mFinanceType //muda o estado do switch

        mFinanceSwitch.setOnCheckedChangeListener { _, isChecked ->
             mFinanceType = isChecked

        }

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            updateDate()
        }

        mFinanceDate.setOnClickListener{
            DatePickerDialog(this@FinanceActivity,
                dateListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        mFinanceButton.setOnClickListener{
            val description = mFinanceDescription.text.toString().trim()
            val amountText = mFinanceAmount.text.toString().trim()
            val date = mFinanceDate.text.toString().trim()
            val type = mFinanceType


            var isFormFilled = true
            isFormFilled = isFieldFilled(description, mFinanceDescription) && isFormFilled
            isFormFilled = isFieldFilled(amountText, mFinanceAmount) && isFormFilled
//            isFormFilled = isFieldFilled(date, mFinanceDate) && isFormFilled

            if (isFormFilled) {

                val amount = amountText.toDouble()

                if(mFinanceKey.isBlank()){
                    val usersRef = mDatabase.getReference("/users/")
                    usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
                        .addChildEventListener(object: ChildEventListener {
                            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                                val financesRef = usersRef.child(snapshot.key!!).child("/finances/")
                                val financeId = financesRef.push().key ?: " "
                                val finance = Finances(financeId, description, amount, date, type)
                                financesRef.child(financeId).setValue(finance)
                                if (type) {
                                    updateBalance(amount)
                                } else {
                                    updateBalance(-amount)
                                }



                                dialogShow(if (type) "Receita cadastrada com sucesso!" else "Despesa cadastrada com sucesso!")

                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {

                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {

                            }

                            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })

                } else {
                    val finance = Finances(mFinanceKey,description,amount,date,type)
                    val financeRef = mDatabase
                        .reference
                        .child("/users")
                        .child(mUserKey)
                        .child("/finances")
                        .child(mFinanceKey)

                    financeRef.setValue(finance)


                    dialogShow(if (type) "Receita '${description}' atualizada com sucesso!" else
                        "Despesa '${description}' atualizada com sucesso!")

                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (mFinanceKey.isBlank()) {
            mFinanceSwitch.isChecked = mFinanceType
            mFinanceButton.text = "Cadastrar"
            //cadastrar
            //oi iaismin
        } else {
            mFinanceButton.text = "Atualizar"
            val userRef = mDatabase.getReference("/users")
            userRef
                .orderByChild("email")
                .equalTo(mAuth.currentUser?.email)
                .addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.children.first().getValue(User::class.java)
                        val finance = user?.finances?.values?.find{it.id == mFinanceKey}
                        mFinanceDescription.text = Editable.Factory.getInstance().newEditable(finance?.description)
                        mFinanceAmount.text = Editable.Factory.getInstance().newEditable(finance?.amount.toString())
                        mFinanceDate.text = Editable.Factory.getInstance().newEditable(finance?.date)
                        mFinanceSwitch.isChecked = finance?.type ?:false
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }
    }

    private fun dialogShow(message: String){
        val dialog = AlertDialog.Builder(this@FinanceActivity)
            .setTitle("Piggy Bank")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok"){dialog, _ ->
                dialog.dismiss()
                if (mFinanceType) {
                    startActivity(Intent(this,RevenueActivity::class.java))
                } else {
                    startActivity(Intent(this,ExpenseActivity::class.java))
                }
                finish()
            }
            .create()

        dialog.show()
    }

    private fun isFieldFilled(input: CharSequence, field: EditText) : Boolean {
        if (input.isBlank()) {
            field.error = "Este campo é obrigatório"
            return false
        }
        return true
    }

   private fun updateBalance(amount : Double) {
        val userRef = mDatabase.getReference("/users")
        userRef
            .orderByChild("email")
            .equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var balance = snapshot.children.first().getValue(User::class.java)?.balance!!
                    balance += amount
                    Log.i("App", "balance: $balance")
                    Log.i("App", "mUserKey: $mUserKey")

                    val balanceRef = mDatabase
                        .reference
                        .child("/users")
                        .child(mUserKey)
                        .child("balance")

                    balanceRef.setValue(balance)

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun updateDate(){
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.CANADA)
        mFinanceDate.text = sdf.format(cal.time)
    }
}