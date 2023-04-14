package com.trackplayer.media.utils

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

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
    SOURCE_URI
}


fun buildMediaItemBundle(
    masterPlaylist: String,
    title: String,
    mediaId: String,
    isPlayable: Boolean,
    isBrowsable: Boolean,
    mediaType: @MediaMetadata.MediaType Int,
    album: String? = null,
    artist: String? = null,
    genre: String? = null,
    sourceUri: String? = null,
    imageUri: String? = null
): Bundle {
    val extras = Bundle()
    extras.putInt(
        MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
        MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM)
    extras.putInt(
        MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
        MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM)

    val mediaItemBundle = Bundle()
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name, title)
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_ID.name, mediaId)
    mediaItemBundle.putBoolean(MediaItemBundleKey.MEDIA_METADATA_IS_PLAYABLE.name, isPlayable)
    mediaItemBundle.putBoolean(MediaItemBundleKey.MEDIA_METADATA_IS_BROWSABLE.name, isBrowsable)
    mediaItemBundle.putInt(MediaItemBundleKey.MEDIA_METADATA_MEDIA_TYPE.name, mediaType)
    mediaItemBundle.putBundle(MediaItemBundleKey.MEDIA_METADATA_EXTRAS.name, extras)
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_ALBUM_TITLE.name, album)
    mediaItemBundle.putString(MediaItemBundleKey.SOURCE_URI.name, sourceUri)
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_ARTWORK_URI.name, imageUri)
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_ARTIST.name, artist)
    mediaItemBundle.putString(MediaItemBundleKey.MEDIA_METADATA_GENRE.name, genre)
    mediaItemBundle.putString(MEDIA_ITEM_TREE_ENUM.MASTER_PLAYLIST_ID.name, masterPlaylist)
    return mediaItemBundle
}

fun extractMediaItemFromBundle(bundle: Bundle): MediaItem {
    val title = bundle.getString(MediaItemBundleKey.MEDIA_METADATA_TITLE.name)!!
    val mediaId = bundle.getString(MediaItemBundleKey.MEDIA_ID.name)!!
    val isPlayable = bundle.getBoolean(MediaItemBundleKey.MEDIA_METADATA_IS_PLAYABLE.name)!!
    val isBrowsable = bundle.getBoolean(MediaItemBundleKey.MEDIA_METADATA_IS_BROWSABLE.name)!!
    val mediaType = bundle.getInt(MediaItemBundleKey.MEDIA_METADATA_MEDIA_TYPE.name)!!
    val extras = bundle.getBundle(MediaItemBundleKey.MEDIA_METADATA_EXTRAS.name)
    val album = bundle.getString(MediaItemBundleKey.MEDIA_METADATA_ALBUM_TITLE.name)
    val artist = bundle.getString(MediaItemBundleKey.MEDIA_METADATA_ARTIST.name)
    val genre = bundle.getString(MediaItemBundleKey.MEDIA_METADATA_GENRE.name)
    val sourceUri = bundle.getString(MediaItemBundleKey.SOURCE_URI.name)
    val imageUri = bundle.getString(MediaItemBundleKey.MEDIA_METADATA_ARTWORK_URI.name)

    return MediaItem.Builder()
        .setMediaId(mediaId)
        .setUri(sourceUri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setAlbumTitle(album)
                .setArtist(artist)
                .setGenre(genre)
                .setArtworkUri(Uri.parse(imageUri))
                .setMediaType(mediaType)
                .setIsPlayable(isPlayable)
                .setIsBrowsable(isBrowsable)
                .setExtras(extras)
                .build()
        )

        .build()
}



enum class CustomCommands {
    ADD_MEDIA_ITEMS,
    ADD_MEDIA_ITEM,
    SET_MEDIA_ITEMS,
    SET_MEDIA_ITEM,
}

enum class MEDIA_ITEM_TREE_ENUM {
    MASTER_PLAYLIST_ID
}