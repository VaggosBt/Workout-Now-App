package com.udemycourses.workoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import com.udemycourses.workoutapp.databinding.ActivityFinishScreenBinding
import java.util.*

class FinishScreenActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding : ActivityFinishScreenBinding? = null

    private var tts : TextToSpeech? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarFinishScreen)

        tts = TextToSpeech(this,this)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Workout Now"
        }




        binding?.toolbarFinishScreen?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding?.finishBtn?.setOnClickListener {
            finish()
        }

        Handler().postDelayed({
            speakOutTwoPhrases(
                "You have completed your workout!",
                "Good job!"
            )
        },500)

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language specified in not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed!")
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun speakOutTwoPhrases(phrase1: String, phrase2: String) {
        val utteranceText = "$phrase1, $phrase2"
        tts!!.speak(phrase1, TextToSpeech.QUEUE_FLUSH, null, utteranceText)
        tts!!.playSilentUtterance(50, TextToSpeech.QUEUE_ADD, null)
        tts!!.speak(phrase2, TextToSpeech.QUEUE_ADD, null, utteranceText)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

    }

}