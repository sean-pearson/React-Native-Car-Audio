import { MediaItemNodeType } from "../models/MediaItemNode";

export const catalog: MediaItemNodeType = {
  parentNode: null,
  childrenNodes: [],
  item: {
    mediaMetadata: {
      title: "Listen Live",
    },
  },

  // playerData: [
  //   {
  //     title: "Listen Live",
  //     playlist: [
  //       {
  //         title: "Wake Up",
  //         imageUri:
  //           "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg",
  //         playlist: [
  //           {
  //             uuid: "wake_up_01",
  //             uri: "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3",
  //             mediaMetadata: {
  //               title: "Intro - The Way Of Waking Up (feat. Alan Watts)",
  //               album: "Wake Up",
  //               artist: "The Kyoto Connection",
  //               genre: "Electronic",
  //               mimeType: "mp3",
  //               imageUri:
  //                 "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg",
  //             },
  //           },
  //         ],
  //       },
  //     ],
  //   },
  // ],
};
