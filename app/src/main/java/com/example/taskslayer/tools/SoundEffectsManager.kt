package com.example.taskslayer.tools

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.taskslayer.R

/**
 * Gerenciador de efeitos sonoros do aplicativo.
 * Utiliza o SoundPool do Android para reprodução rápida de sons curtos,
 * como o efeito de "corte de espada" ao completar tarefas.
 */
class SoundEffectsManager(
    context: Context?,
    private val soundPool: SoundPool = createDefaultSoundPool()
) {

    private var slashSoundId: Int = 0
    private var isLoaded = false

    companion object {
        /**
         * Cria uma instância padrão de SoundPool configurada para efeitos de jogo.
         */
        private fun createDefaultSoundPool(): SoundPool {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            return SoundPool.Builder()
                .setMaxStreams(1) // Permite apenas um som por vez para não sobrepor
                .setAudioAttributes(audioAttributes)
                .build()
        }
    }

    init {
        // Carrega o som de corte (slash) da pasta 'res/raw'
        slashSoundId = soundPool.load(context, R.raw.sword_slash_sound, 1)

        // Callback para garantir que o som só seja tocado após carregar totalmente
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) isLoaded = true
        }
    }

    /**
     * Toca o efeito sonoro de corte de espada.
     */
    fun playSlashSound() {
        if (isLoaded) {
            // Parâmetros: id, volumeEsq, volumeDir, prioridade, loop (0 = não), taxa (1.0 = normal)
            soundPool.play(slashSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * Libera os recursos do SoundPool quando não for mais necessário.
     */
    fun release() {
        soundPool.release()
    }
}
