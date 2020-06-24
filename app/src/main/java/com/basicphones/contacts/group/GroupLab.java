package com.basicphones.contacts.group;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.basicphones.contacts.database.GroupBaseHelper;
import com.basicphones.contacts.database.GroupDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupLab {

    private static GroupLab sGroupLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private GroupLab(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new GroupBaseHelper(mContext)
                .getWritableDatabase();
    }

    public static GroupLab get(Context context) {
        if (sGroupLab == null) {
            sGroupLab = new GroupLab(context);
        }
        return sGroupLab;
    }

    public void addGroup(Group group) {
        ContentValues groupValues = getGroupContentValues(group);
        List<ContentValues> contactValues = getContactContentValues(group);
        mDatabase.insert(GroupDbSchema.GroupTable.NAME, null, groupValues);
        for(int i = 0; i < contactValues.size(); i++){
            mDatabase.insert(GroupDbSchema.ContactGroupTable.NAME, null, contactValues.get(i));
        }
    }

    private static List<ContentValues> getContactContentValues(Group group) {

        List<ContentValues> values = new ArrayList<>();
        List<String> contacts = group.getContacts();
        for(int i = 0; i < contacts.size(); i++) {
            ContentValues value = new ContentValues();
            value.put(GroupDbSchema.ContactGroupTable.Cols.UUID, group.getmId().toString());
            value.put(GroupDbSchema.ContactGroupTable.Cols.ContactID, contacts.get(i));
            values.add(value);
        }
        return values;
    }

    private static ContentValues getGroupContentValues(Group group) {
        ContentValues values = new ContentValues();
        values.put(GroupDbSchema.GroupTable.Cols.UUID, group.getmId().toString());
        values.put(GroupDbSchema.GroupTable.Cols.NAME, group.getName());
        return values;
    }

    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<>();
        GroupCursorWrapper groupCursor = queryGroup(GroupDbSchema.GroupTable.NAME, null, null);
        try{
            groupCursor.moveToFirst();
            while(!groupCursor.isAfterLast()){
                String uuid = groupCursor.getGroupUUID();
                String name = groupCursor.getGroupName();
                List<String> contacts_ID = getContacts_ID(uuid);
                Group m_group = new Group(UUID.fromString(uuid));
                m_group.setName(name);
                m_group.setContacts(contacts_ID);
                groups.add(m_group);
                groupCursor.moveToNext();
            }
        }
        finally {
            groupCursor.close();
        }
        return groups;
    }

    public Group getGroup(UUID  id) {
        GroupCursorWrapper groupCursor = queryGroup(
                GroupDbSchema.GroupTable.NAME,
                GroupDbSchema.GroupTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (groupCursor.getCount() == 0) {
                return null;
            }
            groupCursor.moveToFirst();

            String uuid = groupCursor.getGroupUUID();
            String name = groupCursor.getGroupName();
            List<String> contact_ids = getContacts_ID(uuid);

            Group group = new Group(UUID.fromString(uuid));
            group.setName(name);
            group.setContacts(contact_ids);
            return group;
        } finally {
            groupCursor.close();
        }
    }

    public void updateGroup(Group group) {

        String uuidString = group.getmId().toString();

        String temp;
        //update contactIds
        List<String> newContactIds = new ArrayList<>();
        List<String> database_ContactIds = getContacts_ID(uuidString);
        List<String> phone_ContactIds = group.getContacts();

        //check for blank and null phones
        for(int i=0; i!=phone_ContactIds.size();i++) {
            temp = phone_ContactIds.get(i);
            if(temp != null && !temp.isEmpty()) {       //both lists not same.
                newContactIds.add(temp);
            }

        }
        //check if newPhones and databasePhones are different and update
        if(database_ContactIds != null && (database_ContactIds.size() == newContactIds.size())) {
            database_ContactIds.removeAll(newContactIds);
            if(!database_ContactIds.isEmpty()) {
                mDatabase.delete(GroupDbSchema.ContactGroupTable.NAME,
                        GroupDbSchema.ContactGroupTable.Cols.UUID + " = ?",
                        new String[] { uuidString });
                group.setContacts(newContactIds);
                List<ContentValues> contactValues = getContactContentValues(group);
                for(int i=0; i!=contactValues.size();i++)
                    mDatabase.insert(GroupDbSchema.ContactGroupTable.NAME, null, contactValues.get(i));
            }
        }
        else if(database_ContactIds != null && (database_ContactIds.size() != newContactIds.size())) {
            mDatabase.delete(GroupDbSchema.ContactGroupTable.NAME,
                    GroupDbSchema.ContactGroupTable.Cols.UUID + " = ?",
                    new String[] { uuidString });
            group.setContacts(newContactIds);
            List<ContentValues> contactValues = getContactContentValues(group);
            for(int i=0; i!=contactValues.size();i++)
                mDatabase.insert(GroupDbSchema.ContactGroupTable.NAME, null, contactValues.get(i));
        }

    }

    public List<Group> searchGroupsByName(String search)
    {
        List<Group> groups = new ArrayList<>();
        GroupCursorWrapper groupCursor = queryGroup(GroupDbSchema.GroupTable.NAME, GroupDbSchema.GroupTable.Cols.NAME + " LIKE ?",
                new String[] { "%" + search + "%" });
        try {
            groupCursor.moveToFirst();
            while (!groupCursor.isAfterLast()) {
                String uuid = groupCursor.getGroupUUID();
                String name = groupCursor.getGroupName();
                List<String> contacts_ID = getContacts_ID(uuid);
                Group m_group = new Group(UUID.fromString(uuid));
                m_group.setName(name);
                m_group.setContacts(contacts_ID);
                groups.add(m_group);
                groupCursor.moveToNext();
            }
        } finally {
            groupCursor.close();
        }
        return groups;
    }

    public List<String> getContacts_ID(String uuid) {
        List<String> contacts_id = new ArrayList<>();
        GroupCursorWrapper groupCursorWrapper = queryGroup(GroupDbSchema.ContactGroupTable.NAME, GroupDbSchema.ContactGroupTable.Cols.UUID + " = ?", new String[]{uuid});
        try {
            if(groupCursorWrapper.getCount() == 0) {
                return new ArrayList<>();
            }
            groupCursorWrapper.moveToFirst();
            while (!groupCursorWrapper.isAfterLast()) {
                contacts_id.add(groupCursorWrapper.getContactID());
                groupCursorWrapper.moveToNext();
            }
        }
        finally {
            groupCursorWrapper.close();
        }
        return contacts_id;
    }

    public void deleteGroup(Group group) {
        String uuidString = group.getmId().toString();

        //delete contactg
        mDatabase.delete(GroupDbSchema.GroupTable.NAME,
                GroupDbSchema.GroupTable.Cols.UUID + " = ?",
                new String[] { uuidString });

        mDatabase.delete(GroupDbSchema.ContactGroupTable.NAME,
                GroupDbSchema.ContactGroupTable.Cols.UUID + " = ?",
                new String[] { uuidString });

    }

    public File getPhotoFile(Group group) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, group.getPhotoFilename());
    }

    private GroupCursorWrapper queryGroup(String table, String whereClause, String[] whereArgs) {

        Cursor cursor = mDatabase.query(table, null, whereClause, whereArgs, null, null, null);
        return new GroupCursorWrapper(cursor);
    }
}
