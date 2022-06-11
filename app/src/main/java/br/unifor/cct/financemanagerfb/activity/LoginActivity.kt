package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import br.unifor.cct.financemanagerfb.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginEmail : EditText
    private lateinit var mLoginPassword : EditText
    private lateinit var mLoginRegister : Button
    private lateinit var mLoginSignIn : Button

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = Firebase.auth

        mLoginEmail = findViewById(R.id.login_edittext_email)
        mLoginPassword = findViewById(R.id.login_edittext_password)

        mLoginSignIn = findViewById(R.id.login_button_signin)
        mLoginSignIn.setOnClickListener(this)

        mLoginRegister = findViewById(R.id.login_button_register)
        mLoginRegister.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.login_button_signin -> {
                val email = mLoginEmail.text.toString()
                val password = mLoginPassword.text.toString()

                if (email.isBlank()) {
                    mLoginEmail.error = "Este campo é obrigatório"
                    return
                }

                if (password.isBlank()) {
                    mLoginPassword.error = "Este campo é obrigatório"
                    return
                }

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            val it = Intent(this,MainActivity::class.java)
                            startActivity(it)
                            finish()
                        } else {
                            showDialog("Usuário ou senha inválidos")
                        }
                    }
            }

            R.id.login_button_register ->{
                val it = Intent(this,RegisterActivity::class.java)
                startActivity(it)
            }
        }
    }

    private fun showDialog (message:String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Piggy Bank")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }
}