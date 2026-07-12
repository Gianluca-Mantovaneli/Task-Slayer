package com.example.taskslayer.tools

import android.content.Context
import android.media.SoundPool
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class SoundEffectsManagerTest {

    private lateinit var context: Context
    private lateinit var soundPool: SoundPool
    private lateinit var soundEffectsManager: SoundEffectsManager

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        soundPool = mockk(relaxed = true)

        // Return a dummy sound ID
        every { soundPool.load(any<Context>(), any(), any()) } returns 1

        soundEffectsManager = SoundEffectsManager(context, soundPool)
    }

    @After
    fun teardown() {
    }

    @Test
    fun `playSlashSound testando o tocar do som`() {
        // Given
        val slot = slot<SoundPool.OnLoadCompleteListener>()
        verify { soundPool.setOnLoadCompleteListener(capture(slot)) }

        // Simulate load complete with status 0 (Success)
        slot.captured.onLoadComplete(soundPool, 1, 0)

        // When
        soundEffectsManager.playSlashSound()

        // Then
        verify { soundPool.play(1, 1f, 1f, 1, 0, 1f) }
    }

    @Test
    fun `playSlashSound testando o tocar do som com erro`() {
        // When
        soundEffectsManager.playSlashSound()

        // Then
        verify(exactly = 0) { soundPool.play(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `release testando o release do som`() {
        // When
        soundEffectsManager.release()

        // Then
        verify { soundPool.release() }
    }
}
