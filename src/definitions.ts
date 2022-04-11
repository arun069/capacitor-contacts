export interface contactsPlugin {
  getContacts(options?: getContactsOpt): Promise<{results: any[]}>;
  getContactByName(options: getContactsByNameOpt): Promise<{results: any[]}>; 
  addNewContact(options:addNewContact): Promise<{results: any[]}>
  deleteContact(options: contactUid):Promise<{results: any[]}>
}
export interface getContactsOpt {
    /** getEmail default value is false */
   /** getEmail if pass true its also search saved email address along with mobile number */
  getEmail?: boolean,
     /** getPhoto default value is false */
      /** return saved photo in base64 string */
  getPhoto?: boolean
}
export interface getContactsByNameOpt {
  name: string,
   /** getEmail default value is false */
 /** getEmail if pass true its also search saved email address along with mobile number */
  getEmail?: boolean,
    /** getPhoto default value is false */
       /** return saved photo in base64 string */
  getPhoto?: boolean
}

export interface addNewContact{
  /** Display contact name */
   displayName: string;
   /** mobile number */
   mobileNumber: string,
   homeNumber?: string, 
   workNumber?: string,
   emailID?: string,
   company?: string,
   jobTitle?: string 
}

export interface contactUid{
  contactID : string
}
