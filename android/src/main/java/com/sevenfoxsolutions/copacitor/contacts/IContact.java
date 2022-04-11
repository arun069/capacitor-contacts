package com.sevenfoxsolutions.copacitor.contacts;

public interface IContact {
    public String getDisplayName();
    public void setDisplayName(String displayName);
    public String getMobile();
    public void setMobile(String mobileNumber);
    public String getHomeNumber();
    public void setHomeNumber(String homeNumber);
    public String getWorkNumber();
    public void setWorkNumber(String workNumber);
    public String getEmailID();
    public void setEmailID(String emailID);
    public String getCompany();
    public void setCompany(String company);
    public String getJobTitle();
    public void setJobTitle(String jobTitle);
}
