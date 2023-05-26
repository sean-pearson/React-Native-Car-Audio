import { CarPlayerInstance } from './CarPlayer';

export interface MediaMetadataType {
  title: String;
  album?: String;
  artist?: String;
  genre?: String;
  imageUri?: String;
  mimeType?: String;
}

export interface MediaItemType {
  uuid?: String;
  uri?: String;
  mediaMetadata: MediaMetadataType;
}

export class MediaItem {
  private loadRequired: boolean;
  private _: MediaItemType;
  constructor(data: MediaItemType) {
    this.loadRequired = true;
    this._ = data;
    if (!this._.uuid) {
      this._.uuid = CarPlayerInstance.requestUUID();
    }
  }

  get mediaItem() {
    return this._;
  }

  get uuid() {
    return this._.uuid || null;
  }

  get uri() {
    return this._.uri || null;
  }

  get mediaMetadata() {
    return this._.mediaMetadata;
  }

  get title() {
    return this._.mediaMetadata.title;
  }

  get album() {
    return this._.mediaMetadata.album || null;
  }

  get artist() {
    return this._.mediaMetadata.artist || null;
  }

  get genre() {
    return this._.mediaMetadata.genre || null;
  }

  get imageUri() {
    return this._.mediaMetadata.imageUri || null;
  }

  get mimeType() {
    return this._.mediaMetadata.mimeType || null;
  }

  get extractData() {
    return this._;
  }

  async loadData(ParentId: String, TrackPlayer: any) {
    if (this.loadRequired) {
      this.loadRequired = false;
      await TrackPlayer.loadMediaItem(this.uuid, ParentId, this._);
    }
  }
}
