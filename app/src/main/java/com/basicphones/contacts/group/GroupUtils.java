package com.basicphones.contacts.group;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.basicphones.contacts.ContactInfo;
import com.basicphones.contacts.ContactUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupUtils {

    public static List<Group> getGroupList(Activity activity) {
        List<Group> group_list = new ArrayList<>();
        String[] GROUP_PROJECTION = new String[] {ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
        Cursor managedCursor = activity.managedQuery(ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, ContactsContract.Groups.DELETED + "=0", null, ContactsContract.Groups.TITLE + " ASC");
//        Log.d("*** Here Counts:", "** " + managedCursor.getCount());
        if(managedCursor != null && managedCursor.getCount() > 0) {
            while(managedCursor.moveToNext()) {
                String groupName = managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.Groups.TITLE));
                String groupId = managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.Groups._ID));
                Group group = new Group();
                group.setName(groupName);
                group.setmGroupId(groupId);
                group_list.add(group);
                Log.d("GroupName: ", ""+ groupName);
            }

        }
        return group_list;
    }

    public static void createGroup(Activity activity, String name) {
        try{
            ContentValues groupValues = null;
            ContentResolver cr = activity.getContentResolver();
            groupValues = new ContentValues();
            groupValues.put(ContactsContract.Groups.TITLE, name);
            groupValues.put(ContactsContract.Groups.GROUP_VISIBLE, 1);
            groupValues.putNull(ContactsContract.Groups.ACCOUNT_NAME);
            groupValues.putNull(ContactsContract.Groups.ACCOUNT_TYPE);
            groupValues.put(ContactsContract.Groups.SHOULD_SYNC, false);
            cr.insert(ContactsContract.Groups.CONTENT_URI, groupValues);

        }
        catch(Exception e) {
            Log.d("######### Exception :", "" + e.getMessage());
        }
    }

    public static List<ContactInfo> getContactList(Activity activity, String groupId) {
        List<ContactInfo> contact_list = new ArrayList<>();
        String where =  ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID +"="+groupId
                +" AND "
                + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE+"='"
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE+"'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID, ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID, ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, where,null,
                ContactsContract.Data.DISPLAY_NAME+" COLLATE LOCALIZED ASC");
        while(cursor.moveToNext()){
            ContactInfo contact = new ContactInfo();
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            String raw_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID));      /// get raw_contact_id from specified group
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
            contact.setmContact_id(id);
            contact.setRaw_contact_id(raw_id);
            contact.setName(name);
            contact.setPhones(ContactUtils.getPhoneList(id, activity));
            contact.setEmails(ContactUtils.getEmailList(id, activity));
//            Cursor phoneFetchCursor = getContentResolver().query(Phone.CONTENT_URI,
//                    new String[]{Phone.NUMBER,Phone.DISPLAY_NAME,Phone.TYPE},
//                    Phone.CONTACT_ID+"="+item.id,null,null);
//            while(phoneFetchCursor.moveToNext()){
//                item.phNo = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(Phone.NUMBER));
//                item.phDisplayName = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(Phone.DISPLAY_NAME));
//                item.phType = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(Phone.TYPE));
//            }
//            phoneFetchCursor.close();
            contact_list.add(contact);
        }
        cursor.close();
        return contact_list;
    }

    public static void deleteContactFromGroup(long row_contactId, long groupId, Activity activity)
    {
        ContentResolver cr = activity.getContentResolver();
        String where = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=" + groupId + " AND "
                + ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID + "=?" + " AND "
                + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";

        try {
            cr.delete(ContactsContract.Data.CONTENT_URI, where,
                    new String[] { String.valueOf(row_contactId) });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (Long id : getRawContactIdsForContact(contactId))
//        {
//            try
//            {
//                cr.delete(ContactsContract.Data.CONTENT_URI, where,
//                        new String[] { String.valueOf(id) });
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
    }

    public static Uri addToGroup(String personId, String groupId, Activity activity) {
        try {
            ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, ContactUtils.getRawContactId(personId, activity));
            values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
            return activity.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
        catch (Exception e) {

        }
        return Uri.EMPTY;
    }
    public static void deleteGroup(Activity activity, String groupId) {
        ArrayList<ContentProviderOperation> mOperations = new ArrayList<ContentProviderOperation>();

        // Build the uri of your group with its id
        Uri uri = ContentUris.withAppendedId(ContactsContract.Groups.CONTENT_URI, Long.parseLong(groupId)).buildUpon()
                .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                .build();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(uri);
        mOperations.add(builder.build());

        // Then apply batch
        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, mOperations);
        } catch (Exception e) {
            Log.d("########## Exception :", "" + e.getMessage());
        }
    }
}
