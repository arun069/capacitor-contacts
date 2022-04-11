import { WebPlugin } from '@capacitor/core';

import type { addNewContact, contactsPlugin, contactUid, getContactsByNameOpt, getContactsOpt } from './definitions';

export class contactsWeb extends WebPlugin implements contactsPlugin {
 
  async getContacts(options: getContactsOpt): Promise<{results: any[]}> {  
    console.log(options.getEmail);   console.log(options.getPhoto);  
    return {results: [{ message: 'this plugin not working in web platform!'}, {message2: 'makadina'}]};
  }
  async getContactByName(options: getContactsByNameOpt): Promise<{results: any[]}> {    
    console.log(options.name)  
    console.log(options.getEmail);   console.log(options.getPhoto);  
    return {results:[{ message: 'this plugin not working in web platform!'}]};
  }
  async addNewContact(options: addNewContact): Promise<{results: any[]}> {    
    console.log(options.displayName)  
    return {results:[{ message: 'this plugin not working in web platform!'}]};
  }
  async deleteContact(options: contactUid): Promise<{results: any[]}> {    
    console.log(options.contactID)  
    return {results:[{ message: 'this plugin not working in web platform!'}]};
  }

}
