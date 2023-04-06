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
      <Button
        title="Update"
        color="#777"
        onPress={() =>
          TrackPlayer.test({
            music: [
              {
                id: 'wake_up_01',
                title: 'Intro - The Way Of Waking Up (feat. Alan Watts)',
                album: 'Wake Up',
                artist: 'The Kyoto Connection',
                genre: 'Electronic',
                source:
                  'https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3',
                image:
                  'https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg',
                trackNumber: 1,
                totalTrackCount: 13,
                duration: 90,
                site: 'http://freemusicarchive.org/music/The_Kyoto_Connection/Wake_Up_1957/',
              },
            ],
          })
        }
      />
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
