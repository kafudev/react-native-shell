#! /usr/bin/env node
const process = require('process');
const dotenv = require('dotenv');
const os = require('os');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
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
const PLATFORM = options.platform;
const DEBUG = options.debug;
let BasePath = path.resolve();

// todo如果是远程打包项目，需要下载打包内容，然后解析项目配置，再合并进行打包
// 读取env配置文件
let envConfig = {};
try {
  envConfig = dotenv.parse(fs.readFileSync(BasePath + '/.env'));
} catch (error) {
  console.warn('read .env error', error.message);
}

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

try {
  // 打包配置处理
  console.log('cli doing handleConfig...');
  handleConfig(PLATFORM, mainName, appName, appConfig, envConfig);
  console.log('cli doing handleConfig end.');
  // 读取env新配置文件
  envConfig = dotenv.parse(fs.readFileSync(BasePath + '/.env'));
  // 打包依赖处理
  console.log('cli doing installDep...');
  installDep(PLATFORM, mainName, appName, appConfig, envConfig);
  console.log('cli doing installDep end.');
  // 打包模块处理
  console.log('cli doing checkModule...');
  checkModule(PLATFORM, mainName, appName, appConfig, envConfig);
  console.log('cli doing checkModule end.');
  // 打包执行命令
  console.log('cli doing execBuild...');
  let isSuccess = execBuild(PLATFORM, mainName, appName, appConfig, envConfig);
  console.log('cli doing execBuild end.', isSuccess);
  if (isSuccess === true) {
    // todo进行后续处理，上传，推送通知
    console.log('cli doing outputPackage...');
    // 获取及上传
    let outputRes = outputPackage(
      PLATFORM,
      mainName,
      appName,
      appConfig,
      envConfig,
      true
    );
    console.log('cli doing outputPackage end.', outputRes);
  } else {
    console.error('cli build failed');
    process.exit(1);
  }
} catch (error) {
  console.error('cli error: ', error.message);
  process.exit(1);
}

// 结束退出
console.log('cli build end.\n');
process.exit(0);

// 进行配置处理
function handleConfig(platform, mainName, appName, appConfig, envConfig) {
  switch (platform) {
    case 'android':
      _handleAndroid(mainName, appName, appConfig, envConfig);
      break;
    case 'ios':
      _handleIos(mainName, appName, appConfig, envConfig);
      break;
    default:
      return;
  }
}
// 进行依赖处理
function installDep(platform, mainName, appName, appConfig, envConfig) {
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
  // 默认安装依赖，新增库进行安装
  let cmdStr = 'yarn install';
  if (upDepStr) {
    cmdStr = 'yarn add ' + upDepStr;
  }
  console.log('shell.exec ', cmdStr);
  let { code, stdout, stderr } = shell.exec(cmdStr, { silent: false });
  console.log(`shell.exec [${cmdStr}] :`, code, stdout, stderr);
  if (code !== 0) {
    shell.echo(`shell.exec [${cmdStr}] Error: failed`); // shell.exit(1);
    return false;
  }
  return true;
}

// 进行模块处理
function checkModule(platform, mainName, appName, appConfig, envConfig) {
  switch (platform) {
    case 'android':
      // todo
      break;
    case 'ios':
      // todo
      break;
    default:
      return;
  }
}

// 进行包文件处理-获取-上传
function outputPackage(
  platform,
  mainName,
  appName,
  appConfig,
  envConfig,
  isUpload = true
) {
  let res = {
    package_path: '',
    package_size: '',
    package_md5: '',
    web_url: '',
    qrcode_url: '',
    download_url: '',
  };
  // 获取包文件
  let package_path = '';
  switch (platform) {
    case 'android':
      let files = shell
        .find(
          BasePath +
            `/android/app/build/outputs/apk/${DEBUG ? 'debug' : 'release'}/`
        )
        .filter(function (file) {
          return file.match(/\.apk$/);
        });
      if (files && files[0]) {
        package_path = files[0];
      }
      break;
    case 'ios':
      // todo
      break;
    default:
      return;
  }
  if (package_path) {
    // 设置文件路径
    res.package_path = package_path;
    // 设置文件大小
    res.package_size = formatBytes(fs.statSync(package_path).size);
    // 获取包文件MD5
    let buffer = fs.readFileSync(package_path);
    let hashMd5 = crypto.createHash('md5');
    hashMd5.update(buffer);
    let md5str = hashMd5.digest('hex').toUpperCase();
    res.package_md5 = md5str;
  }
  // 是否上传
  if (isUpload) {
    // 上传蒲公英
    if (appConfig?.modules?.pgyer?.enable) {
      let cmdStr = `curl -F "file=@${package_path}" -F "uKey=${envConfig.PGYER_API_USER}" -F "_api_key=${envConfig.PGYER_API_KEY}" https://www.pgyer.com/apiv2/app/upload`;
      console.log('shell.exec ', cmdStr);
      let { code, stdout, stderr } = shell.exec(cmdStr, { silent: false });
      console.log(`shell.exec [${cmdStr}] :`, code, stdout, stderr);
      if (code !== 0) {
        shell.echo(`shell.exec [${cmdStr}] Error: failed`); // shell.exit(1);
      }
      if (stdout) {
        let ou = JSON.parse(stdout);
        // console.log(`shell.exec stdout:`, stdout, ou);
        if (ou && ou.code === 0) {
          // 短链接
          res.web_url = ou?.data?.buildShortcutUrl
            ? `https://www.pgyer.com/${ou?.data?.buildShortcutUrl}`
            : '';
          // 二维码
          res.qrcode_url = ou?.data?.buildQRCodeURL || '';
        }
      }
      console.log(`upload [pgyer] success`);
    }
    // 上传firim
    if (appConfig?.modules?.firim?.enable) {
      // todo
    }
  }
  return res;
}

// 进行打包处理
function execBuild(platform, mainName, appName, appConfig, envConfig) {
  let cmdStr = '';
  switch (platform) {
    case 'android':
      if(os.platform() !== 'win32') {
        cmdStr = `cd android && chmod +x gradlew && cd ../ && `;
      }
      cmdStr += DEBUG ? 'yarn build-android-debug' : 'yarn build-android';
      break;
    case 'ios':
      cmdStr += DEBUG ? 'yarn build-ios-debug' : 'yarn build-ios';
      break;
    default:
      return false;
  }
  try {
    console.log(`shell.exec [${cmdStr}]`);
    let { code, stdout, stderr } = shell.exec(cmdStr, { silent: false });
    console.log(`shell.exec [${cmdStr}] :`, code);
    if (code !== 0) {
      console.log(`shell.exec [${cmdStr}] Error: failed`, code); // shell.exit(1);
      return false;
    }
    return true;
  } catch (error) {
    console.error('execBuild error', error.message);
    throw error;
  }
}

function _handleAndroid(mainName, appName, appConfig, envConfig) {
  try {
    // 替换主包的名称
    const mainPath =
      BasePath + '/android/app/src/main/java/com/rnshell/app/MainActivity.java';
    let data = fs.readFileSync(mainPath, 'utf8');
    // 匹配查找return "rnshell"
    data = data.replace(/return\s*"rnshell"/g, `return "${mainName}"`);
    fs.writeFileSync(mainPath, data, 'utf8');
    console.log('edit MainActivity success done');
  } catch (error) {
    console.error('edit MainActivity error', error.message);
    throw error;
  }

  // 复制资源到android目录
  const sPath = BasePath + '/app/android/*';
  const tPath = BasePath + '/android/';
  shell.cp('-Rf', sPath, tPath);

  // 合并配置项
  let aConfig = {
    pack_style: appConfig.pack_style,
    app_name: appName,
    app_package: appConfig?.info?.android?.package_id || '',
  }; // 通用参数，打包方式
  let bConfig = appConfig?.info?.android || {};
  let cConfig = appConfig?.account?.android || {};
  let modulesConfig = appConfig?.modules || {};
  let infoConfig = { ...aConfig, ...bConfig, ...cConfig };
  // 修改编译配置
  _overEditEnv('android', infoConfig, envConfig, modulesConfig);
}

function _handleIos(mainName, appName, appConfig, envConfig) {
  try {
    // 替换主包的名称
    const mainPath = BasePath + '/ios/rnshell/AppDelegate.m';
    let data = fs.readFileSync(mainPath, 'utf8');
    // 匹配查找return "rnshell"
    data = data.replace(
      /moduleName:\s*@"rnshell"/g,
      `moduleName:@"${mainName}"`
    );
    fs.writeFileSync(mainPath, data, 'utf8');
    console.log('edit AppDelegate success done');
  } catch (error) {
    console.error('edit AppDelegate error', error.message);
    throw error;
  }

  // 复制资源到ios目录
  const sPath = BasePath + '/app/ios/*';
  const tPath = BasePath + '/ios/';
  shell.cp('-Rf', sPath, tPath);

  // 合并配置项
  let aConfig = {
    pack_style: appConfig.pack_style,
    app_name: appName,
    app_package: appConfig?.info?.ios?.bundle_id || '',
  }; // 通用参数，打包方式
  let bConfig = appConfig?.info?.ios || {};
  let cConfig = appConfig?.account?.ios || {};
  let modulesConfig = appConfig?.modules || {};
  let infoConfig = { ...aConfig, ...bConfig, ...cConfig };
  // 修改编译配置
  _overEditEnv('ios', infoConfig, envConfig, modulesConfig);
}

function _overEditEnv(
  PLATFORM,
  infoConfig = {},
  envConfig = {},
  modulesConfig = {}
) {
  let newModulesConfig = {};
  // 模块配置需要格式化
  for (let key in modulesConfig) {
    if (Object.hasOwnProperty.call(modulesConfig, key)) {
      const element = modulesConfig[key];
      // key模块名，value模块参数 区分ios和android, 拼接并转成大写
      // 添加开启参数
      newModulesConfig[`${(key + '_' + 'enable').toUpperCase()}`] =
        element.enable;
      // 参加附加参数
      const config = element.config;
      for (let key2 in config) {
        if (Object.hasOwnProperty.call(config, key2)) {
          const element2 = config[key2];
          if (typeof element2 === 'string') {
            newModulesConfig[`${(key + '_' + key2).toUpperCase()}`] = element2;
          } else {
            for (let key3 in element2) {
              if (Object.hasOwnProperty.call(element2, key3)) {
                const element3 = element2[key3];
                if (typeof element3 === 'string') {
                  // 判断是否ios和android
                  if (key2 == 'android' || key2 == 'ios') {
                    newModulesConfig[
                      `${(key + '_' + key3 + '_' + PLATFORM).toUpperCase()}`
                    ] = element3;
                  } else {
                    newModulesConfig[
                      `${(key + '_' + key2 + '_' + key3).toUpperCase()}`
                    ] = element3;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // 合并配置项
  infoConfig = { ...infoConfig, ...newModulesConfig };
  // console.log('infoConfig', infoConfig);

  // 输出envConfig
  try {
    const envPath = BasePath + '/.env';
    // 检测文件是否存在，不存在则创建空文件
    if (!fs.existsSync(envPath)) {
      fs.writeFileSync(envPath, '', 'utf8');
    }
    let data = fs.readFileSync(envPath, 'utf8');
    for (const key in infoConfig) {
      if (Object.hasOwnProperty.call(infoConfig, key)) {
        const element = infoConfig[key];
        const kk = key.toUpperCase();
        if (Object.hasOwnProperty.call(envConfig, kk)) {
          if (element) {
            // 匹配查询并替换
            let ss = new RegExp(`${kk}\\s*=.*\n`);
            data = data.replace(ss, `${kk}=${element}\n`);
          }
        } else {
          if (element || element === 0 || element === false) {
            // 添加新内容
            data += `\n${kk}=${element}`;
          }
        }
      }
    }
    fs.writeFileSync(envPath, data, 'utf8');
    console.log('edit .env success done');
  } catch (error) {
    console.error('edit .env error', error.message);
    throw error;
  }
}

function formatBytes(bytes, decimals = 2) {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + '' + sizes[i];
}
