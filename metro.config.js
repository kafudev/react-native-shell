/**
 * Metro configuration for React Native
 * https://github.com/facebook/react-native
 *
 * @format
 */
const os = require('os');
/*
 获取本机IP
 */
function getIPAdressArr() {
  let localIPAddress = [];
  let interfaces = os.networkInterfaces();
  for (let devName in interfaces) {
    let iface = interfaces[devName];
    for (let i = 0; i < iface.length; i++) {
      let alias = iface[i];
      if (
        alias.family === 'IPv4' &&
        alias.address !== '127.0.0.1' &&
        !alias.internal
      ) {
        localIPAddress.push(alias.address);
      }
    }
  }
  return localIPAddress;
}

module.exports = {
  resolver: {
    sourceExts: ['js', 'json', 'ts', 'tsx', 'jsx'],
  },
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
  server: {
    /* server options */
    port: 8081,
    enhanceMiddleware: (Middleware, Server) => {
      let ips = getIPAdressArr();
      let port =
        (Server._config && Server._config && Server._config.server.port) || '';
      console.log('\nMetro server host:port');
      for (let i = 0; i < ips.length; i++) {
        const ip = ips[i];
        console.log(`${i}. ${ip}:${port}`);
      }
      console.log('\n');
      return Middleware;
    },
  },
};
