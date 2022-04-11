import { registerPlugin } from '@capacitor/core';

import type { contactsPlugin } from './definitions';

const contacts = registerPlugin<contactsPlugin>('contacts', {
  web: () => import('./web').then(m => new m.contactsWeb()),
});

export * from './definitions';
export { contacts };
