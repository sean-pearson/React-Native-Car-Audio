/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trackplayer.media.library

import android.content.res.AssetManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.Util
import com.google.common.collect.ImmutableList
import org.json.JSONObject

/**
 * A sample media catalog that represents media items as a tree.
 *
 * It fetched the data from {@code catalog.json}. The root's children are folders containing media
 * items from the same album/artist/genre.
 *
 * Each app should have their own way of representing the tree. MediaItemTree is used for
 * demonstration purpose only.
 */
private const val TAG = "MediaItemTree"

object MediaItemTree {
  private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
  private var titleMap: MutableMap<String, MediaItemNode> = mutableMapOf()
  private var isInitialized = false
  private const val ROOT_ID = "[rootID]"
  private const val MASTER_PLAYLIST_ID = "[masterPlaylistID]"
  private const val PLAYLIST_ID = "[playlistID]"
  private const val PLAYLIST_PREFIX = "[playlist]"
  private const val ITEM_PREFIX = "[item]"

  private class MediaItemNode(val item: MediaItem) {
    private val children: MutableList<MediaItem> = ArrayList()

    fun addChild(childID: String) {
      this.children.add(treeNodes[childID]!!.item)
    }

    fun getChildren(): List<MediaItem> {
      return ImmutableList.copyOf(children)
    }
  }

  private fun buildMediaItem(
    title: String,
    mediaId: String,
    isPlayable: Boolean,
    isBrowsable: Boolean,
    mediaType: @MediaMetadata.MediaType Int,
    subtitleConfigurations: List<SubtitleConfiguration> = mutableListOf(),
    album: String? = null,
    artist: String? = null,
    genre: String? = null,
    sourceUri: Uri? = null,
    imageUri: Uri? = null
  ): MediaItem {
    val extras = Bundle()

    extras.putInt(
      MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
      MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM)
    extras.putInt(
      MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
      MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM)
    val metadata =
      MediaMetadata.Builder()
        .setAlbumTitle(album)
        .setTitle(title)
        .setArtist(artist)
        .setGenre(genre)
        .setIsBrowsable(isBrowsable)
        .setIsPlayable(isPlayable)
        .setArtworkUri(imageUri)
        .setMediaType(mediaType)
        .setExtras(extras)
        .build()

    return MediaItem.Builder()
      .setMediaId(mediaId)
      .setSubtitleConfigurations(subtitleConfigurations)
      .setMediaMetadata(metadata)
      .setUri(sourceUri)
      .build()
  }


  fun initialize() {
    if (isInitialized) return
    isInitialized = true
    // create root and folders for album/artist/genre.
    treeNodes[ROOT_ID] =
      MediaItemNode(
        buildMediaItem(
          title = "Root Folder",
          mediaId = ROOT_ID,
          isPlayable = false,
          isBrowsable = true,
          mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
        )
      )
  }

  private fun checkIfNewMasterPlaylistCanBeAdded(){
    treeNodes[ROOT_ID]!!.getChildren().size
  }

  private fun addMasterPlaylistToTree(masterPlaylistName: String){
    val masterPlaylistID = MASTER_PLAYLIST_ID + masterPlaylistName
    treeNodes[masterPlaylistID] =
      MediaItemNode(
        buildMediaItem(
          title = masterPlaylistName,
          mediaId = masterPlaylistID,
          isPlayable = false,
          isBrowsable = true,
          mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
        )
      )
    treeNodes[ROOT_ID]!!.addChild(masterPlaylistID)
  }

  private fun addMediaItemsToMasterPlaylist(masterPlaylistName:String, mediaItems: MutableList<MediaItem>):MutableList<String>  {
    if(!treeNodes.containsKey(masterPlaylistName)) {
      addMasterPlaylistToTree(masterPlaylistName)
    }
    val updatedIds: MutableList<String> = mutableListOf()
    val masterPlaylistId =  MASTER_PLAYLIST_ID + masterPlaylistName
    updatedIds.add(ROOT_ID)
    updatedIds.add(masterPlaylistId)
    mediaItems.forEach{item ->
      val idInTree = ITEM_PREFIX + item.mediaId
      val mediaItem = item.buildUpon().setMediaId(idInTree).build()
      val albumFolderIdInTree = PLAYLIST_PREFIX + mediaItem.mediaMetadata.albumTitle
      treeNodes[idInTree] =
        MediaItemNode(
          mediaItem
        )
      titleMap[mediaItem.mediaMetadata.title.toString().lowercase()] = treeNodes[idInTree]!!
      //Create album node if it doesn't exist
      if (!treeNodes.containsKey(albumFolderIdInTree)) {
        treeNodes[albumFolderIdInTree] =
          MediaItemNode(
            buildMediaItem(
              title = mediaItem.mediaMetadata.albumTitle.toString(),
              mediaId = albumFolderIdInTree,
              isPlayable = false,
              isBrowsable = true,
              mediaType = MediaMetadata.MEDIA_TYPE_ALBUM,
              imageUri=mediaItem.mediaMetadata.artworkUri,
            )
          )
        treeNodes[masterPlaylistId]!!.addChild(albumFolderIdInTree)
      }
      //add song to album
      treeNodes[albumFolderIdInTree]!!.addChild(idInTree)
      Log.d(TAG, treeNodes[albumFolderIdInTree]!!.item.mediaId)

      updatedIds.add(albumFolderIdInTree)
      updatedIds.add(idInTree)
    }
    return updatedIds
  }

  fun getItem(id: String): MediaItem? {
    return treeNodes[id]?.item
  }

  fun getRootItem(): MediaItem {
    return treeNodes[ROOT_ID]!!.item
  }

  fun getChildren(id: String): List<MediaItem>? {
    return treeNodes[id]?.getChildren()
  }

  fun getRandomItem(): MediaItem {
    var curRoot = getRootItem()
    while (curRoot.mediaMetadata.isBrowsable == true) {
      val children = getChildren(curRoot.mediaId)!!
      curRoot = children.random()
    }
    return curRoot
  }

  fun getItemFromTitle(title: String): MediaItem? {
    return titleMap[title]?.item
  }

  fun addMediaItems(masterPlaylistName: String, mediaItems: MutableList<MediaItem>):MutableList<String> {
    return addMediaItemsToMasterPlaylist(masterPlaylistName, mediaItems)
  }
}
