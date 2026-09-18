package com.myapp.mysimon.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.myapp.mysimon.R

/**
 * Manages audio feedback for the game using SoundPool.
 * Responsible for loading and playing sounds for colored buttons and game errors.
 */
class GameAudioManager(context: Context) {
    // SoundPool instance for low-latency audio playback
    private val soundPool: SoundPool

    // Mapping of logical indices (0-5 for colors, 99 for error) to SoundPool sound IDs
    private val soundMap = HashMap<Int, Int>()
    
    // Flag to track if sounds are ready to be played
    private var isLoaded = false

    init {
        // Configure audio attributes for a gaming context
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Initialize SoundPool with a capacity for 6 concurrent streams
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sound resources into the SoundPool and store their IDs
        soundMap[0] = soundPool.load(context, R.raw.color_0, 1)
        soundMap[1] = soundPool.load(context, R.raw.color_1, 1)
        soundMap[2] = soundPool.load(context, R.raw.color_2, 1)
        soundMap[3] = soundPool.load(context, R.raw.color_3, 1)
        soundMap[4] = soundPool.load(context, R.raw.color_4, 1)
        soundMap[5] = soundPool.load(context, R.raw.color_5, 1)
        soundMap[99] = soundPool.load(context, R.raw.error, 1)

        // Set a listener to enable playback once loading is complete
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }
    }

    /**
     * Plays the sound associated with the provided button index.
     * @param buttonIndex The index identifying the specific sound to play.
     */
    fun playSound(buttonIndex: Int) {
        val soundId = soundMap[buttonIndex]
        if (isLoaded && soundId != null) {
            // Play with full volume on both channels, normal priority, no looping, at normal speed
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    /**
     * Releases the SoundPool resources when the manager is no longer needed.
     */
    fun release() {
        soundPool.release()
    }
}
