package com.sevenfoxsolutions.copacitor.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class contacts {

    public void getContacts(PluginCall call, Context context) {
        try{
           boolean rtEmail =   call.getBoolean("getEmail") == null? false: call.getBoolean("getEmail");
           boolean rtPhoto = call.getBoolean("getPhoto") == null? false:call.getBoolean("getPhoto");
            ArrayList<Map> contactList = LoadContacts(context,rtEmail, rtPhoto,0, null);
            JSONArray jsonArray = new JSONArray(contactList);
            JSObject ret = new JSObject();
            ret.put("results", jsonArray);
            call.resolve(ret);
        }
        catch (IllegalArgumentException e){
            call.reject("IllegalArgumentException: ", e);
        }
        catch (Exception e){
            call.reject("Error ", e);
        }
    }
    public void getContactByName(PluginCall call, Context context) {
        try{
            boolean rtEmail =   call.getBoolean("getEmail") == null? false: call.getBoolean("getEmail");
            boolean rtPhoto = call.getBoolean("getPhoto") == null? false:call.getBoolean("getPhoto");
            String filterVal = call.getString("name");
            if (filterVal.isEmpty()){
                call.reject("Filter value is null");
                return;
            }
            ArrayList<Map> contactList = LoadContacts(context,rtEmail, rtPhoto,1, filterVal);
            JSONArray jsonArray = new JSONArray(contactList);
            JSObject ret = new JSObject();
            ret.put("results", jsonArray);
            call.resolve(ret);
        }
        catch (IllegalArgumentException e){
            call.reject("IllegalArgumentException: ", e);
        }
        catch (Exception e){
            call.reject("Error ", e);
        }
    }
    public void addNewContact(PluginCall call, Context context){
        try{
            addContacts contact = new addContacts();
            contact.setDisplayName(call.getString("displayName"));
            contact.setMobile(call.getString("mobileNumber"));
            contact.setHomeNumber(call.getString("homeNumber"));
            contact.setWorkNumber(call.getString("workNumber"));
            contact.setEmailID(call.getString("emailID"));
            contact.setCompany(call.getString("company"));
            contact.setJobTitle(call.getString("jobTitle"));
            if (contact.getDisplayName().isEmpty() || contact.getMobile().isEmpty()){
                call.reject("Missing Required Parameters! Displayname, mobilenumber");
                return;
            }
           String status = insertNewContact(context, contact);
            if (status == "added"){
                JSObject ret = new JSObject();
                ret.put("results", true);
                call.resolve(ret);
            }else {
                call.reject(status);
            }
        }
        catch (IllegalArgumentException e){
            call.reject("IllegalArgumentException: ", e);
        }
        catch (Exception e){
            call.reject("Error ", e);
        }
    }
    public void deleteContact(PluginCall call, Context context){
        try{
            String _id = call.getString("contactID");
            if (_id == null || _id.isEmpty()){
                call.reject("Contact Id is not correct");
                return;
            }
           String status = deleteContact(context,_id);
            if (status == "deleted"){
                JSObject ret = new JSObject();
                ret.put("results", true);
                call.resolve(ret);
            }else {
                call.reject(status);
            }
        }
        catch (IllegalArgumentException e){
            call.reject("IllegalArgumentException: ", e);
        }
        catch (Exception e){
            call.reject("Error ", e);
        }
    }



    // convert bitmap to base64 string
    private static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
    private  static ArrayList<Map> LoadContacts(Context context,boolean rtEmail, boolean rtPhoto,int filterType, String filterValue){
        ArrayList<Map> contactList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = null;
        // select query according to filter param
        switch (filterType){
            case 0:
                // not filter applied
                cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
                break;
            case 1:
                // by name filter
                cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+ " Like ?", new String[]{"%" + filterValue + "%"} , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
                break;
        }
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                Map<String,String> map =  new HashMap<String, String>();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String pmr = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE));
                if (rtPhoto){
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));
                    // photo data
                    if (inputStream != null){
                        try{
                            Bitmap photo = BitmapFactory.decodeStream(inputStream);
                            String photo64 =convert(photo);
                            map.put("photo", photo64);
                        }catch (Exception e){
                            continue;
                        }
                    }
                }
                map.put("id", id);
                map.put("Name", name);
                map.put("alternateName", pmr);
                // add contact numbers
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    int Index = 1;
                    while ( pCur != null && pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        map.put("telephone" + Index, contactNumber);
                        Index++;
                    }
                    pCur.close();
                }
                if (rtEmail){
                    // add email data
                    Cursor pEmail = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    int Index = 1;
                    while (pEmail != null && pEmail.moveToNext()) {
                        String email = pEmail.getString(pEmail.getColumnIndex(
                                ContactsContract.CommonDataKinds.Email.DATA));
                        map.put("email" + Index, email);
                        Index++;
                    }
                    pEmail.close();
                }
                // add all data to arraylist
                contactList.add(map);
            }
        }
        if (cur != null) {
            cur.close();
        }
        return  contactList;
    }

    private static String insertNewContact(Context context,addContacts contact)
    {
        ArrayList <ContentProviderOperation> ops = new ArrayList <ContentProviderOperation> ();
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (contact.getDisplayName() != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            contact.getDisplayName()).build());
        }

        //------------------------------------------------------ Mobile Number
        if (contact.getMobile() != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getMobile() )
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (contact.getHomeNumber()  != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getHomeNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (contact.getWorkNumber() != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getWorkNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (contact.getEmailID() != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmailID())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (contact.getCompany() != null && contact.getJobTitle() != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, contact.getCompany())
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, contact.getJobTitle())
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return "added";
        } catch (Exception e) {
           return e.getMessage();
        }

    }
    private  static String deleteContact(Context context, String id){

        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cur  = cr.query(ContactsContract.Contacts.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone._ID+ " = ?", new String[]{id} , null);
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    System.out.println("The uri is " + uri.toString());
                    cr.delete(uri, null, null);
                }
            }
            if (cur != null) {
                cur.close();
            }
            return "deleted";
        } catch (Exception e) {
            return e.getMessage();
        }
    }


}


