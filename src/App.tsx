import React, {useEffect, useState} from 'react';
import {
  Button,
  NativeEventEmitter,
  NativeModules,
  SafeAreaView,
  StyleSheet,
  Text,
} from 'react-native';
import TrackPlayer from './lib/TrackPlayer';

function App() {
  const [nowPlaying, setNowPlaying] = useState<{
    album?: string;
    artist?: string;
    duration?: number;
    title?: string;
  }>({});
  useEffect(() => {
    const myModuleEvt = new NativeEventEmitter(NativeModules.TrackPlayerModule);
    myModuleEvt.addListener('NOW_PLAYING', data => setNowPlaying(data));
    // if (!nowPlaying.album) {
    //   setNowPlaying(await TrackPlayer.getNowPlaying());
    // }
  });
  return (
    <SafeAreaView style={styles.container}>
      <Text style={{color: 'white', textAlign: 'center'}}>
        {nowPlaying.title}
      </Text>
      <Text style={{color: 'white', textAlign: 'center'}}>
        {nowPlaying.artist}
      </Text>
      <Button title="Add" color="#777" onPress={() => TrackPlayer.add()} />
      <Button title="Pause" color="#777" onPress={() => TrackPlayer.pause()} />
      <Button title="Play" color="#777" onPress={() => TrackPlayer.play()} />
      <Button
        title="Skip To Next"
        color="#777"
        onPress={() => TrackPlayer.skipToNext()}
      />
      <Button
        title="Skip To Previous"
        color="#777"
        onPress={() => TrackPlayer.skipToPrevious()}
      />
      <Button
        title="toggle play"
        color="#777"
        onPress={() => TrackPlayer.togglePlay()}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 20,
    backgroundColor: '#112',
  },
});

export default App;
