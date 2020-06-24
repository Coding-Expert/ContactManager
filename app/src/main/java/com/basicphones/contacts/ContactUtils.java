package com.basicphones.contacts;

import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactUtils {

    public static void saveContactInPhone(ContactInfo contact, Activity activity, Bitmap bitmap_image) {        ///// add new contact to phone

        ArrayList< ContentProviderOperation > ops = new ArrayList <ContentProviderOperation> ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        if (contact.getName() != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            contact.getName()).build());
        }
        if(contact.getPhones().size() > 0) {
            for(int i = 0; i < contact.getPhones().size(); i++) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(i))
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
            }
        }
        if(contact.getEmails().size() > 0){
            for(int i = 0; i < contact.getEmails().size(); i++) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmails().get(i))
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
            }
        }

        if (bitmap_image != null) { // If an image is selected
            // successfully
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap_image.compress(Bitmap.CompressFormat.PNG, 75,
                    stream);
            // Adding insert operation to operations list
            // to insert Photo in the table ContactsContract.Data
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                            ContactsContract.Data.RAW_CONTACT_ID,
                            0)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY,
                            1)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray()).build());   ///(byte[])null).build());


            try {
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveContactInPhone(ContactInfo contact, Context activity, byte[] photo_data) {        ///// add new contact to phone

        ArrayList< ContentProviderOperation > ops = new ArrayList <ContentProviderOperation> ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        if (contact.getName() != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            contact.getName()).build());
        }
        if(contact.getPhones().size() > 0) {
            for(int i = 0; i < contact.getPhones().size(); i++) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(i))
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
            }
        }
        if(contact.getEmails().size() > 0){
            for(int i = 0; i < contact.getEmails().size(); i++) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmails().get(i))
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
            }
        }

        if (photo_data != null) { // If an image is selected
            // Adding insert operation to operations list
            // to insert Photo in the table ContactsContract.Data
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                            ContactsContract.Data.RAW_CONTACT_ID,
                            0)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY,
                            1)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.Photo.PHOTO, photo_data).build());   ///(byte[])null).build());
        }

        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean updateContact(ContactInfo contact, Activity activity, Bitmap bitmap_image)          ///// update content of selected contact
    {

        boolean success = true;
        try
        {

            ContentResolver contentResolver  = activity.getContentResolver();

            String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

            String[] nameParams = new String[]{ contact.getmContact_id(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
            String[] numberParams = new String[]{ contact.getmContact_id(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
            String[] photoParams = new String[]{ contact.getmContact_id(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            if(!contact.getName().equals(""))
            {
                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                    .withSelection(where,nameParams)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                    .build());
            }


//            Long id = getID("455123");
//            int i = getActivity().getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts._ID+"=?", new String[]{id.toString()});


            String id= contact.getmContact_id();
            List<String> old_phone = getPhoneList(id, activity);
            Uri url = ContactsContract.Data.CONTENT_URI;
            if(old_phone.size() > 0){
                for(int i = 0; i < old_phone.size(); i++) {
                    String where1 = ContactsContract.Data.CONTACT_ID + " = '" + id + "' AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + old_phone.get(i) + "'";
                    activity.getApplicationContext().getContentResolver().delete(url, where1, null);
                }
            }
            if(contact.getPhones().size() > 0) {
                for(int i = 0; i < contact.getPhones().size(); i++) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(contact.getmContact_id(), activity))
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(i)).
                                    withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }
            }
            List<String> old_email = getEmailList(contact.getmContact_id(), activity);
            if(old_email.size() > 0)
            {
                for(int i = 0; i < old_email.size(); i++) {
                    String where1 = ContactsContract.Data.CONTACT_ID + " = '" + contact.getmContact_id() + "' AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + " = '" + old_email.get(i) + "'";
                    activity.getApplicationContext().getContentResolver().delete(url, where1, null);
                }
            }
            if(contact.getEmails().size() > 0){
                for(int i = 0; i < contact.getEmails().size(); i++) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(contact.getmContact_id(), activity))
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmails().get(i))
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
                }
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(bitmap_image == null)
            {
                final ArrayList<ContentProviderOperation> ops1 = new ArrayList<ContentProviderOperation>();
                ops1.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withSelection(where,photoParams)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, null)
                        .build());

                activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops1);


            }
            else{
                //                bitmap_image = PictureUtils.getScaledBitmap(mTempPhotoFile.getAbsolutePath(), imageViewWidth, imageViewHeight);

                bitmap_image.compress(Bitmap.CompressFormat.PNG, 75, stream);
                ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(
                            ContactsContract.Data.RAW_CONTACT_ID,
                            getRawContactId(contact.getmContact_id(), activity))
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY,
                            1)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.Photo.PHOTO,
                            stream.toByteArray()).build());
                try {
                    stream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            Log.d("contact message:", "sdfsdfsdfsdf");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            success = false;

        }
        return success;
    }

    public static boolean updateContact(ContactInfo contact, Activity activity, byte[] byte_image)          ///// update content of selected contact
    {

        boolean success = true;
        try
        {
            ContentResolver contentResolver  = activity.getContentResolver();

            String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

            String[] nameParams = new String[]{ contact.getmContact_id(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
            String[] numberParams = new String[]{ contact.getmContact_id(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
            String[] photoParams = new String[]{ contact.getmContact_id(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            if(!contact.getName().equals(""))
            {
                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withSelection(where,nameParams)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                        .build());
            }


//            Long id = getID("455123");
//            int i = getActivity().getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts._ID+"=?", new String[]{id.toString()});
            String id= contact.getmContact_id();
            List<String> old_phone = getPhoneList(id, activity);
            Uri url = ContactsContract.Data.CONTENT_URI;
            if(old_phone.size() > 0){
                for(int i = 0; i < old_phone.size(); i++) {
                    String where1 = ContactsContract.Data.CONTACT_ID + " = '" + id + "' AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + old_phone.get(i) + "'";
                    activity.getApplicationContext().getContentResolver().delete(url, where1, null);
                }
            }
            if(contact.getPhones().size() > 0) {
                for(int i = 0; i < contact.getPhones().size(); i++) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(contact.getmContact_id(), activity))
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(i)).
                                    withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }
            }
            List<String> old_email = getEmailList(contact.getmContact_id(), activity);
            if(old_email.size() > 0)
            {
                for(int i = 0; i < old_email.size(); i++) {
                    String where1 = ContactsContract.Data.CONTACT_ID + " = '" + contact.getmContact_id() + "' AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + " = '" + old_email.get(i) + "'";
                    activity.getApplicationContext().getContentResolver().delete(url, where1, null);
                }
            }
            if(contact.getEmails().size() > 0){
                for(int i = 0; i < contact.getEmails().size(); i++) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(contact.getmContact_id(), activity))
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmails().get(i))
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
                }
            }
//            if(byte_image == null)
//            {
//                final ArrayList<ContentProviderOperation> ops1 = new ArrayList<ContentProviderOperation>();
//                ops1.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
//                        .withSelection(where,photoParams)
//                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, null)
//                        .build());
//
//                activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops1);
//
//
//            }
//            else{
//                ops.add(ContentProviderOperation
//                        .newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValue(
//                                ContactsContract.Data.RAW_CONTACT_ID,
//                                getRawContactId(contact.getmContact_id(), activity))
//                        .withValue(ContactsContract.Data.IS_SUPER_PRIMARY,
//                                1)
//                        .withValue(ContactsContract.Data.MIMETYPE,
//                                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
//                        .withValue(
//                                ContactsContract.CommonDataKinds.Photo.PHOTO,
//                                byte_image).build());
//            }


            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public static List<String> getPhoneList(String contactId, Activity activity) {  ///////// get phone list from phone using contact id
        List<String> phones = new ArrayList<>();
        try{
            ContentResolver cr = activity.getContentResolver();
            String[] projection = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//            int hash_number = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//            if (hash_number > 0) {
            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);
            if(cp.getCount() > 0) {

                while (cp.moveToNext()) {
                    String number = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int type = cp.getInt(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    switch (type) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            // do something with the Home number here...
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            // do something with the Mobile number here...
                            Log.d("ContactsH", number);
                            //                                    this.callByNumber(number);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            // do something with the Work number here...
                            break;
                    }
                    phones.add(number);
                }
//                    mContact.setPhones(phones);
                cp.close();
            }
//            }
        }
        catch(Exception e) {
            Log.d("Error in Contacts Read:", "" + e.getMessage());
        }
        return phones;
    }
    ////////// get raw_contact_id of contact using contact id
    public static String getRawContactId(String contactId, Activity activity)
    {
        String res = "";
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{ contactId };
        Cursor c = activity.getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if(c != null && c.moveToFirst())
        {
            res = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));
            c.close();
        }

        return res;
    }
    public static List<String> getEmailList(String id, Activity activity) {         ///// get email list from phone using contact id
        List<String> email_list = new ArrayList<>();
        Cursor emailCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
        if(emailCur.getCount() > 0) {
            List<String> emails = new ArrayList<>();
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                email_list.add(email);
            }
            emailCur.close();
        }
        return email_list;
    }

    public static Bitmap getPhoto(long contactId, Activity activity) {          //// get contact photo from phone using contact id
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = activity.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }

    public static Long getID(String number, Activity activity){

        Uri uri =  Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor c =  activity.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);
        while(c.moveToNext()){
            return c.getLong(c.getColumnIndex(ContactsContract.PhoneLookup._ID));
        }
        return null;
    }

    public static void deleteContact(Activity activity, String contactId) {             /// delete contact from phone using contact id
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,contactId);
        int deleted = activity.getContentResolver().delete(uri,null,null);  /// if operation is success, return deleted > 0
    }

    public static List<ContactInfo> getContactsFromPhone(Activity activity) {
        List<ContactInfo> contactlist = new ArrayList<>();
        try{
            ContentResolver cr = activity.getContentResolver();
            ContentProviderClient mCProviderClient = cr.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);
//            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            String[] projection = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER,};
//            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            Cursor cursor = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            String phone = null;
            //            cursor.moveToFirst();
            int count = cursor.getCount();
            if( cursor != null && cursor.moveToFirst()) {
                if(cursor.getCount() > 0) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Log.d("contact id: ", id);
                        ContactInfo mContact = new ContactInfo();
                        mContact.setmContact_id(id);
                        mContact.setName(name);
                        int hash_number = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hash_number > 0) {
                            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
//                        if(cp != null && cp.moveToFirst()) {
//                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            int type = cp.getInt(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                            cp.close();
//                        }
                            if(cp.getCount() > 0) {
                                List<String> phones = new ArrayList<>();
                                while (cp.moveToNext()) {
                                    String number = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    int type = cp.getInt(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                    switch (type) {
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                            // do something with the Home number here...
                                            break;
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                            // do something with the Mobile number here...
                                            Log.d("ContactsH", number);
//                                    this.callByNumber(number);
                                            break;
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                            // do something with the Work number here...
                                            break;
                                    }
                                    if(phones.size() > 0){
                                        int dup_count = 0;
                                        for(int i = 0; i < phones.size(); i++){
                                            if(phones.get(i).equals(number)){
                                                dup_count++;
                                                break;
                                            }
                                        }
                                        if(dup_count == 0){
                                            phones.add(number);
                                        }
                                    }
                                    else{
                                        phones.add(number);
                                    }

                                }
                                mContact.setPhones(phones);
                                cp.close();
                            }
                        }
                        Cursor emailCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                        if(emailCur.getCount() > 0) {
                            List<String> emails = new ArrayList<>();
                            while (emailCur.moveToNext()) {
                                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                if(emails.size() > 0){
                                    int dup_count = 0;
                                    for(int i = 0; i < emails.size(); i++){
                                        if(emails.get(i).equals(email)){
                                            dup_count++;
                                            break;
                                        }
                                    }
                                    if(dup_count == 0){
                                        emails.add(email);
                                    }
                                }
                                else{
                                    emails.add(email);
                                }
                            }
                            mContact.setEmails(emails);
                            emailCur.close();
                        }
//                        ContactLab.get(getActivity()).addContact(mContact);
//                        Bitmap photo = getPhoto(Long.parseLong(id));
//                        if(photo != null){
//                            mContact.setBitmap(photo);
//                        }
                        contactlist.add(mContact);
                    } while (cursor.moveToNext());
                }

            }
//            int count = cursor.getCount();
//            Log.d("contact count", ""+ cursor.getCount());

        }
        catch(Exception e) {
            Log.d("Error in Contacts Read:", "" + e.getMessage());
        }
        return contactlist;
    }
}
