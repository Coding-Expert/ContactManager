package com.basicphones.contacts.contactbackup;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.basicphones.contacts.ContactInfo;
import com.basicphones.contacts.ContactUtils;
import com.basicphones.contacts.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.documentfile.provider.DocumentFile;
import ezvcard.VCard;
import ezvcard.android.AndroidCustomFieldScribe;
import ezvcard.io.text.VCardReader;
import static ezvcard.util.IOUtils.closeQuietly;

public class VcfContactManagement extends AsyncTask<Object, Integer, Void> {

    ProgressDialog progressDialog;
    public Context context;
    public String[] files;
    public String export_filepath;
    public boolean export_flag;
    public MainActivity m_activity;
    public DocumentFile documentFile;
    public boolean process_flag = false;
    public List<ContactInfo> phone_contacts;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(export_flag) {
            progressDialog = ProgressDialog.show(context, "", "loading...");
        }
        else{
            progressDialog = ProgressDialog.show(context, "", "loading...");
        }

    }

    @Override
    protected Void doInBackground(Object... objects) {
        boolean export_flag = (boolean)objects[0];
        if(export_flag){
//            saveContacts(this.context, export_filepath);
            writeFile(documentFile);
        }
        else{
            for(int i = 0; i < files.length; i++){
                readVCFAndWriteToSimCard(context, files[0]);
            }

        }
        return null;
    }

    public void setImportData(Context context, String[] fileList, boolean flag, MainActivity activity, List<ContactInfo> contacts){
        this.context = context;
        this.files = fileList;
        this.export_flag = flag;
        this.m_activity = activity;
        this.phone_contacts = contacts;
    }
    public void setExportData(Context context, DocumentFile pickedDir, boolean flag){
        this.context = context;
        this.export_flag = flag;
        this.documentFile = pickedDir;
    }
//    public void setExportData(Context context, String path, boolean flag){
//        this.context = context;
//        this.export_flag = flag;
//        this.export_filepath = path;
//    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //super.onProgressUpdate(values);
        Log.d("progressValues ", " " + values[0]);
        progressDialog.setProgress((int) values[0]);
//        progressDialog.setProgress(50);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        if(export_flag) {
            Toast.makeText(context, "exported contacts", Toast.LENGTH_SHORT).show();

        }

        else{

            Toast.makeText(context, "imported contacts", Toast.LENGTH_SHORT).show();
            m_activity.refreshContactView();
        }
    }

    public void readVCFAndWriteToSimCard(Context context, String path) {
        File vcardFile = new File(path);
        Uri simUri = Uri.parse("content://icc/adn");
        ContentValues cv = new ContentValues();
        if (!vcardFile.exists()) {
            throw new RuntimeException("vCard file does not exist: " + path);
        }
        VCardReader reader = null;
        try {
            reader = new VCardReader(vcardFile);
            reader.registerScribe(new AndroidCustomFieldScribe());
            VCard vcard = null;
            while ((vcard = reader.readNext()) != null) {
                //I'm inserting only one contact here
                try {
                    if (vcard.getFormattedName().getValue() != null && !vcard.getTelephoneNumbers().isEmpty()) {
//                        cv = new ContentValues();
//                        cv.put("tag", vcard.getFormattedName().getValue());
//                        cv.put("number", vcard.getTelephoneNumbers().get(0).getText());
//                        getContentResolver().insert(simUri, cv);
                        ContactInfo import_contact = getImportContact(vcard);
                        byte[] import_photo = getPhotoData(vcard);
                        insertContactToPhone(import_contact, import_photo);
//                        ContactUtils.saveContactInPhone(import_contact, context, import_photo);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            process_flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(reader);
        }
    }
    public boolean insertContactToPhone(ContactInfo importContact, byte[] photo_byte){
        if(phone_contacts.size() > 0){
            int same_count = 0;
            for(int i = 0; i < phone_contacts.size(); i++){
                ContactInfo m_contact = phone_contacts.get(i);
                if(m_contact.getName().equals(importContact.getName())){
                    same_count++;
                    if(m_contact.getPhones().size() == 0){
                        if(importContact.getPhones().size() == 0){      //  if there are no phone_number in import_contact
                            if(m_contact.getEmails().size() == 0){      //  if there are no emails in phone_contact
                                if(importContact.getEmails().size() == 0){
                                }
                                else{
                                    ContactUtils.updateContact(importContact, m_activity, photo_byte);
                                }
                            }
                            else{

                            }
                        }
                        else{
                            ContactUtils.saveContactInPhone(importContact, context, photo_byte);
                        }
                    }
                    else{
                        if(importContact.getPhones().size() == 0){
                            ContactUtils.saveContactInPhone(importContact, context, photo_byte);
                        }
                        else{
                            List<String> new_phones = new ArrayList<String>();
                            int count = 0;
                            ArrayList<String> index_array = new ArrayList<>();
                            for(int j = 0; j < m_contact.getPhones().size(); j++){
                                String phone_number1 = m_contact.getPhones().get(j);
                                for(int k = 0; k < importContact.getPhones().size(); k++){
                                    if(phone_number1.equals(importContact.getPhones().get(k))){
                                        count++;
                                        index_array.add("" + k);
                                    }
                                }
                            }
                            if(count > 0){
                                if (count == importContact.getPhones().size() && count == m_contact.getPhones().size()) {

                                }
                                else{
                                    new_phones = m_contact.getPhones();
                                    for (int p = 0; p < importContact.getPhones().size(); p++) {
                                        for (int q = 0; q < index_array.size(); q++) {
                                            if (p != Integer.parseInt(index_array.get(q))) {
                                                new_phones.add(importContact.getPhones().get(p));
                                            }
                                        }
                                    }
                                    m_contact.setPhones(new_phones);
                                    ContactUtils.updateContact(m_contact, m_activity, photo_byte);
                                }
                            }
                            else{
                                ContactUtils.saveContactInPhone(importContact, context, photo_byte);
                            }
                        }
                    }
                }

            }
            if(same_count == 0){
                ContactUtils.saveContactInPhone(importContact, context, photo_byte);
            }
        }
        return true;
    }
    public ContactInfo getImportContact(VCard vcard){
        ContactInfo import_contact = new ContactInfo();
        import_contact.setName(vcard.getFormattedName().getValue());
        List<String> newPhones = new ArrayList<>();
        if(vcard.getTelephoneNumbers().size() > 0){
            for(int i = 0; i < vcard.getTelephoneNumbers().size(); i++){
                newPhones.add(vcard.getTelephoneNumbers().get(i).getText());
            }
        }
        import_contact.setPhones(newPhones);
        List<String> newEmails = new ArrayList<>();
        if(!vcard.getEmails().isEmpty() || vcard.getEmails().size() > 0){
            for(int i = 0; i < vcard.getEmails().size(); i++){
                newEmails.add(vcard.getEmails().get(i).getValue());
            }
        }
        import_contact.setEmails(newEmails);

        return import_contact;
    }
    public byte[] getPhotoData(VCard vcard){
        byte[] photo_data = null;
        if(!vcard.getPhotos().isEmpty() || vcard.getPhotos().size() > 0){
            photo_data = vcard.getPhotos().get(0).getData();
        }
        return photo_data;
    }

    public ArrayList<String> saveContacts(){
        ArrayList<String> vCard = new ArrayList<String>();
        String vfile = "Contacts" + "_" + System.currentTimeMillis()+".vcf";
//        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER, ContactsContract.Contacts.LOOKUP_KEY};
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        int count = cursor.getCount();
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

        return vCard;

    }

    public void get(Cursor cursor, ArrayList<String> vCard, Context context, String vfile)
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

    public byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void writeFile(DocumentFile pickedDir) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        String vfile = "Contacts" + "_" + currentDateandTime +".vcf";
        try {
            DocumentFile file = pickedDir.createFile("text/vcard", vfile);
            OutputStream out_stream = context.getContentResolver().openOutputStream(file.getUri());
            ArrayList<String> m_vcard = saveContacts();

            try {

                if(m_vcard.size() > 0){
                    for(int i = 0; i < m_vcard.size(); i++){
                        try {
                            out_stream.write(m_vcard.get(i).getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } finally {
                out_stream.close();
            }

        } catch (IOException e) {
            throw new RuntimeException("Something went wrong : " + e.getMessage(), e);
        }
    }
}
