package com.trackplayer.trackPlayerBridge


import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.common.util.concurrent.ListenableFuture
import com.trackplayer.media.library.MediaItemTree
import com.trackplayer.media.service.MediaService
import com.trackplayer.media.utils.CustomCommands
import com.trackplayer.media.utils.buildMediaItem
import com.trackplayer.media.utils.buildMediaItemBundle
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MediaModule"
class Module (reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext){

    private val context = reactContext
    private val scope = MainScope()
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    override fun initialize() {
        super.initialize()
        val sessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    }


    override fun onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy()
        Log.d(TAG, "onCatalystInstanceDestroy")

    }

    override fun getName(): String {
        return "ReactNativeBridgeConnectorModule"
    }

    fun sendEvent(eventName: String, params: WritableMap) {
        val reactContext = reactApplicationContext
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }


    @ReactMethod
    fun setupPlayer(data: ReadableMap?, promise: Promise) {
    }

    @ReactMethod
    fun updateOptions(data: ReadableMap?, callback: Promise) {
    }
    private fun mediaItemToString(mediaItem: MediaItem): String {
        return "MediaItem{" +
                "mediaId='" + mediaItem.mediaId + '\'' +
                ", title='" + mediaItem.mediaMetadata.title + '\'' +
                ", artist='" + mediaItem.mediaMetadata.artist + '\'' +
                ", album='" + mediaItem.mediaMetadata.albumTitle + '\'' +
                ", mimeType='" + mediaItem.mediaMetadata.mediaType + '\'' +
                '}'
    }
    val subtitleConfigurations: MutableList<MediaItem.SubtitleConfiguration> = mutableListOf()
    @ReactMethod
    fun add(callback: Promise) {
        val mediaBundle: Bundle = buildMediaItemBundle(
            masterPlaylist = "Test",
           title = "Chickens on a farm",
           mediaId = "[Item] Chickens on a farm",

            isPlayable = true,
            isBrowsable = false,
            mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
            album = "Spatial Audio",
            artist = "Watson Wu",
            genre = "Animals",
            sourceUri = "https://od-media.kcrw.com/kcrw/audio/website/music/mb/KCRW-morning_becomes_eclectic-morning_becomes_eclectic_playlist_april_13_2023-230413.mp3",
            imageUri = "https://storage.googleapis.com/uamp/Spatial Audio/Chickens.jpg"
        )
        val resp = controller?.sendCustomCommand(SessionCommand(CustomCommands.ADD_MEDIA_ITEM.name, Bundle.EMPTY), mediaBundle)
        Log.d(TAG, resp.toString())
    }

    @ReactMethod
    fun remove(data: ReadableArray?, callback: Promise) {

    }

    @ReactMethod
    fun updateMetadataForTrack(index: Int, map: ReadableMap?, callback: Promise){

    }

    @ReactMethod
    fun updateNowPlayingMetadata(map: ReadableMap?, callback: Promise) {

    }

    @ReactMethod
    fun clearNowPlayingMetadata(callback: Promise) {
    }

    @ReactMethod
    fun removeUpcomingTracks(callback: Promise)  {
    }

    @ReactMethod
    fun skip(index: Int, initialTime: Float, callback: Promise) {
    }

    @ReactMethod
    fun skipToNext(callback: Promise){

    }

    @ReactMethod
    fun skipToPrevious(callback: Promise){

    }

    @ReactMethod
    fun reset(callback: Promise) {
    }

    @ReactMethod
    fun play(callback: Promise) {

        controller?.play()
    }



    @ReactMethod
    fun pause(callback: Promise)  {
        controller?.pause()
    }


    @ReactMethod
    fun seekTo(seconds: Float, callback: Promise) {

    }

    @ReactMethod
    fun setVolume(volume: Float, callback: Promise) {
    }

    @ReactMethod
    fun getVolume(callback: Promise) {
    }

    @ReactMethod
    fun setRate(rate: Float, callback: Promise) {
    }

    @ReactMethod
    fun getRate(callback: Promise) {
    }

    @ReactMethod
    fun setRepeatMode(mode: Int, callback: Promise) {
    }

    @ReactMethod
    fun getRepeatMode(callback: Promise) {
    }

    @ReactMethod
    fun getTrack(index: Int, callback: Promise) {
    }

    @ReactMethod
    fun getQueue(callback: Promise) {
    }

    @ReactMethod
    fun getCurrentTrack(callback: Promise) {
    }

    @ReactMethod
    fun getDuration(callback: Promise) {
    }

    @ReactMethod
    fun getBufferedPosition(callback: Promise) {
    }

    @ReactMethod
    fun getPosition( callback: Promise) {
    }

    @ReactMethod
    fun getState( callback: Promise) {
    }




}