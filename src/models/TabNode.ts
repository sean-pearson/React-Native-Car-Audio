import { Player } from "./AAPlayer";
import { BrowsableNode } from "./BrowsableNode";
import { MediaItem } from "./MediaItem";

export class TabNode {
  private title: String;
  private tabNodeId: String;
  private children: Array<BrowsableNode | MediaItem>;
  private type: BrowsableNode | MediaItem | null = null;
  private loadRequired: boolean;
  constructor(title: String, uuid?: String) {
    this.loadRequired = true;
    this.title = title;
    this.children = [];
    if (uuid) {
      this.tabNodeId = uuid;
    } else {
      this.tabNodeId = Player.requestUUID();
    }
  }
  addChild(child: BrowsableNode | MediaItem, uuid?: String) {
    if (!this.type) {
      this.type = child;
    }
    if (typeof child == typeof this.type) {
      this.children.push(child);
    } else {
      throw "Can't mix MediaItems with Browsable Nodes";
    }
  }
  get uuid() {
    return this.tabNodeId;
  }

  async loadData(TrackPlayer: any) {
    if (this.loadRequired) {
      this.loadRequired = false;
      await TrackPlayer.loadTab(this.uuid, this.title);
    }
    this.children.forEach((child: BrowsableNode | MediaItem) => {
      child.loadData(this.uuid, TrackPlayer);
    });
  }
}
