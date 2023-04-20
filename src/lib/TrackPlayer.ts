import { NativeModules } from "react-native";
import { MediaItemNode } from "../models/MediaItemNode";
import { catalog } from "./catalog";

const { ReactNativeBridgeConnectorModule: TrackPlayer } = NativeModules;
async function setupPlayer(
  browsableStyle: BrowserLayout,
  playableStyle: BrowserLayout
) {
  TrackPlayer.setupPlayer(browsableStyle, playableStyle);
  loadPlayer(new MediaItemNode(catalog));
}

async function loadPlayer(root: MediaItemNode) {
  TrackPlayer.loadPlayer(root);
}

async function add() {
  return TrackPlayer.add();
}
async function play() {
  return TrackPlayer.play();
}
async function pause() {
  return TrackPlayer.pause();
}
async function togglePlay() {
  return TrackPlayer.togglePlay();
}
async function skipToNext() {
  return TrackPlayer.skipToNext();
}
async function skipToPrevious() {
  return TrackPlayer.skipToPrevious();
}
async function getNowPlaying() {
  return TrackPlayer.getNowPlaying();
}
