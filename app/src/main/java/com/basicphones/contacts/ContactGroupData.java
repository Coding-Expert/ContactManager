package com.basicphones.contacts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.basicphones.contacts.group.Group;
import com.basicphones.contacts.group.GroupUtils;
import com.basicphones.contacts.group.NewGroupFragment;

import java.util.List;

public class ContactGroupData extends AsyncTask<Object, Integer, Void> {

    ProgressDialog progressDialog;
    public Context context;
    public Activity m_activity;
    public int tab_index;
    public List<ContactInfo> contactInfoList;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(tab_index == 0) {
            progressDialog = ProgressDialog.show(m_activity, "", "loading groups");
        }
        else{
            progressDialog = ProgressDialog.show(m_activity, "", "loading contacts");
        }

    }

    @Override
    protected Void doInBackground(Object... objects) {
//        m_activity.loadingContactandGroup(tab_index);
        if(tab_index == 0) {
//            List<ContactInfo> contacts = ContactUtils.getContactsFromPhone(m_activity);
            NewContactFragment.getInstance().getContactDataFromPhone();
        }
        else{
            List<Group> groups = GroupUtils.getGroupList(m_activity);
            if(groups.size() > 0){
                NewGroupFragment.getInstance().all_group = groups;
            }
        }
        return null;
    }

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
        if(tab_index == 0) {
//            NewContactFragment.getInstance().updateUI1();
        }
        else{
            NewGroupFragment.getInstance().updateUI();
        }
    }
    public void setIndex(Activity activity, int index){
        this.tab_index = index;
        m_activity = activity;
    }

}
