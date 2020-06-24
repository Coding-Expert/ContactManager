package com.basicphones.contacts.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import jagerfield.mobilecontactslibrary.Contact.Contact;
import jagerfield.mobilecontactslibrary.ElementContainers.EmailContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.NumberContainer;
import jagerfield.mobilecontactslibrary.ImportContactsAsync;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basicphones.contacts.ContactInfo;
import com.basicphones.contacts.ContactUtils;
import com.basicphones.contacts.PictureUtils;
import com.basicphones.contacts.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ContactSelectActivity extends AppCompatActivity {


    public static final String RETURN_STATE = "groupState";
    public static final String GROUP_OBJECT = "contactObject";

    private Toolbar mTopToolbar;
    private TextView title_view;
    private ImageView back;
    private Button cancel_btn;
    private Button save_btn;
    private RecyclerView mContactRecyclerView;
    private TextView mGroupTextView;
    private String mQuery;
    private ContactSelect_Adapter mAdapter;
    private TextView mContactTextView;
    private String group_id;
    private Group mGroup;
    public List<ContactInfo> group_member_contact = new ArrayList<>();
    public List<String> member_contact_id = new ArrayList<>();
    public List<String> selected_contactId_list = new ArrayList<>();
    public List<ContactInfo> all_contact = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        group_id = (String) getIntent().getSerializableExtra("extra_group_id");
//        mGroup = GroupLab.get(this).getGroup(group_id);
//        selected_contactId_list = mGroup.getContacts();
        group_member_contact = GroupUtils.getContactList(this, group_id);
        if(group_member_contact.size() > 0){
            for(int i = 0; i < group_member_contact.size(); i++) {
                member_contact_id.add(group_member_contact.get(i).getmContact_id());
            }
            selected_contactId_list = member_contact_id;
        }

        title_view = (TextView) findViewById(R.id.toolbar_title);
        title_view.setText("Select Contact");

//        save_btn = (Button) findViewById(R.id.save);
//        save_btn.setOnClickListener(save_listener);
//        cancel_btn = (Button) findViewById(R.id.cancel);
//        cancel_btn.setOnClickListener(cancel_listener);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backlistener);

        mContactRecyclerView = (RecyclerView) findViewById(R.id.group_recycler_view);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupTextView = (TextView) findViewById(R.id.empty_view);

        mContactTextView = (TextView) findViewById(R.id.empty_view);

//        updateUI();
        getContactDataFromPhone();
    }

    public void getContactDataFromPhone(){
        new ImportContactsAsync(this, new ImportContactsAsync.ICallback()
        {
            @Override
            public void mobileContacts(ArrayList<Contact> contactList)
            {
                ArrayList<Contact> listItem = contactList;
                List<ContactInfo> contactInfoList = new ArrayList<>();
                for(int k = 0; k < listItem.size(); k++) {
                    Contact temp_contact = listItem.get(k);
                    LinkedList<NumberContainer> number = temp_contact.getNumbers();
                    LinkedList<EmailContainer> email = temp_contact.getEmails();

                    ContactInfo data = new ContactInfo();
                    data.setmContact_id(String.valueOf(temp_contact.getId()));

                    List<String> phone_list = new ArrayList<>();
                    for (int i = 0; i < number.size(); i++) {
                        String phone_number = number.get(i).elementValue();
                        phone_list.add(phone_number);
                    }
                    if(temp_contact.getDisplaydName() == null || temp_contact.getDisplaydName().isEmpty()){
                        data.setName(String.valueOf(number.get(0).elementValue()));
                    }
                    else{
                        data.setName(temp_contact.getDisplaydName());
                    }
                    data.setPhones(phone_list);
                    List<String> email_list = new ArrayList<>();
                    for (int j = 0; j < email.size(); j++) {
                        String email_string = email.get(j).getEmail();
                        email_list.add(email_string);
                    }
                    data.setEmails(email_list);
                    contactInfoList.add(data);
                }
                all_contact = contactInfoList;
                updateUI();
            }
        }).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateUI();
        getContactDataFromPhone();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contactselect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
//        ContactLab contactLab = ContactLab.get(getApplicationContext());
        List<ContactInfo> contacts = new ArrayList<>();
        if(mQuery==null) {
//            contacts = contactLab.getContacts();
            contacts = all_contact; //ContactUtils.getContactsFromPhone(this);
        }
        else {
//            contacts = contactLab.searchContactsByName(mQuery);
        }
        if (mAdapter == null) {
            mAdapter = new ContactSelect_Adapter(contacts);
            mAdapter.setContext(this);
            mContactRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setContacts(contacts);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mAdapter.getPosition());
        }

//        int contactSize = contactLab.getContacts().size();
        int contactSize = contacts.size();

        if (contactSize==0) {
            mContactRecyclerView.setVisibility(View.GONE);
            mContactTextView.setVisibility(View.VISIBLE);
//            save_btn.setVisibility(View.INVISIBLE);
//            cancel_btn.setVisibility(View.INVISIBLE);
        }
        else {
            mContactRecyclerView.setVisibility(View.VISIBLE);
            mContactTextView.setVisibility(View.GONE);
//            save_btn.setVisibility(View.VISIBLE);
//            cancel_btn.setVisibility(View.VISIBLE);
        }

    }

    private View.OnClickListener backlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            goPreviousScreen();
        }
    };

    public void goPreviousScreen() {
        super.onBackPressed();
    }

    private View.OnClickListener cancel_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            goPreviousScreen();
        }
    };

    private class ContactSelect_Adapter extends RecyclerView.Adapter<ContactSelectHolder> {

        private List<ContactInfo> mContacts;
        private Context mContext;

        private int position;

        public ContactSelect_Adapter(List<ContactInfo> contacts) {
            mContacts = contacts;
        }

        @Override
        public ContactSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from( getApplicationContext());
            View view = layoutInflater
                    .inflate(R.layout.list_select_contact, parent, false);
            return new ContactSelectHolder(view,this);
        }

        @Override
        public void onBindViewHolder(ContactSelectHolder holder, int position) {
            ContactInfo contact = mContacts.get(position);
            holder.bindContact(contact, mContext);
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public void setContacts(List<ContactInfo> contacts) {
            mContacts = contacts;
        }
        public void setContext(Context context) {
            mContext = context;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    private class ContactSelectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private ImageView mPhotoView;
        private CheckBox mSolvedCheckBox;
        private ContactInfo mContact;
        private ContactSelect_Adapter mAdapter;
        int imageViewWidth=0;
        int imageViewHeight=0;
        private Context context;



        public ContactSelectHolder(View itemView, ContactSelect_Adapter adaptor ) {
            super(itemView);
            mNameTextView = (TextView)
                    itemView.findViewById(R.id.list_item_contact_name);
            mPhotoView = (ImageView)
                    itemView.findViewById(R.id.list_item_contact_photo);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_contact_check);
            final ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {

                    imageViewWidth = mPhotoView.getMeasuredWidth();
                    imageViewHeight = mPhotoView.getMeasuredHeight();

                    updatePhotoView();
                    mPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });

            itemView.setOnClickListener(this);
            mSolvedCheckBox.setOnCheckedChangeListener(check_listener);
            mAdapter=adaptor;
        }

        public void bindContact(ContactInfo contact, Context mcontext) {
            mContact = contact;
            context = mcontext;
            mNameTextView.setText(mContact.getName());
            updatePhotoView();
            updateCheckBox();

        }
        private void updatePhotoView()
        {
            if(imageViewWidth!=0 && imageViewHeight!=0) {
//                File mPhotoFile = ContactLab.get(getApplicationContext()).getPhotoFile(mContact);
                Bitmap image = ContactUtils.getPhoto(Long.parseLong(mContact.getmContact_id()),ContactSelectActivity.this);
                if (image == null) {
                    String name = mContact.getName();
                    if (name != null && !name.isEmpty()) {
                        String initials = String.valueOf(name.charAt(0));
                        //String char2 = String.valueOf(name.substring(name.indexOf(' ') + 1).charAt(0));
                        //if(char2!=null && !char2.isEmpty())
                        // initials = initials + char2;
                        Bitmap bitmap = PictureUtils.generateCircleBitmap(getApplicationContext(),
                                PictureUtils.getMaterialColor(name),
                                40, initials);
                        // + String.valueOf())
                        mPhotoView.setImageBitmap(bitmap);
                    } else
                        mPhotoView.setImageDrawable(null);

                } else {
                    Bitmap photo_bitmap = PictureUtils.getCroppedBitmap(image, imageViewWidth, imageViewHeight);
                    mPhotoView.setImageBitmap(photo_bitmap);
                }
            }

        }

        private void updateCheckBox() {
            if(member_contact_id.contains(mContact.getmContact_id())) {
                mSolvedCheckBox.setChecked(true);
            }
            else{
                mSolvedCheckBox.setChecked(false);
            }
        }

        @Override
        public void onClick(View v) {

//            mAdapter.setPosition(getAdapterPosition());
////            Intent intent = ContactViewActivity.newIntent(getActivity(), mContact.getId());
//            Intent intent = new Intent(this, ContactDetail.class);
//            intent.putExtra("extra_contact_id", mContact.getId());
//            startActivity(intent);
        }
        private CompoundButton.OnCheckedChangeListener check_listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(!member_contact_id.contains(mContact.getmContact_id())) {
                        member_contact_id.add(mContact.getmContact_id());
                    }
//                    String msg = "checked";
//                    Toast.makeText(getApplicationContext(), msg, msg.length()).show();
                }
                else{
                    if(member_contact_id.contains(mContact.getmContact_id())) {
                        member_contact_id.remove(mContact.getmContact_id());
                    }
//                    String msg = "unchecked";
//                    Toast.makeText(getApplicationContext(), msg, msg.length()).show();
                }
            }
        };

    }

    private View.OnClickListener save_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            save();
        }
    };
    public void save() {
        if(group_member_contact.size() > 0){
            for(int i = 0; i < group_member_contact.size(); i++) {
                GroupUtils.deleteContactFromGroup(Long.parseLong(group_member_contact.get(i).getRaw_contact_id()), Long.parseLong(group_id), ContactSelectActivity.this);
            }
        }
        if(member_contact_id.size() > 0) {
            for(int i = 0; i < member_contact_id.size(); i++) {
                GroupUtils.addToGroup(member_contact_id.get(i), group_id, ContactSelectActivity.this);
            }
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RETURN_STATE, "1");
        resultIntent.putExtra(GROUP_OBJECT, mGroup);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
