import { useLayoutEffect, useState } from 'react';
import { Button, SafeAreaView, StyleSheet, Text } from 'react-native';

import React from 'react';
import {
  BrowsableNode,
  CarPlayerInstance,
  MediaItem,
  TabNode,
} from 'react-native-car-audio';

function App() {
  const [nowPlaying, setNowPlaying] = useState<{
    album?: string;
    artist?: string;
    duration?: number;
    title?: string;
  }>({});
  const TrackPlayer = CarPlayerInstance;
  const initPlayer = async () => {
    const tab1 = new TabNode('Live Radio');
    tab1.addChild(
      new MediaItem({
        uri: 'https://kcrw1.streamguys1.com/kcrw_64k_aac_on_air/playlist.m3u8',
        mediaMetadata: {
          title: 'On Air',
        },
      })
    );
    tab1.addChild(
      new MediaItem({
        uri: 'https://kcrw1.streamguys1.com/kcrw_64k_aac_e24/playlist.m3u8',
        mediaMetadata: {
          title: 'Eclectic 24',
        },
      })
    );

    TrackPlayer.addTab(tab1);

    const tab2 = new TabNode('Album');

    const node = new BrowsableNode(
      'Wake Up',
      'https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg'
    );
    const media1 = new MediaItem({
      uri: 'https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3',
      mediaMetadata: {
        title: 'Intro - The Way Of Waking Up (feat. Alan Watts)',
      },
    });
    const media2 = new MediaItem({
      uri: 'https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/06_-_No_Pain_No_Gain.mp3',
      mediaMetadata: {
        title: 'No Pain, No Gain',
        imageUri:
          'https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg',
      },
    });
    node.addChild(media1);
    node.addChild(media2);
    tab2.addChild(node);
    TrackPlayer.addTab(tab2);
    await TrackPlayer.loadPlayer();
  };
  useLayoutEffect(() => {
    initPlayer();
  }, []);
  return (
    <SafeAreaView style={styles.container}>
      <Text style={{ color: 'white', textAlign: 'center' }}>
        {nowPlaying.title}
      </Text>
      <Text style={{ color: 'white', textAlign: 'center' }}>
        {nowPlaying.artist}
      </Text>
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
      {/* <Button
        title="toggle play"
        color="#777"
        onPress={() => TrackPlayer.togglePlay()}
      /> */}
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
