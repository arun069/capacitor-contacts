package com.sevenfoxsolutions.copacitor.contacts;

public class addContacts implements  IContact {
   private String _displayName;
    private  String _mobileNumber = null;
    private  String _homeNumber = null;
    private String _workNumber = null;
    private   String _emailID = null;
    private String _company = null;
    private String _jobTitle = null;

    @Override
    public String getDisplayName() {
        return _displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    @Override
    public String getMobile() {
        return _mobileNumber;
    }

    @Override
    public void setMobile(String mobileNumber) {
        _mobileNumber = mobileNumber;
    }

    @Override
    public String getHomeNumber() {
        return _homeNumber;
    }

    @Override
    public void setHomeNumber(String homeNumber) {
        _homeNumber =homeNumber;
    }

    @Override
    public String getWorkNumber() {
        return _workNumber;
    }

    @Override
    public void setWorkNumber(String workNumber) {
        _workNumber = workNumber;
    }

    @Override
    public String getEmailID() {
        return _emailID;
    }

    @Override
    public void setEmailID(String emailID) {
        _emailID =emailID;
    }

    @Override
    public String getCompany() {
        return _company;
    }

    @Override
    public void setCompany(String company) {
        _company =company;
    }

    @Override
    public String getJobTitle() {
        return _jobTitle;
    }

    @Override
    public void setJobTitle(String jobTitle) {
        _jobTitle = jobTitle;
    }

}
