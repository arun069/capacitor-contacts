package com.sevenfoxsolutions.copacitor.contacts;

import android.Manifest;

import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(name = "contacts", permissions = {
//        @Permission(
//                alias = "camera",
//                strings = { Manifest.permission.CAMERA }
//                      ),
        @Permission(
                alias = "contacts",
                strings ={
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
        }
        )
})
public class contactsPlugin extends Plugin {

    private contacts implementation = new contacts();

    @PluginMethod
    public void getContacts(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            requestPermissionForAlias("contacts", call, "getcontactsCallback");
        } else {
            implementation.getContacts(call, this.getContext());
        }

    }

    @PluginMethod
    public void getContactByName(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            requestPermissionForAlias("contacts", call, "getcontactsBynameCallback");
        } else {
            implementation.getContactByName(call, this.getContext());
        }

    }
    @PluginMethod
    public void addNewContact(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            requestPermissionForAlias("contacts", call, "addCallback");
        } else {
            implementation.addNewContact(call, this.getContext());
        }
    }

    @PluginMethod
    public void deleteContact(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            requestPermissionForAlias("contacts", call, "deleteallback");
        } else {
            implementation.deleteContact(call, this.getContext());
        }
    }





    //  call back function from permission
    @PermissionCallback
    private void getcontactsCallback(PluginCall call) {
        if (getPermissionState("contacts") == PermissionState.GRANTED) {
            implementation.getContacts(call, this.getContext());
        } else {
            call.reject("Permission is required");
        }
    }
    @PermissionCallback
    private void getcontactsBynameCallback(PluginCall call) {
        if (getPermissionState("contacts") == PermissionState.GRANTED) {
            implementation.getContactByName(call, this.getContext());
        } else {
            call.reject("Permission is required");
        }
    }
    @PermissionCallback
    private void addCallback(PluginCall call) {
        if (getPermissionState("contacts") == PermissionState.GRANTED) {
            implementation.addNewContact(call, this.getContext());
        } else {
            call.reject("Permission is required");
        }
    }
    @PermissionCallback
    private void deleteallback(PluginCall call) {
        if (getPermissionState("contacts") == PermissionState.GRANTED) {
            implementation.deleteContact(call, this.getContext());
        } else {
            call.reject("Permission is required");
        }
    }

}
