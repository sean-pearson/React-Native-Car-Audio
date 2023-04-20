package com.trackplayer.trackPlayerBridge


import android.content.ComponentName
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.*
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.trackplayer.media.library.MediaItemTree
import com.trackplayer.media.service.MediaService
import com.trackplayer.media.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

private const val TAG = "MediaModule"
@UnstableApi class Module (reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext){

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

    private fun viewStyleTransform(style: String): Int{
        return if (style == "GRID") MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM else MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM
    }

    @ReactMethod
    fun updateViewStyles(browsableStyle: String, playableStyle: String, callback: Promise){
        try {
            val setupPlayerBundle: Bundle = Bundle()
            setupPlayerBundle.putInt(
                SetViewStyleCustomCommand.PLAYABLE_VIEW_STYLE.name,
                viewStyleTransform(playableStyle)
            )
            setupPlayerBundle.putInt(
                SetViewStyleCustomCommand.BROWSABLE_VIEW_STYLE.name,
                viewStyleTransform(browsableStyle)
            )

            val resp = controller?.sendCustomCommand(
                SessionCommand(
                    CustomCommands.SET_VIEW_STYLES.name,
                    Bundle.EMPTY
                ), setupPlayerBundle
            )
            awaitCallback(resp!!, callback)
        } catch (e: Exception ){
            return callback.reject(e)
        }
    }

    //TODO: implement function
    private fun mimeTypeToMediaType(type:String):Int{
        return MediaMetadata.MEDIA_TYPE_MUSIC
    }

    private fun awaitCallback(resp: ListenableFuture<SessionResult>, callback: Promise){
        if(resp==null){
            callback.reject(Throwable("Response was null"))
        }
        val executor = Executors.newSingleThreadScheduledExecutor()
        val decorator = MoreExecutors.listeningDecorator(executor)

        Futures.addCallback(
            resp,
            object : FutureCallback<SessionResult> {
                override fun onSuccess(result: SessionResult) {
                    // handle success
                    callback.resolve(result.resultCode)
                }

                override fun onFailure(t: Throwable) {
                    // handle failure
                    callback.reject(t)
                }
            },
            decorator
        )
    }

    @ReactMethod
    fun resetPlayer(callback: Promise){
        Log.d(TAG, "resetPlayer")
        val resp = controller?.sendCustomCommand(SessionCommand(CustomCommands.RESET_PLAYER.name, Bundle.EMPTY), Bundle.EMPTY)
        awaitCallback(resp!!, callback)
    }

    @ReactMethod
    fun loadTab(uuid: String, title: String, callback: Promise){
        Log.d(TAG, "loadTab")
        val tabBundle: Bundle = Bundle();
        tabBundle.putString(MediaItemBundleKey.MEDIA_ID.name, uuid)
        tabBundle.putString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name, title)
        val resp = controller?.sendCustomCommand(SessionCommand(CustomCommands.ADD_TAB.name, Bundle.EMPTY), tabBundle)
        awaitCallback(resp!!, callback)
    }

    @ReactMethod
    fun loadNode(uuid: String, parentId: String, title: String, imageUri: String, callback: Promise){
        Log.d(TAG, "loadNode")
        val nodeBundle: Bundle = Bundle();
        nodeBundle.putString(MediaItemBundleKey.MEDIA_ID.name, uuid)
        nodeBundle.putString(MediaItemBundleKey.PARENT_ID.name, parentId)
        nodeBundle.putString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name, title)
        nodeBundle.putString(MediaItemBundleKey.MEDIA_METADATA_ARTWORK_URI.name, imageUri)
        val resp = controller?.sendCustomCommand(SessionCommand(CustomCommands.ADD_NODE.name, Bundle.EMPTY), nodeBundle)
        awaitCallback(resp!!, callback)
    }

    @ReactMethod
    fun loadMediaItem(uuid: String, parentId: String, data: ReadableMap, callback: Promise){
        Log.d(TAG, "loadMediaItem")
        val mediaItemBundle: Bundle = Bundle();
        mediaItemBundle.putString(MediaItemBundleKey.PARENT_ID.name, parentId)
        mediaItemBundle.putAll(buildMediaItemBundle(data))
        val resp = controller?.sendCustomCommand(SessionCommand(CustomCommands.ADD_MEDIA_ITEM.name, Bundle.EMPTY), mediaItemBundle)
        awaitCallback(resp!!, callback)
    }

    @ReactMethod
    fun skipToNext(callback: Promise){
        val resp = controller?.seekToNextMediaItem()
    }

    @ReactMethod
    fun skipToPrevious(callback: Promise){
        val resp = controller?.seekToPreviousMediaItem()
    }

    @ReactMethod
    fun reset(callback: Promise) {
    }

    @ReactMethod
    fun play(callback: Promise) {
        val resp = controller?.play()
    }

    @ReactMethod
    fun pause(callback: Promise)  {
        val resp = controller?.pause()
    }

    @ReactMethod
    fun seekTo(positionMs: Long, callback: Promise) {
        val resp = controller?.seekTo(positionMs)
    }
}