import { Player } from "./AAPlayer";
import { MediaItem } from "./MediaItem";

export class BrowsableNode {
  private title: String;
  private browsableNodeId: String;
  private imageUri: String;
  private children: Array<BrowsableNode | MediaItem>;
  private type: BrowsableNode | MediaItem | null = null;

  constructor(title: String, imageUri: String, uuid?: String) {
    this.title = title;
    this.imageUri = imageUri;
    this.children = [];
    if (uuid) {
      this.browsableNodeId = uuid;
    } else {
      this.browsableNodeId = Player.requestUUID();
    }
  }

  addChild(child: BrowsableNode | MediaItem) {
    if (!this.type) {
      this.type = child;
      this.children.push(child);
    } else {
      if (typeof child == typeof this.type) {
        this.children.push(child);
      } else {
        throw "Can't mix MediaItems with Browsable Nodes";
      }
    }
  }

  get uuid() {
    return this.browsableNodeId;
  }

  async loadData(ParentId: String, TrackPlayer: any) {
    await TrackPlayer.loadNode(this.uuid, ParentId, this.title, this.imageUri);
    console.log("loadNode" + this.title + this.children.length);
    this.children.forEach((child: BrowsableNode | MediaItem) => {
      console.log("node child: " + child.uuid);
      child.loadData(this.uuid, TrackPlayer);
    });
  }
}
