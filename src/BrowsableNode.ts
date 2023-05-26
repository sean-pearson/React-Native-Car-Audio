import { CarPlayerInstance } from './CarPlayer';
import { MediaItem } from './MediaItem';

export class BrowsableNode {
  private title: String;
  private browsableNodeId: String;
  private imageUri: String;
  private children: Array<BrowsableNode | MediaItem>;
  private type: BrowsableNode | MediaItem | null = null;
  private loadRequired: boolean;

  constructor(title: String, imageUri: String, uuid?: String) {
    this.title = title;
    this.imageUri = imageUri;
    this.loadRequired = true;
    this.children = [];
    if (uuid) {
      this.browsableNodeId = uuid;
    } else {
      this.browsableNodeId = CarPlayerInstance.requestUUID();
    }
  }

  addChild(child: BrowsableNode | MediaItem) {
    if (!this.type) {
      this.type = child;
      this.children.push(child);
    } else {
      if (typeof child === typeof this.type) {
        this.children.push(child);
      } else {
        throw "Can't mix MediaItems with Browsable Nodes";
      }
    }
    return this;
  }

  get uuid() {
    return this.browsableNodeId;
  }

  async loadData(ParentId: String, TrackPlayer: any) {
    if (this.loadRequired) {
      this.loadRequired = false;
      await TrackPlayer.loadNode(
        this.uuid,
        ParentId,
        this.title,
        this.imageUri
      );
    }
    this.children.forEach(async (child: BrowsableNode | MediaItem) => {
      await child.loadData(this.uuid, TrackPlayer);
    });
  }
}
