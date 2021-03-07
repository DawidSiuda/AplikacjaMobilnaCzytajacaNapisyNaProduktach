package com.example.foodadditivelabeldecoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button;
import android.widget.EditText
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    val items = HashMap<String, String>()

    fun FillMap(){
        items["E100"] = "kurkumina"
        items["E101"] = "ryboflawina"
        items["E103"] = "tartrazyna"
        items["E104"] = "żółcień chinolinowa"
        items["E110"] = "żółć pomarańczowa FCF"
        items["E120"] = "kwas karminowy, koszenila"
        items["E122"] = "azorubina"
        items["E123"] = "amarant"
        items["E124"] = "czerwień koszenilowa"
        items["E127"] = "erytrozyna"
        items["E128"] = "czerwień 2G"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FillMap()

        val text = findViewById<EditText>(R.id.LK_DecodedText)

        val button = findViewById<Button>(R.id.LK_RunButton)
        button.setOnClickListener {
            var randomNumber = (0..10).random()

            for(element in items.iterator()){
                if (randomNumber == 0)
                    text.setText(element.key + ": " + element.value)
                randomNumber -= 1
            }
        }
    }
}