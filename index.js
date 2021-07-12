/**
 * @format
 */
import '@babel/polyfill';
import { AppRegistry, StatusBar } from 'react-native';
import App from './App';
import { name as appName } from './app.json';

StatusBar.setBackgroundColor('rgba(0,0,0,0.3)');
StatusBar.setTranslucent(true);
StatusBar.setHidden(false);

AppRegistry.registerComponent(appName, () => App);
