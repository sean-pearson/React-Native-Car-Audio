package com.car.audio.media.service

import android.os.Bundle
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import androidx.media3.session.MediaSession.ControllerInfo
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.car.audio.media.library.MediaItemTree
import com.car.audio.media.utils.CustomCommands
import com.car.audio.media.utils.SetViewStyleCustomCommand


private const val TAG = "MediaService"
@UnstableApi class MediaService : MediaLibraryService() {
    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    private lateinit var player: ExoPlayer
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private val mediaBrowsers: MutableList<ControllerInfo> = mutableListOf()
    private val mediaControllers: MutableList<ControllerInfo> = mutableListOf()
    override fun onGetSession(controllerInfo: ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    override fun onCreate() {
        super.onCreate()
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
    private fun updateChildrenOf(mediaItems: MutableList<String>){
        mediaItems.forEach{item ->
            Log.d(TAG, item)
            notifyChildrenChanged(item)
        }
    }

    private fun notifyChildrenChanged(mediaId: String){
        val children = MediaItemTree.getChildren(mediaId)
            ?: return

        mediaBrowsers.forEach{browser ->
            mediaLibrarySession.notifyChildrenChanged(browser, mediaId, children.size,null)
        }
        mediaControllers.forEach{controller ->
            mediaLibrarySession.notifyChildrenChanged(controller, mediaId, children.size,null)
        }
    }

    private fun setViewStyles(args: Bundle){
        MediaItemTree.setViewStyles(playableStyle = args.getInt(SetViewStyleCustomCommand.PLAYABLE_VIEW_STYLE.name),
            browsableStyle = args.getInt(SetViewStyleCustomCommand.BROWSABLE_VIEW_STYLE.name)
        )

    }

  private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onCustomCommand(
            session: MediaSession,
            controller: ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            Log.d(TAG, "CustomCommand: " + customCommand.customAction)
            when (customCommand.customAction){
                CustomCommands.SET_VIEW_STYLES.name -> {
                    setViewStyles(args)
                }

                CustomCommands.ADD_MEDIA_ITEM.name -> {
                    updateChildrenOf(MediaItemTree.addMediaItem(args))

                }
                CustomCommands.ADD_TAB.name -> {
                    MediaItemTree.addTab(args)
                    notifyChildrenChanged(MediaItemTree.ROOT_ID)

                }
                CustomCommands.ADD_NODE.name -> {
                    updateChildrenOf(MediaItemTree.addNode(args))
                }
                CustomCommands.RESET_PLAYER.name -> {
                    MediaItemTree.resetRoot()
                    notifyChildrenChanged(MediaItemTree.ROOT_ID)
                }
            }
            Log.d(TAG, "Returning From Custom Command")

            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
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
                .add(SessionCommand(CustomCommands.SET_VIEW_STYLES.name, Bundle()))
                .add(SessionCommand(CustomCommands.ADD_TAB.name, Bundle()))
                .add(SessionCommand(CustomCommands.ADD_NODE.name, Bundle()))
                .add(SessionCommand(CustomCommands.RESET_PLAYER.name, Bundle()))
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
            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.getRootItem(), params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
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
            mediaBrowsers.add(browser)
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
            val children =
                MediaItemTree.getChildren(parentId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )

            return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
        }

      override fun onSetMediaItems(
          mediaSession: MediaSession,
          controller: ControllerInfo,
          mediaItems: MutableList<MediaItem>,
          startIndex: Int,
          startPositionMs: Long
      ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
          var index = startIndex;
          val updatedMediaItems: MutableList<MediaItem> = mutableListOf()
          mediaItems.forEach { mediaItem ->
              if (mediaItem.requestMetadata.searchQuery != null) {
                  getMediaItemFromSearchQuery(mediaItem.requestMetadata.searchQuery!!)
              } else {
                  if(MediaItemTree.getItem(mediaItem.mediaId)?.localConfiguration==null){
                      val children = MediaItemTree.getChildren(mediaItem.mediaId)
                      children?.forEach { child ->
                          updatedMediaItems.add(child)
                      }
                  } else {
                      MediaItemTree.getItemsSiblings(mediaItem.mediaId)?.forEachIndexed{ idx, sibling ->
                          if(mediaItem.mediaId == sibling.mediaId){
                              index = idx
                          }
                          updatedMediaItems.add(sibling)
                      }
                  }
              }
          }
          return super.onSetMediaItems(
              mediaSession,
              controller,
              updatedMediaItems,
              index,
              startPositionMs
          )
      }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            return Futures.immediateFuture(mediaItems)
        }

        private fun getMediaItemFromSearchQuery(query: String): MediaItem {
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
