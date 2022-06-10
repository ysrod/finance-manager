package br.unifor.cct.financemanagerfb.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.Editable
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


// classe para registrar as finanças
class FinanceActivity : AppCompatActivity() {


    private lateinit var mFinanceDescription : EditText
    private lateinit var mFinanceAmount : EditText
    private lateinit var mFinanceDate : EditText
    private lateinit var mFinanceSwitch : Switch
    private lateinit var mFinanceButton : Button

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDatabase : FirebaseDatabase

    private var mUserKey = ""
    private var mFinanceKey = ""
    private var mFinanceType = false



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

        mFinanceButton.setOnClickListener{
            val description = mFinanceDescription.text.toString().trim()
            val amount = mFinanceAmount.text.toString().trim()
            val date = mFinanceDate.text.toString().trim()
            val type = mFinanceType

            if(description.isBlank()) {
                mFinanceDescription.error = "Este campo é obrigatório"
                return@setOnClickListener
            }

            if(mFinanceKey.isBlank()){
                val usersRef = mDatabase.getReference("/users/")
                usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
                    .addChildEventListener(object: ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val financesRef = usersRef.child(snapshot.key!!).child("/finances/")
                            val financeId = financesRef.push().key ?: " "
                            val finance = Finances(financeId, description, amount, date, type)
                            financesRef.child(financeId).setValue(finance)

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
                        mFinanceAmount.text = Editable.Factory.getInstance().newEditable(finance?.amount)
                        mFinanceDate.text = Editable.Factory.getInstance().newEditable(finance?.date)
                        mFinanceSwitch.isChecked = finance?.type ?:false
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }
    }

    fun dialogShow(message: CharSequence){
        val dialog = AlertDialog.Builder(this@FinanceActivity)
            .setTitle("Finances Manager")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok"){dialog, _ ->
                dialog.dismiss()

                finish()
            }
            .create()

        dialog.show()
    }
}