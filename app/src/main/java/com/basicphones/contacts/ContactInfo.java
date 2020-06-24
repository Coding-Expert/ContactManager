package com.basicphones.contacts;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContactInfo implements Serializable {

    private UUID mId;
    private String mName;
    private List<String> mPhones = new ArrayList<>();
    private List<String> mEmails = new ArrayList<>();
    private String mContact_id;
    private Bitmap bitmap = null;
    private String raw_contact_id;

    public ContactInfo() {
        // Generate unique identifier
        mName="";
        mId = UUID.randomUUID();
    }

    public ContactInfo(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getmContact_id() {
        return mContact_id;
    }
    public void setmContact_id(String contact_id) {
        mContact_id = contact_id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getPhones() {
        return mPhones;
    }

    public void setPhones(List<String> phones) {
        mPhones = phones;
    }

    public void addPhone(String phone) {
        mPhones.add(phone);
    }

    public List<String> getEmails() {
        return mEmails;
    }

    public void setEmails(List<String> emails) {
        mEmails = emails;
    }

    public void addEmail(String email) {
        mEmails.add(email);
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".png";
    }

    public void setRaw_contact_id(String rawId){
        raw_contact_id = rawId;
    }

    public String getRaw_contact_id() {
        return raw_contact_id;
    }

}
