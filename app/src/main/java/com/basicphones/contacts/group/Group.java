package com.basicphones.contacts.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group implements Serializable {

    private UUID mId;
    private String name;
    private List<String> contacts_ID = new ArrayList<>();
    private String mGroupId;

    public Group() {
        name="";
        mId = UUID.randomUUID();
    }

    public Group(UUID uuid) {
        mId = uuid;
    }

    public UUID getmId() {
        return mId;
    }

    public void setmId(UUID mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getContacts() {
        return contacts_ID;
    }

    public void setContacts(List<String> contacts) {
        this.contacts_ID = contacts;
    }

    public String getPhotoFilename() {
        return "GRP_IMG_" + getmId().toString() + ".png";
    }

    public void setmGroupId(String id) {
        mGroupId = id;
    }

    public String getmGroupId() {
        return mGroupId;
    }

}
