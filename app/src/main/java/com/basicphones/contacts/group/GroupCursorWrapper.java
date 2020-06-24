package com.basicphones.contacts.group;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.basicphones.contacts.database.GroupDbSchema;

public class GroupCursorWrapper extends CursorWrapper {

    public GroupCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getGroupUUID() {
        return getString(getColumnIndex(GroupDbSchema.GroupTable.Cols.UUID));
    }

    public String getContactID() {
        return getString(getColumnIndex(GroupDbSchema.ContactGroupTable.Cols.ContactID));
    }

    public String getGroupName() {
        return getString(getColumnIndex(GroupDbSchema.GroupTable.Cols.NAME));
    }
}
