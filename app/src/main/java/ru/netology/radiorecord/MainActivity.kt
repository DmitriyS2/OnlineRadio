package ru.netology.radiorecord


import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.netology.radiorecord.adapter.Listener
import ru.netology.radiorecord.adapter.RadioAdapter
import ru.netology.radiorecord.databinding.ActivityMainBinding
import ru.netology.radiorecord.dto.Station
import ru.netology.radiorecord.viewmodel.MainViewModel
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    var flagPlay: Boolean = false
    var firstStart: Boolean = true

    //    private val mediaObserver = MediaLifecycleObserver()
    private var _binding: ActivityMainBinding? = null
    //  lateinit var binding: ActivityMainBinding

    private var player: ExoPlayer? = null

    //   private var playWhenReady = true
    //  private var mediaItemIndex = 0
    //   private var playbackPosition = 0L

    private val playbackStateListener: Player.Listener = playbackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        _binding = binding
        setContentView(binding.root)
        //    lifecycle.addObserver(mediaObserver)
        this.window.statusBarColor = this.getColor(R.color.gray)

        val adapter = RadioAdapter(object : Listener {
            override fun highlight(station: Station) {
                flagPlay = false
                firstStart = true
                releasePlayer()
                binding.buttonPlay.setImageResource(R.drawable.play_80)
                //    mediaObserver.stopTrack()

                viewModel.highlight(station)
            }

        })

        binding.rvRadio.layoutManager = LinearLayoutManager(this)
        binding.rvRadio.adapter = adapter

        //  binding.buttonPlay.setImageResource(if (player?.isPlaying==false) R.drawable.pause_80 else R.drawable.play_80)
        binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.pause_80 else R.drawable.play_80)
        binding.buttonPlay.isEnabled = viewModel.dataModel.value?.listRadio != null

        binding.buttonPlay.setOnClickListener {

            ObjectAnimator.ofPropertyValuesHolder(
                binding.buttonPlay,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.15F, 1.0F),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.15F, 1.0F)
            ).start()

            if (!flagPlay) {
                //первый старт
                if (firstStart) {
                    //     mediaObserver.firstStartPlay(viewModel.selectedTrack.value.toString())
                    initializePlayer()
                    showText("playing")
                    //продолжение после паузы
                } else {
                    player?.play()
                    //   mediaObserver.notFirstStartPlay()
                    showText("playing again")
                }
                flagPlay = true
                //пауза
            } else {
                //   mediaObserver.pauseTrack()
                player?.pause()
                flagPlay = false
                showText("pause")
            }

//            mediaObserver.player?.setOnCompletionListener {
//                mediaObserver.stopTrack()
//                viewModel.goToNextTrack()
//                mediaObserver.firstStartPlay(viewModel.selectedTrack.value.toString())
//                showText("playing")
//            }

            //       viewModel.changeImageTrack(viewModel.selectedTrack.value, flagPlay)
            binding.buttonPlay.setImageResource(if (flagPlay) R.drawable.pause_80 else R.drawable.play_80)
        }

        binding.retryButton.setOnClickListener {
            Log.d("MyLog", "retryButton")
            ObjectAnimator.ofPropertyValuesHolder(
                binding.retryButton,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
            ).start()
            viewModel.getAlbum()
        }

        viewModel.dataModel.observe(this) {
            it?.let { dataModel ->
                binding.progress.isVisible = dataModel.loading
                binding.errorGroup.isVisible = dataModel.error
                binding.buttonPlay.isEnabled = !dataModel.error

                viewModel.changeListRadio()
            }
        }

        viewModel.listStations.observe(this) {
            adapter.submitList(it)
        }

        viewModel.selectedTrack.observe(this) {
            Log.d("MyLog", "selectedTrack = $it")

            if (it != null) {
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.textShortRadio,
                    PropertyValuesHolder.ofFloat(View.ROTATION, 0F, 360F),
                ).setDuration(1000L)
                    .start()
                binding.textShortRadio.text = it.short_title

            } else {
                binding.textShortRadio.text = "Выберите\nрадио"
            }

            binding.textDesc.text = it?.tooltip ?: " \n "
            val str = it?.genre?.joinToString(separator = " / ") { genre ->
                genre.name
            } ?: " "
            binding.textGenre.text = str

            Glide.with(binding.avatar)
                .load(it?.bg_image_mobile)
                .into(binding.avatar)

            binding.buttonPlay.isEnabled = it != null
        }
    }

//    @OptIn(UnstableApi::class)
    //  override fun onStart() {
    //  super.onStart()
//    _binding?.buttonPlay?.setImageResource(if (flagPlay) R.drawable.pause_80 else R.drawable.play_80)
    //          initializePlayer()

    //   }
    @OptIn(UnstableApi::class)
    override fun onResume() {
        super.onResume()
        _binding?.buttonPlay?.setImageResource(if (flagPlay) R.drawable.pause_80 else R.drawable.play_80)
        //    hideSystemUi()
//        if ((Util.SDK_INT <= 23 || player == null)) {
//            //        initializePlayer()
//        }

    }

    //   @OptIn(UnstableApi::class)
    override fun onPause() {
        super.onPause()
        //    if (Util.SDK_INT <= 23) {
        releasePlayer()
        //      }
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun showText(text: String) {
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initializePlayer() {
        firstStart = false
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                //      binding.player.player = exoPlayer
                //адаптивная потоковая передача (trackSelectionParameters)- Обычно один и тот же медиаконтент разбивается
                // на несколько дорожек разного качества (скорость передачи данных и разрешение).
                // Плеер выбирает трек исходя из доступной пропускной способности сети.
//                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
//                    .buildUpon()
//                    .setMaxVideoSizeSd()
//                    .build()

                //      val radio = viewModel.selectedTrack.value?.stream_128?.let { MediaItem.fromUri(it) }
                viewModel.selectedTrack.value?.stream_128?.let {
                    MediaItem.fromUri(it)
                    exoPlayer.setMediaItems(listOf(MediaItem.fromUri(it)))
                    exoPlayer.playWhenReady = true
                    exoPlayer.addListener(playbackStateListener) //Чтобы вызывать обратные вызовы, вам необходимо зарегистрировать их playbackStateListener в плеере, до prepare
                    exoPlayer.prepare()
                }
                //    exoPlayer.setMediaItems(listOf(radio), mediaItemIndex, playbackPosition)
                //   exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)


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
        firstStart = true
        flagPlay = false
        player?.release()
        player?.removeListener(playbackStateListener)
        player = null
    }


    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            var stateString = ""
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    stateString = "ExoPlayer.STATE_IDLE      -"
                    _binding?.buttonPlay?.setImageResource(R.drawable.play_80)
                    _binding?.buttonPlay?.isEnabled = false
                    _binding?.progress?.visibility = View.VISIBLE
                }

                ExoPlayer.STATE_BUFFERING -> {
                    stateString = "ExoPlayer.STATE_BUFFERING -"
                    _binding?.progress?.visibility = View.VISIBLE
                    _binding?.buttonPlay?.isEnabled = false
                }
                ExoPlayer.STATE_READY -> {
                    stateString ="ExoPlayer.STATE_READY     -"
                    _binding?.progress?.visibility = View.GONE
                    _binding?.buttonPlay?.isEnabled = true
                }
                ExoPlayer.STATE_ENDED -> {
                    stateString = "ExoPlayer.STATE_ENDED     -"
                }
                else -> {
                    stateString ="UNKNOWN_STATE             -"
                }
            }
            Log.d("MyLog", "changed state to $stateString")
        }
    }
}