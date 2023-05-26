import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR =
  `The package 'react-native-car-audio' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const CarAudio = NativeModules.CarAudio
  ? NativeModules.CarAudio
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
export * from './BrowsableNode';
export * from './CarPlayer';
export * from './MediaItem';
export * from './TabNode';
export function multiply(a: number, b: number): Promise<number> {
  return CarAudio.multiply(a, b);
}
