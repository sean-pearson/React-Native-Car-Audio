package com.example.android.uamp.trackplayer.module

import android.content.ComponentName
import android.media.MediaMetadata
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.example.android.uamp.common.MusicServiceConnection
import com.example.android.uamp.media.MusicService
import com.example.android.uamp.media.extensions.*
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.annotation.Nonnull

data class Music (
    val music: List<Track>
)
interface Track {
    val id: String
    val title: String
    val album: String
    val artist: String
    val genre: String
    val source: String
    val image: String
    val trackNumber: Number
    val totalTrackCount: Number
    val duration: Number
    val site: String
}
interface PlaybackStateCallback {
    fun onPlaybackStateChanged(state: PlaybackStateCompat?)
}

class MusicModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val TAG = "MusicModule";
    private val scope = MainScope()
    private val context = reactContext
    private lateinit var musicServiceConnection: MusicServiceConnection
    init {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Code to execute on the main thread after 5 seconds
                musicServiceConnection = MusicServiceConnection.getInstance(
                    context,
                    ComponentName(context, MusicService::class.java)
                )
                musicServiceConnection.playbackState.observeForever { value ->
                    Log.d(TAG, value.toString())
                }
                musicServiceConnection.nowPlaying.observeForever { value ->
                    Log.d(TAG, value.mediaMetadata.toString())
                    val title = value?.getText(MediaMetadata.METADATA_KEY_TITLE)?.toString()?:""
                    val artist = value?.getText(MediaMetadata.METADATA_KEY_ARTIST)?.toString()?:""
                    val album = value?.getText(MediaMetadata.METADATA_KEY_ALBUM)?.toString()?:""
                    val duration = value?.getText(MediaMetadata.METADATA_KEY_DURATION)?.toString()?.toDouble()?:0.0
                    Log.d(TAG, "" + title + artist + album + duration)
                    val map: WritableMap = Arguments.createMap()
                    map.putString("title", title )
                    map.putString("artist", artist )
                    map.putString("album", album )
                    map.putDouble("duration", duration)
                    sendEvent("NOW_PLAYING", map)
                }
            }
        }, 5000)
        // Observe the playbackState on the main thread

    }

    override fun onCatalystInstanceDestroy() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                musicServiceConnection.playbackState.removeObserver { /* observer */ }
            }
        })
    }
    fun sendEvent(eventName: String, params: WritableMap) {
        val reactContext = reactApplicationContext
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @Nonnull
    override fun getName(): String {
        return "TrackPlayerModule"
    }

//    val mediaId: String,
//    val title: String,
//    val subtitle: String,
//    val albumArtUri: Uri,
//    val browsable: Boolean,
//    var playbackRes: Int
    @ReactMethod
    fun test(obj: ReadableMap, callback: Promise) = scope.launch {
//        val args = Bundle()
//        args.putString("url", "")
//        val x = musicServiceConnection.transportControls.sendCustomAction("TEST", args)
//        Log.d(TAG, x.toString())
//        val music = obj.getArray("music");
//        val idx0 = music!!.getMap(0)
//        val source = idx0.getString("source")
//        Log.d(TAG, source.toString())
//        val uri: Uri = Uri.parse(source.toString())
//

        callback.resolve(null)
    }

    @ReactMethod
    fun play( callback: Promise) = scope.launch {
        try {
            musicServiceConnection.transportControls.play()
            Log.d(TAG, "play");
        } catch (e: Exception) {
            callback.resolve(e)
        }
        callback.resolve(true)
    }



    @ReactMethod
    fun pause( callback: Promise) = scope.launch {
        try {
            musicServiceConnection.transportControls.pause()
            Log.d(TAG, "pause");
        } catch (e: Exception) {
            callback.resolve(e)
        }
        callback.resolve(true)

    }

    @ReactMethod
    fun togglePlay(callback: Promise)= scope.launch {
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        musicServiceConnection.transportControls.pause()
                    playbackState.isPauseEnabled -> musicServiceConnection.transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!"

                        )
                    }
                }
            }
        }
    }
    @ReactMethod
    fun skipToNext( callback: Promise) = scope.launch {
        try {
            musicServiceConnection.transportControls.skipToNext()
            Log.d(TAG, "skipToNext");
        } catch (e: Exception) {
            callback.resolve(e)
        }
        callback.resolve(true)
    }

    @ReactMethod
    fun skipToPrevious( callback: Promise) = scope.launch {
        try {
            musicServiceConnection.transportControls.skipToPrevious()
            Log.d(TAG, "skipToPrevious");
        } catch (e: Exception) {
            callback.resolve(e)
        }
        callback.resolve(true)
    }

    @ReactMethod
    fun getNowPlaying( callback: Promise) = scope.launch {
        try {
//            val value = musicServiceConnection.nowPlaying.value
//
//                Log.d(TAG, value?.mediaMetadata.toString())
//                val title = value?.getText(MediaMetadata.METADATA_KEY_TITLE)?.toString() ?: ""
//                val artist = value?.getText(MediaMetadata.METADATA_KEY_ARTIST)?.toString() ?: ""
//                val album = value?.getText(MediaMetadata.METADATA_KEY_ALBUM)?.toString() ?: ""
//                val duration =
//                    value?.getText(MediaMetadata.METADATA_KEY_DURATION)?.toString()?.toDouble()
//                        ?: 0.0
//                Log.d(TAG, "" + title + artist + album + duration)
//                val map: WritableMap = Arguments.createMap()
//                map.putString("title", title)
//                map.putString("artist", artist)
//                map.putString("album", album)
//                map.putDouble("duration", duration)
//                Log.d(TAG, "skipToPrevious");
//                callback.resolve(map)
        } catch (e: Exception) {
            callback.resolve(e)
        }
        callback.resolve(true)
    }
}
