import { NativeModules } from 'react-native';
import uuid from 'react-native-uuid';
import { TabNode } from './TabNode';

class CarPlayer {
  private static CarPlayerInstance: CarPlayer;
  private TrackPlayer: any;
  // private browsableStyle: BrowserLayout = 'GRID';
  // private playableStyle: BrowserLayout = 'LIST';
  private tabs: Array<TabNode>;
  private uuidSet = new Set();

  constructor() {
    const { ReactNativeBridgeConnectorModule: TrackPlayer } = NativeModules;
    this.TrackPlayer = TrackPlayer;
    this.tabs = [];
  }

  public static getInstance(): CarPlayer {
    if (!CarPlayer.CarPlayerInstance) {
      CarPlayer.CarPlayerInstance = new CarPlayer();
    }
    return CarPlayer.CarPlayerInstance;
  }

  updateViewStyles() {
    // this.browsableStyle = browsableStyle ?? this.browsableStyle;
    // this.playableStyle = playableStyle ?? this.playableStyle;
    // this.TrackPlayer.setViewStyles(browsableStyle, playableStyle);
    this.loadPlayer();
  }

  loadPlayer = async () => {
    await this.TrackPlayer.resetPlayer();
    this.tabs.forEach(async (tab) => {
      await tab.loadData(this.TrackPlayer);
    });
  };

  addTab(tab: TabNode) {
    if (this.tabs.length < 4) {
      this.tabs.push(tab);
    } else {
      console.log('Max limit of tabs');
    }

    return this;
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

  clearTabs(): void {
    this.tabs = [];
  }
}

export const CarPlayerInstance = CarPlayer.getInstance();
