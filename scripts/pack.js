#! /usr/bin/env node
const process = require('process');
const fs = require('fs');
const path = require('path');
const shell = require('shelljs');
const program = require('commander');
program.version('0.0.1');

console.log('Path', path.resolve('./'));

program
  .option('--platform [type]', 'platform[android|ios]')
  .option('--debug', 'build a debug package', false)
  .parse();

const options = program.opts();
console.log('cli options', options);

// 初始化打包选项
const BasePath = path.resolve();
const PLATFORM = options.platform;
const DEBUG = options.debug;

// todo如果是远程打包项目，需要下载打包内容，然后解析项目配置，再合并进行打包
// 读取项目配置app.json
const appJson = require(BasePath + '/app.json');
const appConfig = appJson.config;

// 获取项目主包的name
const mainName = appJson.name;
const appName = appJson.displayName;

// 打包初始化
console.log('\n');
console.log('app platform: ', PLATFORM);
console.log('app config: ', mainName, appName);
console.log('\ncli build start...');
// 分平台打包
let supportPlatform = ['android', 'ios'];
if (!supportPlatform.includes(PLATFORM)) {
  console.error(`不支持此${PLATFORM}平台打包`);
}

// 打包配置处理
console.log('cli doing handleConfig...');
handleConfig(PLATFORM, mainName, appName, appConfig);
// 打包依赖处理
console.log('cli doing installDep...');
installDep(PLATFORM, mainName, appName, appConfig);
// 打包执行命令
console.log('cli doing execBuild...');
let isExec = execBuild(PLATFORM, mainName, appName, appConfig);
if (isExec) {
  // todo进行后续处理，上传，推送通知
}
// 结束退出
console.log('cli build end.\n');
process.exit(0);

// 进行配置处理
function handleConfig(platform, mainName, appName, appConfig) {
  switch (platform) {
    case 'android':
      _handleAndroid(mainName, appName, appConfig);
      break;
    case 'ios':
      _handleIos(mainName, appName, appConfig);
      break;
    default:
      return;
  }
}
// 进行依赖处理
function installDep(platform, mainName, appName, appConfig) {
  let packageJson = require(BasePath + '/package.json');
  let sDep = (packageJson && packageJson.dependencies) || {};
  let nDep = (appConfig && appConfig.dependencies) || {};
  let upDep = {};
  let upDepStr = '';
  if (nDep) {
    //判断是否需要有安装的依赖
    for (const key in nDep) {
      if (Object.hasOwnProperty.call(nDep, key)) {
        let isH = false;
        const element = nDep[key];
        if (element) {
          // 进行原依赖对比
          for (const key1 in sDep) {
            if (Object.hasOwnProperty.call(sDep, key1)) {
              const element1 = sDep[key1];
              if (key1 == key) {
                isH = true;
                if (element != '*') {
                  upDep[key] = element;
                }
              }
            }
          }
          if (!isH) {
            upDep[key] = element;
          }
        }
      }
    }
  }
  // 进行依赖安装
  for (const key in upDep) {
    if (Object.hasOwnProperty.call(upDep, key)) {
      const element = upDep[key];
      upDepStr += '  ' + `${key}@${element}`;
    }
  }
  // 进行安装
  if (upDepStr) {
    let cmdStr = 'yarn add ' + upDepStr;
    console.log('shell.exec ', cmdStr);
    sExec = shell.exec(cmdStr);
    if (sExec.code !== 0) {
      shell.echo('shell.exec [yarn add] Error: failed', sExec); // shell.exit(1);
      return false;
    }
    return true;
  }
  return true;
}
// 进行打包处理
function execBuild(platform, mainName, appName, appConfig) {
  const { spawn } = require('child_process');
  let sExec = null;
  switch (platform) {
    case 'android':
      sExec = shell.exec(
        DEBUG ? 'yarn build-android-debug' : 'yarn build-android'
      );
      if (sExec.code !== 0) {
        shell.echo('shell.exec [yarn build] Error: failed', sExec); // shell.exit(1);
        return false;
      }
      return true;
      break;
    case 'ios':
      sExec = shell.exec(DEBUG ? 'yarn build-ios-debug' : 'yarn build-ios');
      if (sExec.code !== 0) {
        shell.echo('shell.exec [yarn build] Error: failed', sExec); // shell.exit(1);
        return false;
      }
      return true;
      break;
    default:
      return false;
  }
}

function _handleAndroid(mainName, appName, appConfig) {
  // 先替换主包的名称
  const mainPath =
    BasePath + '/android/app/src/main/java/com/rnshell/app/MainActivity.java';
  fs.readFile(mainPath, 'utf8', function (err, data) {
    if (err) throw err;
    // 匹配查找return "rnshell"
    let newData = data.replace(
      /return\s*"rnshell"/g,
      `return "${mainName}"`
    );
    if (!newData) {
      newData = data;
    }
    fs.writeFile(mainPath, newData, 'utf8', (err) => {
      if (err) throw err;
      console.log('edit MainActivity success done');
    });
  });

  // 复制资源到android目录
  const sPath = BasePath + '/app/android/*';
  const tPath = BasePath + '/android/';
  shell.cp('-Rf', sPath, tPath);
}

function _handleIos(mainName, appName, appConfig) {
  // 替换主包的名称
  const mainPath = BasePath + '/ios/rnshell/AppDelegate.m';
  fs.readFile(mainPath, 'utf8', function (err, data) {
    if (err) throw err;
    // 匹配查找moduleName:@"rnshell"
    let newData = data.replace(
      /moduleName:\s*@"rnshell"/g,
      `moduleName:@"${mainName}"`
    );
    if (!newData) {
      newData = data;
    }
    fs.writeFile(mainPath, newData, 'utf8', (err) => {
      if (err) throw err;
      console.log('edit AppDelegate success done');
    });
  });

  // 复制资源到ios目录
  const sPath = BasePath + '/app/ios/*';
  const tPath = BasePath + '/ios/';
  shell.cp('-Rf', sPath, tPath);
}
