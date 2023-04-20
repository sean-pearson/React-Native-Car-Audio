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

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.google.common.collect.ImmutableList
import com.trackplayer.media.utils.MediaItemBundleKey

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

@UnstableApi object MediaItemTree {
  private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
  private var titleMap: MutableMap<String, MediaItemNode> = mutableMapOf()
  private var isInitialized = false
  const val ROOT_ID = "[rootID]"
  private const val TAB = "[TAB]"
  private const val NODE = "[NODE]"
  private const val ITEM = "[ITEM]"
  private var browsableStyle = MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM
  private var playableStyle = MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM


  private class MediaItemNode(val item: MediaItem) {
    private val children: MutableList<MediaItem> = ArrayList()
    private val childrenNodes: MutableList<MediaItemNode> = ArrayList()
    private var nextSiblingNode: MediaItemNode? = null;
    private var priorSiblingNode: MediaItemNode? = null;
    private var parentNode: MediaItemNode? = null;

    fun addChild(childID: String) {
      val childNode = treeNodes[childID]
      if(childNode != null) {
        this.children.add(childNode.item)
        childrenNodes.add(childNode)
        childNode.parentNode = this;
        if (childrenNodes.size > 0) {
         childNode.priorSiblingNode = childrenNodes.last()
          childNode.nextSiblingNode = treeNodes[childID]!!
        }
      } else {
        Log.e(TAG, "Attempted to add null child.")
      }
    }

    fun getChildren(): List<MediaItem> {
      return ImmutableList.copyOf(children)
    }
    fun getParentNode(): MediaItemNode? {
      return this.parentNode
    }

    fun getSiblingNodes():ArrayList<MediaItemNode>{
      val siblings: ArrayList<MediaItemNode> = ArrayList()
      var current: MediaItemNode = this
      while (current.priorSiblingNode != null){
        current = current.priorSiblingNode!!;
      }
      while (current.nextSiblingNode != null){
        siblings.add(current)
        current = current.nextSiblingNode!!;
      }
      siblings.add(current)
      return siblings
    }

    fun getSibling():MutableList<MediaItem>{
      val siblingNodes = this.getSiblingNodes()
      return siblingNodes.map{node ->
        node.item
      }.toMutableList()
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
      browsableStyle)
    extras.putInt(
      MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
      playableStyle)
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





  fun setViewStyles(playableStyle: Int, browsableStyle: Int){
    this.browsableStyle =  browsableStyle
    this.playableStyle = playableStyle
  }

  private fun getNodesToUpdate(uuid: String): MutableList<String>{
    var node = treeNodes[uuid]
    val invertedListOfNodes: MutableList<String> = mutableListOf()
    while(node?.getParentNode()!= null){
      invertedListOfNodes.add(node.item.mediaId)
      node = node.getParentNode()
    }
    return invertedListOfNodes.asReversed()
  }



  fun addTab(args: Bundle) {
    val title: String = args.getString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name)!!
    val uuid: String = args.getString(MediaItemBundleKey.MEDIA_ID.name)!!
    Log.d(TAG, "addTab: $title")
    treeNodes[uuid] =
      MediaItemNode(
        buildMediaItem(
          title = title,
          mediaId = uuid,
          isPlayable = false,
          isBrowsable = true,
          mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
        )
      )
    treeNodes[ROOT_ID]!!.addChild(uuid)

  }

  fun addNode(args: Bundle): MutableList<String> {

    val uuid: String = args.getString(MediaItemBundleKey.MEDIA_ID.name)!!
    val parentId: String = args.getString(MediaItemBundleKey.PARENT_ID.name)!!
    val title: String = args.getString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name)!!
    val imageUri: Uri = Uri.parse(args.getString(MediaItemBundleKey.MEDIA_METADATA_ARTWORK_URI.name)!!)
    Log.d(TAG, "addNode: $title $parentId")
    treeNodes[uuid] =
      MediaItemNode(
        buildMediaItem(
          title = title,
          mediaId = uuid,
          isPlayable = false,
          isBrowsable = true,
          mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
          imageUri = imageUri
        )
      )
    treeNodes[parentId]!!.addChild(uuid)
    return getNodesToUpdate(uuid)
  }

  fun addMediaItem(args:Bundle) : MutableList<String> {
    try {
      val title = args.getString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name)!!
      val mediaType = args.getInt(MediaItemBundleKey.MEDIA_METADATA_MEDIA_TYPE.name)!!
      val album = args.getString(MediaItemBundleKey.MEDIA_METADATA_ALBUM_TITLE.name)
      val artist = args.getString(MediaItemBundleKey.MEDIA_METADATA_ARTIST.name)
      val genre = args.getString(MediaItemBundleKey.MEDIA_METADATA_GENRE.name)
      val sourceString = args.getString(MediaItemBundleKey.SOURCE_URI.name)
      val sourceUri: Uri = Uri.parse(sourceString)
      val imageString = args.getString(MediaItemBundleKey.MEDIA_METADATA_ARTWORK_URI.name)
      val imageUri = if (imageString!=null) Uri.parse(imageString) else null
      val uuid: String = args.getString(MediaItemBundleKey.MEDIA_ID.name)!!
      val parentId: String = args.getString(MediaItemBundleKey.PARENT_ID.name)!!
      Log.d(TAG, "addMediaItem: $title $parentId")
      treeNodes[uuid] =
        MediaItemNode(
          buildMediaItem(
            title = title,
            mediaId = uuid,
            isPlayable = true,
            isBrowsable = false,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
            imageUri = imageUri,
            sourceUri = sourceUri,
            album = album,
            artist = artist,
            genre = genre,
          )
        )
      treeNodes[parentId]!!.addChild(uuid)
      return getNodesToUpdate(uuid)
    } catch (e: Exception){
      Log.e(TAG, e.toString())
      return mutableListOf()
    }
  }

  fun resetRoot() {
    treeNodes = mutableMapOf()
    titleMap = mutableMapOf()
    isInitialized = false
    this.initialize()
  }
}
