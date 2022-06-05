package br.unifor.cct.financemanagerfb.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import br.unifor.cct.financemanagerfb.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler(Looper.myLooper()!!)

        handler.postDelayed({
            val it = Intent(SplashActivity@this,LoginActivity::class.java)
            startActivity(it)
            finish()
        },3000L)
    }
}