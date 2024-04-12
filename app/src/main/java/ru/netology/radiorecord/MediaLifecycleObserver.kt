package ru.netology.radiorecord

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer

//class MediaLifecycleObserver : LifecycleEventObserver {

  //  private var player: ExoPlayer? = null
//    private var playWhenReady = true
//    private var mediaItemIndex = 0
//    private var playbackPosition = 0L

//    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//        when (event) {
//            Lifecycle.Event.ON_START -> {
//                if (Util.SDK_INT > 23) {
//                    initializePlayer()
//                }
//            }

//            Lifecycle.Event.ON_RESUME -> {
//                    initializePlayer()
//                }
//                Log.d("MyLog", "ON_RESUME")
//            }

//            Lifecycle.Event.ON_PAUSE -> {
//                    releasePlayer()
//                Log.d("MyLog", "ON_PAUSE")
//            }

//            Lifecycle.Event.ON_STOP -> {
//                    releasePlayer()
//                Log.d("MyLog", "ON_STOP")
//            }

//            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
//            else -> Unit
//        }
//    }
//}