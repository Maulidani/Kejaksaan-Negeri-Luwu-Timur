package go.kejaksaannegeriluwutimur.view.kepaladesa

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import go.kejaksaannegeriluwutimur.R

class ChatContentsActivity : AppCompatActivity() {
    private val tvUserName: TextView by lazy { findViewById(R.id.tv_user_name) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_contents)

        setUpUi()
    }

    private fun setUpUi() {
        val name = intent.getStringExtra("name")
        tvUserName.text = name
    }
}