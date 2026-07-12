package com.example.taskslayer.tools

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.taskslayer.R

class SoundEffectsManager(
    context: Context?,
    private val soundPool: SoundPool = createDefaultSoundPool()
) {

    private var slashSoundId: Int = 0
    private var isLoaded = false

    companion object {
        private fun createDefaultSoundPool(): SoundPool {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            return SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build()
        }
    }

    init {
        // Carrega o som da pasta raw e descobre o ID dele
        slashSoundId = soundPool.load(context, R.raw.sword_slash_sound, 1)

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) isLoaded = true
        }
    }

    fun playSlashSound() {
        if (isLoaded) {
            // toca o som: id, volumeEsq, volumeDir, prioridade, loop (0 = não), taxaDeVelocidade (1.0 = normal)
            soundPool.play(slashSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    // libera memória caso o app feche
    fun release() {
        soundPool.release()
    }
}