package br.unifor.cct.financemanagerfb.activity

import android.os.Bundle
import android.telephony.PhoneNumberUtils.formatNumber
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.unifor.cct.financemanagerfb.R
import br.unifor.cct.financemanagerfb.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.MaskedTextChangedListener.Companion.installOn


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mRegisterName: EditText
    private lateinit var mRegisterEmail: EditText
    private lateinit var mRegisterPhone : EditText
    private lateinit var mRegisterPassword: EditText
    private lateinit var mRegisterPasswordConfirmation: EditText
    private lateinit var mRegisterSave: Button

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDatabase : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mRegisterName = findViewById(R.id.register_edittext_name)
        mRegisterEmail = findViewById(R.id.register_edittext_email)
        mRegisterPhone = findViewById(R.id.register_edittext_phone)
        mRegisterPassword = findViewById(R.id.register_edittext_pw)
        mRegisterPasswordConfirmation = findViewById(R.id.register_edittext_pwconfirmation)
        mRegisterSave = findViewById(R.id.register_button)
        mRegisterSave.setOnClickListener(this)

        val listener: MaskedTextChangedListener = installOn(
            mRegisterPhone,
            "([00]) [00000]-[0000]",
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
                    Log.d("TAG", extractedValue)
                    Log.d("TAG", maskFilled.toString())
                }
            }
        )

        mRegisterPhone.setHint(listener.placeholder())

    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.register_button -> handlerSaveAction()
        }

    }

    private fun handlerSaveAction (){
        val name = mRegisterName.text.trim()
        val email = mRegisterEmail.text.trim()
        val phone = mRegisterPhone.text.trim()
        val password = mRegisterPassword.text.trim()
        val passwordConfirmation = mRegisterPasswordConfirmation.text.trim()

        var isFormFilled = true
        isFormFilled = isFieldFilled(name, mRegisterName) && isFormFilled
        isFormFilled = isFieldFilled(email, mRegisterEmail) && isFormFilled
        isFormFilled = isFieldFilled(phone, mRegisterPhone) && isFormFilled
        isFormFilled = isFieldFilled(password, mRegisterPassword) && isFormFilled
        isFormFilled = fieldHasEnoughSize(password, mRegisterPassword) && isFormFilled
        isFormFilled = isFieldFilled(passwordConfirmation, mRegisterPasswordConfirmation) && isFormFilled
        isFormFilled = areFieldEqual(password, passwordConfirmation, mRegisterPasswordConfirmation) && isFormFilled

//        val formattedPhone = formatNumber(phone.toString(),"BR")


        if (isFormFilled) {
            val usersRef = mDatabase.getReference("/users")

            mAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val key = usersRef.push().key ?: ""
                        val user = User (
                            id = key,
                            name = name.toString(),
                            email = email.toString(),
                            phone = phone.toString()
                        )

                        usersRef.child(key).setValue(user)
                        showDialog("Usuário cadastrado com sucesso!")
                    } else {
                        showDialog("Ocorreu um erro. Tente novamente")
                    }
                }
        }
    }

    private fun isFieldFilled(input: CharSequence, field: EditText) : Boolean {
        if (input.isBlank()) {
            field.error = "Este campo é obrigatório"
            return false
        }
        return true
    }

    private fun fieldHasEnoughSize(input: CharSequence, field: EditText) : Boolean {
        if (input.length < 6) {
            field.error = "Digite uma senha com no mínimo 6 caracteres"
            return false
        }
        return true
    }

    private fun areFieldEqual(password: CharSequence, confirmation: CharSequence, field: EditText) : Boolean {
        if (password != confirmation) {
            field.error = "As senhas não são iguais"
            return false
        }
        return true
    }

    private fun showDialog (message:String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Piggy Bank")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                finish()
            }.create()
        dialog.show()
    }
}