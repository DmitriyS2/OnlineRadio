package ru.netology.radiorecord

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer

class MediaLifecycleObserver : LifecycleEventObserver {
    var player: MediaPlayer? = MediaPlayer()

  //  private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private fun play() {
        player?.setOnPreparedListener {
            it.start()
            Log.d("MyLog", "fun play")
        }
        player?.prepareAsync()
    }

    fun firstStartPlay(nameTrack: String) {
        firstStart = false

        if (player == null) {
            player = MediaPlayer()

        //    player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
     //   player?.setDataSource("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/$nameTrack")
      //  player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player?.setAudioAttributes(audioAttributes)
        player?.setDataSource(nameTrack)

//        Log.d(
//            "MyLog",
//            "URL=https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/$nameTrack"
//        )
        Log.d(
            "MyLog",
            "firstStartPlay"
        )
        play()
    }

    fun notFirstStartPlay() {
        player?.currentPosition?.let { current ->
            player?.seekTo(current)
        }
        player?.start()
        Log.d(
            "MyLog",
            "notFirstStartPlay"
        )
    }

    fun pauseTrack() {
        player?.pause()
        flagPlay = false
        Log.d(
            "MyLog",
            "pauseTrack"
        )
    }

    fun stopTrack() {
        player?.stop()
        player?.reset()
        Log.d(
            "MyLog",
            "stopTrack"
        )
    }

    @OptIn(UnstableApi::class)
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
//            Lifecycle.Event.ON_START -> {
//                if (Util.SDK_INT > 23) {
//                    initializePlayer()
//                }
//            }

//            Lifecycle.Event.ON_RESUME -> {
//                //    hideSystemUi()
//                if ((Util.SDK_INT <= 23 || player == null)) {
//                    initializePlayer()
//                }
//                Log.d("MyLog", "ON_RESUME")
//            }

//            Lifecycle.Event.ON_PAUSE -> {
//                if (Util.SDK_INT <= 23) {
//                    releasePlayer()
//                }
//                player?.pause()
//                flagPlay = false
//                Log.d("MyLog", "ON_PAUSE")
//            }

//            Lifecycle.Event.ON_STOP -> {
//                if (Util.SDK_INT > 23) {
//                    releasePlayer()
//                }
//                player?.release()
//                player = null
//                flagPlay = false
//                firstStart = true
//                Log.d("MyLog", "ON_STOP")
//            }

//            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }

//    private fun initializePlayer() {
//        player = ExoPlayer.Builder()
//            .build()
//            .also { exoPlayer ->
//                binding.player.player = exoPlayer
//                //адаптивная потоковая передача (trackSelectionParameters)- Обычно один и тот же медиаконтент разбивается
//                // на несколько дорожек разного качества (скорость передачи данных и разрешение).
//                // Плеер выбирает трек исходя из доступной пропускной способности сети.
//                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
//                    .buildUpon()
//                    .setMaxVideoSizeSd()
//                    .build()
//
//                val second = MediaItem.fromUri(BASE_URL)
//                exoPlayer.setMediaItems(listOf(second), mediaItemIndex, playbackPosition)
//                //   exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
//
//                exoPlayer.playWhenReady = playWhenReady
//                exoPlayer.addListener(playbackStateListener) //Чтобы вызывать обратные вызовы, вам необходимо зарегистрировать их playbackStateListener в плеере, до prepare
//                exoPlayer.prepare()
//            }
//    }
}