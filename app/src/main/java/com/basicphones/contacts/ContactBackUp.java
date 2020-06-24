package com.basicphones.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ContactBackUp {

    public static boolean saveContacts(Context context, String path){
        ArrayList<String> vCard = new ArrayList<String>();
        String vfile = "Contacts" + "_" + System.currentTimeMillis()+".vcf";
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String storage_path = path + File.separator + vfile;
        FileOutputStream mFileOutputStream = null;
        try {
            mFileOutputStream = new FileOutputStream(storage_path, false);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(cursor!=null&&cursor.getCount()>0)
        {
            cursor.moveToFirst();
            for(int i =0;i<cursor.getCount();i++)
            {

                get(cursor, vCard, context, vfile);
                Log.d("TAG", "Contact "+(i+1)+"VcF String is"+vCard.get(i));
                cursor.moveToNext();
            }

        }
        else
        {
            Log.d("TAG", "No Contacts in Your Phone");
        }
        if(vCard.size() > 0){
            for(int i = 0; i < vCard.size(); i++){
                try {
                    mFileOutputStream.write(vCard.get(i).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    public static void get(Cursor cursor, ArrayList<String> vCard, Context context, String vfile)
    {

        //cursor.moveToFirst();
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor fd;
        try {
            fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            FileInputStream fis = fd.createInputStream();
//            long length = fd.getLength();
//            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            byte[] buf = readBytes(fis);
            fis.read(buf);
            String vcardstring= new String(buf);
            vCard.add(vcardstring);
        } catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}
