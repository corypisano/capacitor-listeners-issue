import { registerPlugin } from '@capacitor/core';

import type { MyPluginPlugin } from './definitions';

const MyPlugin = registerPlugin<MyPluginPlugin>('MyPlugin', {
  web: () => import('./web').then(m => new m.MyPluginWeb()),
});

export * from './definitions';
export { MyPlugin };
