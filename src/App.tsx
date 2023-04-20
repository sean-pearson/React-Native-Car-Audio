import { useLayoutEffect, useState } from "react";
import { Button, SafeAreaView, StyleSheet, Text } from "react-native";

import React from "react";
import { Player } from "./models/AAPlayer";
import { BrowsableNode } from "./models/BrowsableNode";
import { MediaItem } from "./models/MediaItem";
import { TabNode } from "./models/TabNode";

function App() {
  const [nowPlaying, setNowPlaying] = useState<{
    album?: string;
    artist?: string;
    duration?: number;
    title?: string;
  }>({});
  const TrackPlayer = Player;
  const initPlayer = async () => {
    const tab1 = new TabNode("Test 1");
    const media1 = new MediaItem({
      uri: "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3",
      mediaMetadata: { title: "Wake Up" },
    });
    TrackPlayer.addTab(tab1);
    const tab2 = new TabNode("Test 2");
    const node = new BrowsableNode(
      "Album",
      "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"
    );
    node.addChild(media1);
    tab1.addChild(node);
    // tab2.addChild(node);
    TrackPlayer.addTab(tab2);
    TrackPlayer.addTab(new TabNode("Test 3"));
    TrackPlayer.addTab(new TabNode("Test 4"));
    TrackPlayer.addTab(new TabNode("Test 5"));
    await TrackPlayer.loadPlayer();
  };
  useLayoutEffect(() => {
    initPlayer();
  }, []);
  return (
    <SafeAreaView style={styles.container}>
      <Text style={{ color: "white", textAlign: "center" }}>
        {nowPlaying.title}
      </Text>
      <Text style={{ color: "white", textAlign: "center" }}>
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
    justifyContent: "center",
    padding: 20,
    backgroundColor: "#112",
  },
});

export default App;
