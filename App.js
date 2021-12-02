/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import type { Node } from 'react';
import { useBackHandler } from '@react-native-community/hooks';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  BackHandler,
  Button,
  NativeModules,
  TextInput,
  View,
} from 'react-native';

import {
  Colors,
  DebugInstructions,
  Header,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

StatusBar.setBackgroundColor('rgba(0,0,0,0.3)');
StatusBar.setTranslucent(true);
StatusBar.setHidden(false);

const Section = ({ children, title }): Node => {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <View style={styles.sectionContainer}>
      <Text
        style={[
          styles.sectionTitle,
          {
            color: isDarkMode ? Colors.white : Colors.black,
          },
        ]}
      >
        {title}
      </Text>
      <Text
        style={[
          styles.sectionDescription,
          {
            color: isDarkMode ? Colors.light : Colors.dark,
          },
        ]}
      >
        {children}
      </Text>
    </View>
  );
};

let bfirstTime = Date.parse(new Date());
const App = (props) => {
  const isDarkMode = useColorScheme() === 'dark';

  let [bundleName, setBundleName] = React.useState('rnshell');
  let [bundleUrl, setBundleUrl] = React.useState(
    'http://192.168.110.20:8081/index.bundle?platform=android&minify=true'
  );

  setTimeout(() => {
    NativeModules.RNBootSplash.hide(true); // 隐藏启动屏
  }, 1500);

  console.log('App props', props);

  // android双击退出
  useBackHandler(() => {
    console.log('BackHandler onBackKeyDown');
    let nt = Date.parse(new Date());
    let xt = nt - bfirstTime;
    if (xt > 1.2 * 1000) {
      bfirstTime = Date.parse(new Date());
      console.log('BackHandler 再次点击退出');
      return true;
    } else {
      console.log('BackHandler 退出APP');
      BackHandler.exitApp();
    }
    // let the default thing happen
    return false;
  });

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
    paddingTop: 10,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      {/* <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} /> */}
      <StatusBar />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}
      >
        <Header />
        <Button
          title="显示toast"
          onPress={() => {
            NativeModules.Common &&
              NativeModules.Common.toast('显示toast内容', 0);
          }}
        />
        <TextInput
          defaultValue={bundleName}
          style={{ backgroundColor: '#fff' }}
          placeholder="请输入bundle名称"
          onChangeText={(v) => {
            setBundleName(v);
          }}
        />
        <TextInput
          defaultValue={bundleUrl}
          style={{ backgroundColor: '#fff' }}
          placeholder="请输入bundle地址"
          onChangeText={(v) => {
            setBundleUrl(v);
          }}
        />
        <Button
          title="打开远程bundle页面"
          onPress={() => {
            NativeModules.Common &&
              NativeModules.Common.openPageActivity({
                style: 1,
                isReload: false,
                bundleUrl: bundleUrl,
                moduleName: bundleName,
                moduleVersion: '1.0.0',
                appName: bundleName.toLocaleUpperCase(),
                appLogo: '',
                extraData: {
                  cc: 1,
                  xxx: 'xxxx',
                  abc: {
                    ccc: 'xx',
                  },
                },
              });
          }}
        />
        <Button
          title="重新加载远程bundle页面"
          onPress={() => {
            NativeModules.Common &&
              NativeModules.Common.openPageActivity({
                style: 1,
                isReload: true,
                bundleUrl: bundleUrl,
                moduleName: bundleName,
                moduleVersion: '1.0.0',
                appName: bundleName.toLocaleUpperCase(),
                appLogo: '',
                extraData: {
                  cc: 1,
                  xxx: 'xxxx',
                  abc: {
                    ccc: 'xx',
                  },
                },
              });
          }}
        />
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}
        >
          <Section title="Step One">
            Edit <Text style={styles.highlight}>App.js</Text> to change this
            screen and then come back to see your edits.
          </Section>
          <Section title="See Your Changes">
            <ReloadInstructions />
          </Section>
          <Section title="Debug">
            <DebugInstructions />
          </Section>
          <Section title="Learn More">
            Read the docs to discover what to do next:
          </Section>
          <LearnMoreLinks />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
