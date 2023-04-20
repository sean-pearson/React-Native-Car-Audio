package com.trackplayer.media.utils

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

fun buildMediaItem(
    title: String,
    mediaId: String,
    isPlayable: Boolean,
    isBrowsable: Boolean,
    mediaType: @MediaMetadata.MediaType Int,
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
        .setMediaMetadata(metadata)
        .setUri(sourceUri)
        .build()
}

enum class MediaItemBundleKey {
    MEDIA_METADATA_EXTRAS,
    MEDIA_METADATA_ALBUM_TITLE,
    MEDIA_METADATA_ALBUM_ARTIST,
    MEDIA_METADATA_TITLE,
    MEDIA_METADATA_ARTIST,
    MEDIA_METADATA_GENRE,
    MEDIA_METADATA_IS_BROWSABLE,
    MEDIA_METADATA_IS_PLAYABLE,
    MEDIA_METADATA_ARTWORK_URI,
    MEDIA_METADATA_MEDIA_TYPE,
    MEDIA_ID,
    SUBTITLE_CONFIGURATIONS,
    SOURCE_URI,
    PARENT_ID
}

fun buildMediaItemBundle(
    mediaItem: ReadableMap
): Bundle {
    val mediaItemBundle = Bundle()
    val mediaMetadata = mediaItem.getMap("mediaMetadata")!!
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_ID.name, mediaItem.getString("uuid"))
    mediaItemBundle.putString(MediaItemBundleKey.SOURCE_URI.name, mediaItem.getString("uri"))
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name, mediaMetadata.getString("title"))
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_ALBUM_TITLE.name, mediaMetadata.getString("album"))
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_ARTIST.name, mediaMetadata.getString("artist"))
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_GENRE.name, mediaMetadata.getString("genre"))
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_ARTWORK_URI.name, mediaMetadata.getString("imageUri"))

    Log.d("UTILS", mediaItem.getString("uuid")?: "")
    Log.d("UTILS",  mediaItem.getString("uri")?: "")
    Log.d("UTILS",  mediaMetadata.getString("title")?: "")
    Log.d("UTILS",  mediaMetadata.getString("album")?: "")
    Log.d("UTILS",  mediaMetadata.getString("artist")?: "")
    Log.d("UTILS",  mediaMetadata.getString("genre")?: "")
    Log.d("UTILS", mediaMetadata.getString("imageUri")?: "")
    return mediaItemBundle
}




enum class CustomCommands {
    ADD_MEDIA_ITEMS,
    ADD_MEDIA_ITEM,
    SET_MEDIA_ITEMS,
    SET_MEDIA_ITEM,
    SET_VIEW_STYLES,
    ADD_TAB,
    ADD_NODE,
    RESET_PLAYER
}
enum class SetViewStyleCustomCommand {
    PLAYABLE_VIEW_STYLE,
    BROWSABLE_VIEW_STYLE
}

enum class MEDIA_ITEM_TREE_ENUM {
    MASTER_PLAYLIST_ID
}