package ru.netology.radiorecord

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.netology.radiorecord.adapter.Listener
import ru.netology.radiorecord.adapter.RadioAdapter
import ru.netology.radiorecord.databinding.ActivityMainBinding
import ru.netology.radiorecord.dto.Station
import ru.netology.radiorecord.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    var flagPlay: Boolean = false
    var firstStart: Boolean = true

    private val viewModel: MainViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    private var player: ExoPlayer? = null

    private val playbackStateListener: Player.Listener = playbackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        _binding = binding
        setContentView(binding.root)

        this.window.statusBarColor = this.getColor(R.color.gray)

        val adapter = RadioAdapter(object : Listener {
            override fun highlight(station: Station) {
                flagPlay = false
                firstStart = true
                releasePlayer()
                binding.buttonPlay.setImageResource(R.drawable.play_80)
                viewModel.highlight(station)
            }
        })

        binding.rvRadio.layoutManager = LinearLayoutManager(this)
        binding.rvRadio.adapter = adapter

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
                    initializePlayer()
                    showText("playing")

                    //продолжение после паузы
                } else {
                    player?.play()
                    showText("playing again")
                }
                flagPlay = true

                //пауза
            } else {
                player?.pause()
                flagPlay = false
                showText("pause")
            }

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
            if (it != null) {
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.textShortRadio,
                    PropertyValuesHolder.ofFloat(View.ROTATION, 0F, 360F),
                ).setDuration(1000L)
                    .start()
                binding.textShortRadio.text = it.short_title

            } else {
                binding.textShortRadio.text = getString(R.string.choose_radio)
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

    override fun onResume() {
        super.onResume()
        _binding?.buttonPlay?.setImageResource(if (flagPlay) R.drawable.pause_80 else R.drawable.play_80)
    }

    override fun onPause() {
        super.onPause()
        if (player?.isPlaying==false) {
                releasePlayer()
        }

    }

    override fun onStop() {
        if (player?.isPlaying==false) {
            releasePlayer()
        }
        super.onStop()
    }

    override fun onDestroy() {
        _binding = null
        releasePlayer()
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
                viewModel.selectedTrack.value?.stream_128?.let {
                    MediaItem.fromUri(it)
                    exoPlayer.setMediaItems(listOf(MediaItem.fromUri(it)))
                    exoPlayer.playWhenReady = true
                    exoPlayer.addListener(playbackStateListener) //Чтобы вызывать обратные вызовы, необходимо зарегистрировать их playbackStateListener в плеере, до prepare
                    exoPlayer.prepare()
                }
            }
    }

    private fun releasePlayer() {
        firstStart = true
        flagPlay = false
        player?.release()
        player?.removeListener(playbackStateListener)
        player = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {

            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    _binding?.buttonPlay?.setImageResource(R.drawable.play_80)
                    _binding?.buttonPlay?.isEnabled = false
                    _binding?.progress?.visibility = View.VISIBLE
                }

                ExoPlayer.STATE_BUFFERING -> {
                    _binding?.progress?.visibility = View.VISIBLE
                    _binding?.buttonPlay?.isEnabled = false
                }
                ExoPlayer.STATE_READY -> {
                    _binding?.progress?.visibility = View.GONE
                    _binding?.buttonPlay?.isEnabled = true
                }
                ExoPlayer.STATE_ENDED -> {
                }
                else -> {
                }
            }
        }
    }
}