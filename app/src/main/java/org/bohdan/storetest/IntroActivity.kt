package org.bohdan.storetest

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.bohdan.storetest.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

//         Check if application is opened for the first time
        val preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        val firstTime = preferences.getString("FirstTimeInstall", "")

        if (firstTime == "Yes") {
            // If application was opened for the first time
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            val editor = preferences.edit()
            editor.putString("FirstTimeInstall", "Yes")
            editor.apply()
        }

        binding.startBtn.setOnClickListener{
            startActivity(Intent(this@IntroActivity, Login::class.java))
            finish()
        }

    }
}