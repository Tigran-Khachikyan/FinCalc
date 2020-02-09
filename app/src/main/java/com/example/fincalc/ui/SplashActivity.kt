package com.example.fincalc.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.fincalc.R
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.apply {
            systemUiVisibility = SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        setContentView(R.layout.activity_splash)

        CoroutineScope(Main).launch {
            launch {
                tvSplashSurface.animation =
                    AnimationUtils.loadAnimation(baseContext, R.anim.splash_text)
                ivSplash.animation = AnimationUtils.loadAnimation(baseContext, R.anim.splash_image)
                tvSplashSurface.playSplash(baseContext)
                delay(4000)
                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
                Animatoo.animateFade(this@SplashActivity)
                finish()
            }
        }
    }
}
