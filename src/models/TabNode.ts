import { Player } from "./AAPlayer";
import { BrowsableNode } from "./BrowsableNode";
import { MediaItem } from "./MediaItem";

export class TabNode {
  private title: String;
  private tabNodeId: String;
  private children: Array<BrowsableNode | MediaItem>;
  private type: BrowsableNode | MediaItem | null = null;
  constructor(title: String, uuid?: String) {
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
      console.log("if");
      this.type = child;
      this.children.push(child);
    } else {
      console.log("else");
      if (typeof child == typeof this.type) {
        console.log("else -> if");
        this.children.push();
      } else {
        console.log("else -> else");
        throw "Can't mix MediaItems with Browsable Nodes";
      }
    }
    console.log("Adding Child", this.children.length);
  }
  get uuid() {
    return this.tabNodeId;
  }

  async loadData(TrackPlayer: any) {
    await TrackPlayer.loadTab(this.uuid, this.title);
    console.log("loadTab: " + this.title + this.children.length);
    this.children.forEach((child: BrowsableNode | MediaItem) => {
      console.log("tab child: " + child.uuid);
      child.loadData(this.uuid, TrackPlayer);
    });
  }
}
