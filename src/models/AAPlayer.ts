import { NativeModules } from "react-native";
import uuid from "react-native-uuid";
import { TabNode } from "./TabNode";
class AAPlayer {
  private static AAPlayerInstance: AAPlayer;
  private TrackPlayer: any;
  private browsableStyle: BrowserLayout = "GRID";
  private playableStyle: BrowserLayout = "LIST";
  private tabs: Array<TabNode>;
  private uuidSet = new Set();

  constructor() {
    const { ReactNativeBridgeConnectorModule: TrackPlayer } = NativeModules;
    this.TrackPlayer = TrackPlayer;
    this.tabs = [];
  }

  public static getInstance(): AAPlayer {
    if (!AAPlayer.AAPlayerInstance) {
      AAPlayer.AAPlayerInstance = new AAPlayer();
    }
    return AAPlayer.AAPlayerInstance;
  }

  updateViewStyles(
    browsableStyle?: BrowserLayout,
    playableStyle?: BrowserLayout
  ) {
    this.browsableStyle = browsableStyle ?? this.browsableStyle;
    this.playableStyle = playableStyle ?? this.playableStyle;
    this.TrackPlayer.setViewStyles(browsableStyle, playableStyle);
    this.loadPlayer();
  }

  async loadPlayer() {
    console.log("loadPlayer");
    await this.TrackPlayer.resetPlayer();
    console.log("resetPlayer Done");
    this.tabs.forEach((tab) => {
      tab.loadData(this.TrackPlayer);
    });
  }

  addTab(tab: TabNode) {
    if (this.tabs.length < 4) {
      this.tabs.push(tab);
    } else {
      console.log("Max limit of tabs");
    }
  }

  skipToPrevious(): void {
    this.TrackPlayer.skipToPrevious();
  }

  skipToNext(): void {
    this.TrackPlayer.skipToNext();
  }

  play(): void {
    this.TrackPlayer.play();
  }

  pause(): void {
    this.TrackPlayer.pause();
  }

  requestUUID(): String {
    let id: String = uuid.v4().toString();
    while (this.uuidSet.has(id)) {
      id = uuid.v4().toString();
    }
    this.uuidSet.add(id);
    return id;
  }
}

export const Player = AAPlayer.getInstance();
