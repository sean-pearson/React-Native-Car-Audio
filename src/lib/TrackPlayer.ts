import {NativeModules} from 'react-native';

const {ReactNativeBridgeConnectorModule: TrackPlayer} = NativeModules;

async function test(obj: {
  music: Array<{
    id: string;
    title: string;
    album: string;
    artist: string;
    genre: string;
    source: string;
    image: string;
    trackNumber: number;
    totalTrackCount: number;
    duration: number;
    site: string;
  }>;
}) {
  return TrackPlayer.test(obj);
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

export default {
  add,
  test,
  pause,
  play,
  togglePlay,
  skipToNext,
  skipToPrevious,
  getNowPlaying,
};
