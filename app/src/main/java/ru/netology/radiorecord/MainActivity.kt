package ru.netology.radiorecord


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import ru.netology.radiorecord.databinding.ActivityMainBinding
import java.io.IOException


var flagPlay: Boolean = false
var firstStart: Boolean = true

class MainActivity : AppCompatActivity() {

//    private val mediaObserver = MediaLifecycleObserver()
    private var _binding:ActivityMainBinding? = null
  //  lateinit var binding: ActivityMainBinding

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private val playbackStateListener: Player.Listener = playbackStateListener()

    val BASE_URL = "https://radiorecord.hostingradio.ru/sd9096.aacp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        _binding = binding
        setContentView(binding.root)

    //    lifecycle.addObserver(mediaObserver)
        this.window.statusBarColor = this.getColor(R.color.gray)

    }

    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onResume() {
        super.onResume()
        //    hideSystemUi()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    private fun showText(text: String) {
        Toast.makeText(
            this,
            //      "${viewModel.selectedTrack.value.toString()} $text",
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
          //      binding.player.player = exoPlayer
                //адаптивная потоковая передача (trackSelectionParameters)- Обычно один и тот же медиаконтент разбивается
                // на несколько дорожек разного качества (скорость передачи данных и разрешение).
                // Плеер выбирает трек исходя из доступной пропускной способности сети.
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()

                val second = MediaItem.fromUri(BASE_URL)
                exoPlayer.setMediaItems(listOf(second), mediaItemIndex, playbackPosition)
                //   exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.addListener(playbackStateListener) //Чтобы вызывать обратные вызовы, вам необходимо зарегистрировать их playbackStateListener в плеере, до prepare
                exoPlayer.prepare()
            }
    }

    //это вспомогательный метод, вызываемый в onResume, который позволяет работать в полноэкранном режиме.
//    @SuppressLint("InlinedApi")
//    private fun hideSystemUi() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, binding.player).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition //текущая позиция воспроизведения
            mediaItemIndex = exoPlayer.currentMediaItemIndex //Индекс текущего медиа-элемента
            playWhenReady = exoPlayer.playWhenReady //состояние воспроизведения/паузы
            exoPlayer.removeListener(playbackStateListener) //чтобы избежать висящих ссылок из плеера, которые могут вызвать утечку памяти, надо удалить слушатель
            exoPlayer.release()
        }
        player = null
    }
}

private fun playbackStateListener() = object : Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
        val stateString: String = when (playbackState) {
            ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
            ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
        Log.d("MyLog", "changed state to $stateString")
    }
}