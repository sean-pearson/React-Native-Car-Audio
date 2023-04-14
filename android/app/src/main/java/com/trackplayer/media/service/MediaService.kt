package com.trackplayer.media.service

import android.os.Bundle
import android.util.Log
import androidx.media.MediaBrowserCompatUtils
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.util.MediaFormatUtil
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import androidx.media3.session.MediaSession.ControllerInfo
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.trackplayer.media.library.MediaItemTree
import com.trackplayer.media.utils.CustomCommands
import com.trackplayer.media.utils.MEDIA_ITEM_TREE_ENUM
import com.trackplayer.media.utils.extractMediaItemFromBundle


private const val TAG = "MediaService"
private const val ROOT_ID = "[rootID]"
private const val ALBUM_ID = "[albumID]"
class MediaService : MediaLibraryService() {
    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    private lateinit var player: ExoPlayer
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private val mediaBrowers: MutableList<ControllerInfo> = mutableListOf()
    private val mediaControllers: MutableList<ControllerInfo> = mutableListOf()
    override fun onGetSession(controllerInfo: ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        // Create and assign a custom Callback to the MediaSession
        player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build()
        MediaItemTree.initialize()
        mediaLibrarySession = MediaLibrarySession.Builder(this, player, librarySessionCallback).build()

    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession.release()
        }
        super.onDestroy()
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


    private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onCustomCommand(
            session: MediaSession,
            controller: ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            Log.d(TAG, "CustomCommand" + customCommand.customAction)
            if(customCommand.customAction == CustomCommands.ADD_MEDIA_ITEM.name){
                Log.d(TAG, "Add Media Item")
                val mediaItem: MediaItem = extractMediaItemFromBundle(args)

                val mediaItems: MutableList<MediaItem> = mutableListOf(mediaItem)
                val masterPlaylistName = args.getString(MEDIA_ITEM_TREE_ENUM.MASTER_PLAYLIST_ID.name)
                    ?: return super.onCustomCommand(session, controller, customCommand, args)

                val idsToUpdate = MediaItemTree.addMediaItems(masterPlaylistName, mediaItems)
                idsToUpdate.forEach{mediaId ->
                    Log.d(TAG, "Custom Call $mediaId")
                    val children = MediaItemTree.getChildren(mediaId)
                        ?: return super.onCustomCommand(session, controller, customCommand, args)

                    mediaBrowers.forEach{browser ->
                        mediaLibrarySession.notifyChildrenChanged(browser, mediaId, children.size,null)
                    }
                    mediaControllers.forEach{_controller ->
                        mediaLibrarySession.notifyChildrenChanged(_controller, mediaId, children.size,null)
                    }
                }


            }

            return super.onCustomCommand(session, controller, customCommand, args)
        }
        override fun onConnect(
            session: MediaSession,
            controller: ControllerInfo
        ): MediaSession.ConnectionResult {
            mediaControllers.add(controller)
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
                .add(SessionCommand(CustomCommands.ADD_MEDIA_ITEM.name, Bundle()))
                .add(SessionCommand(CustomCommands.ADD_MEDIA_ITEMS.name, Bundle()))
                .build()

            return MediaSession.ConnectionResult.accept(
                availableSessionCommands,
                connectionResult.availablePlayerCommands
            )
        }




        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Log.d(TAG, "onGetLibraryRoot")
            val maxChildrenCount = browser.connectionHints.getInt(MediaConstants.EXTRAS_KEY_ROOT_CHILDREN_LIMIT)
            Log.d(TAG, "Max $maxChildrenCount children")
            val x = browser.connectionHints.keySet()
            x.forEach{key ->
                Log.d(TAG, "key: $key")
            }
            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.getRootItem(), params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Log.d(TAG, "onGetItem")
            val item =
                MediaItemTree.getItem(mediaId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )
            return Futures.immediateFuture(LibraryResult.ofItem(item, /* params= */ null))
        }

        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            mediaBrowers.add(browser)
            Log.d(TAG, "onSubscribe")
            val children =
                MediaItemTree.getChildren(parentId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )
            session.notifyChildrenChanged(browser, parentId, children.size, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            Log.d(TAG, "onGetChildren ${parentId}")
            val children =
                MediaItemTree.getChildren(parentId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )

            return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            Log.d(TAG, "onAddMediaItems ${mediaItems.size}")

            val updatedMediaItems: MutableList<MediaItem> = mutableListOf()
            mediaItems.forEach { mediaItem ->
                Log.d(TAG, mediaItem.mediaId as String)

                if (mediaItem.requestMetadata.searchQuery != null) {
                    Log.d(TAG, "if")
                    getMediaItemFromSearchQuery(mediaItem.requestMetadata.searchQuery!!)
                }
                else {
                    Log.d(TAG, "else")
                    if(MediaItemTree.getItem(mediaItem.mediaId)?.localConfiguration==null){
                        val children = MediaItemTree.getChildren(mediaItem.mediaId)
                        children?.forEach { child ->
                        updatedMediaItems.add(child)
                    }
                    } else {
                        updatedMediaItems.add(MediaItemTree.getItem(mediaItem.mediaId) ?: mediaItem)
                    }
                }
            }
            return Futures.immediateFuture(updatedMediaItems)
        }


        private fun getMediaItemFromSearchQuery(query: String): MediaItem {
            Log.d(TAG, "getMediaItemFromSearchQuery")
            // Only accept query with pattern "play [Title]" or "[Title]"
            // Where [Title]: must be exactly matched
            // If no media with exact name found, play a random media instead
            val mediaTitle =
                if (query.startsWith("play ", ignoreCase = true)) {
                    query.drop(5)
                } else {
                    query
                }

            return MediaItemTree.getItemFromTitle(mediaTitle) ?: MediaItemTree.getRandomItem()
        }
    }
}
const val REWIND_30 = "REWIND_30"
const val FAST_FWD_30 = "FAST_FWD_30"
