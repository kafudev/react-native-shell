/**
 * @format
 */
import '@babel/polyfill';
import 'react-native-gesture-handler';
import { AppRegistry, NativeModules } from 'react-native';
import App from './App';
import { name as appName } from './app.json';

// setTimeout(() => {
//   NativeModules?.RNBootSplash?.hide(); // 隐藏启动屏
//   console.log('index.js: RNBootSplash?.hide');
// }, 1500);

AppRegistry.registerComponent(appName, () => App);
