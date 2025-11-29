package com.example.quizapp

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

class SoundManager(private val context: Context) {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    fun playCorrectSound() {
        try {
            // 정답: 높은 음 2번 (띵띵)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
            }, 200)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playWrongSound() {
        try {
            // 오답: 낮은 경고음 (삐-)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, 400)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        toneGenerator.release()
    }
}
