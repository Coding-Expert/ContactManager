package com.basicphones.contacts.group;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basicphones.contacts.ContactInfo;
import com.basicphones.contacts.ContactDetail;
import com.basicphones.contacts.ContactUtils;
import com.basicphones.contacts.PictureUtils;
import com.basicphones.contacts.R;

import java.util.ArrayList;
import java.util.List;

public class GroupViewFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private RecyclerView mContactRecyclerView;
    private TextView mContactTextView;
    private FloatingActionButton contact_ARU_btn;           //// button to add, remove, update in current group
    private FloatingActionButton delete_btn;
    private String groupId;
    private Group mGroup;
    private String mQuery;
    private ContactAdapter mAdapter;
    public List<ContactInfo> contacts;
    private static int SEND_SMS_REQUEST_CODE = 100;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public static final int WRITE_CONTACT_REQUESTCODE = 124;
    public static final int WRITE_GROUP_REQUESTCODE = 125;
    public List<String> member_rawcontact_id = new ArrayList<>();
    public String rawContactId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        groupId = (String) getArguments().getSerializable(ARG_GROUP_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.groupview_fragment, container, false);
        mContactRecyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContactTextView = (TextView) view
                .findViewById(R.id.empty_view);

        contact_ARU_btn = (FloatingActionButton) view.findViewById(R.id.group);
        contact_ARU_btn.setOnClickListener(contact_aru_listener);

        delete_btn = (FloatingActionButton) view.findViewById(R.id.group_delete);
        delete_btn.setOnClickListener(delete_listener);

//        mGroup = GroupLab.get(getContext()).getGroup(groupId);

        checkPermissions();

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.groupview, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.group_sms:
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    sendSmsToContacts();
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                }

                return true;
//            case R.id.group_email:
//                sendEmailToContacts();
//                return true;
//            case R.id.contact_delete:
//                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//                    deleteContact();
//                    updateUI();
//                }
//                else{
//                    requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUESTCODE);
//                }
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void deleteContact(String rawId, String groupId){
//        if(member_rawcontact_id.size() > 0){
//            for(int i = 0; i < member_rawcontact_id.size(); i++){
//                GroupUtils.deleteContactFromGroup(Long.parseLong(member_rawcontact_id.get(i)), Long.parseLong(groupId), getActivity());
//            }
//            member_rawcontact_id.clear();
//            String msg = "removed contact from group";
//            Toast.makeText(getContext(), msg, msg.length()).show();
//        }
//        else{
//            String msg = "There are no selected contacts";
//            Toast.makeText(getContext(), msg, msg.length()).show();
//        }
        GroupUtils.deleteContactFromGroup(Long.parseLong(rawId), Long.parseLong(groupId), getActivity());
        updateUI();

    }

    public void sendSmsToContacts() {
        String numbersToBeUsed = "";
//        String[] values = {"324234", "234234"};
        if(contacts.size() > 0) {
            for (int i = 0; i < contacts.size(); i++) {
                if(contacts.get(i).getPhones().size() > 0) {
//                    for(int j = 0; j < contacts.get(i).getPhones().size(); j++) {
                        if (numbersToBeUsed.isEmpty()) {
//                    numbersToBeUsed = values[i];
                            numbersToBeUsed = contacts.get(i).getPhones().get(0);
                        } else {
//                            numbersToBeUsed = numbersToBeUsed + "; " + values[i];
                            numbersToBeUsed = numbersToBeUsed + "; " + contacts.get(i).getPhones().get(0);
                        }
//                    }
                }

            }
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + numbersToBeUsed));
//        i.putExtra("address", numbersToBeUsed);
//        i.putExtra("sms_body", "");
//        i.setType("vnd.android-dir/mms-sms");
//        System.out.println(numbersToBeUsed);
            startActivity(intent);
        }

    }
    public void sendEmailToContacts() {
        List<String> email_list = new ArrayList<>();
//        String[] email_list = new String[]{};
        if(contacts.size() > 0) {
            for (int i = 0; i < contacts.size(); i++) {
                if(contacts.get(i).getEmails().size() > 0) {
//                    for(int j = 0; j < contacts.get(i).getEmails().size(); j++) {
                        email_list.add(contacts.get(i).getEmails().get(0));
//                        email_list[i] = contacts.get(i).getEmails().get(j);
//                    }
                }

            }
            String[] email_array = new String[email_list.size()];
            if(email_list.size() > 0){
                for(int i = 0; i < email_list.size(); i++){
                    email_array[i] = email_list.get(i);
                }
            }
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
//            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email_list.to});
            i.putExtra(Intent.EXTRA_EMAIL  , email_array);
            i.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
            i.putExtra(Intent.EXTRA_TEXT   , "BODY");
            startActivity(Intent.createChooser(i, "Send mail..."));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == SEND_SMS_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSmsToContacts();
            }
        }
        if(requestCode == WRITE_CONTACT_REQUESTCODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                deleteContact(rawContactId, groupId);
            }
        }
        if(requestCode == WRITE_GROUP_REQUESTCODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                GroupUtils.deleteGroup(getActivity(), groupId);
                getActivity().finish();
            }
        }

    }


    public static GroupViewFragment newInstance(String groupId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_GROUP_ID, groupId);
        GroupViewFragment fragment = new GroupViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View.OnClickListener contact_aru_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContactSelectActivity.class);
            intent.putExtra("extra_group_id", groupId);
            startActivityForResult(intent, 1);
        }
    };

    private View.OnClickListener delete_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            showConfirmDeleteDialogue();

        }
    };

    private void showConfirmDeleteDialogue() {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Group Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        GroupLab.get(getActivity()).deleteGroup(mGroup);
                        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                            GroupUtils.deleteGroup(getActivity(), groupId);
                            getActivity().finish();
                        }
                        else{
                            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_GROUP_REQUESTCODE);
                        }

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        cancel();
                    }

                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {

            return;
        }

        String returnValue = data.getStringExtra(ContactSelectActivity.RETURN_STATE);
        if(returnValue!=null) {
            if (returnValue.equals("0")){
                Toast.makeText(getActivity(), "not saved",
                        Toast.LENGTH_SHORT).show();
            }
            else if (returnValue.equals("1")) {

                mGroup = (Group) data.getSerializableExtra(ContactSelectActivity.GROUP_OBJECT);
                if(mGroup!=null) {
                    Toast.makeText(getActivity(), "saved",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        updateUI();
    }

    private void updateUI() {
//        ContactLab contactLab = ContactLab.get(getActivity());
        contacts = new ArrayList<>();
        if(mQuery==null) {
//            if (mGroup == null) {
//                return;
//            } else {
//                if (mGroup.getContacts().size() > 0) {
//                    contacts = new ArrayList<>();
//                    for (int i = 0; i < mGroup.getContacts().size(); i++) {
//                        contacts.add(contactLab.getContact(UUID.fromString(mGroup.getContacts().get(i))));
//                    }
//                }
//            }
            contacts = GroupUtils.getContactList(getActivity(), groupId);
//            if(contacts.size() > 0){
//                for(int i = 0; i < contacts.size(); i++){
//                    member_rawcontact_id.add(contacts.get(i).getRaw_contact_id());
//                }
//            }
        }
        else {
//            contacts = contactLab.searchContactsByName(mQuery);
        }

        if (mAdapter == null) {
            mAdapter = new ContactAdapter(contacts);
            mContactRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setContacts(contacts);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mAdapter.getPosition());
        }

//        int contactSize = contactLab.getContacts().size();
        int contactSize = contacts.size();

        if (contacts == null || contacts.size() == 0) {
            mContactRecyclerView.setVisibility(View.GONE);
            mContactTextView.setVisibility(View.VISIBLE);
        }
        else {
            mContactRecyclerView.setVisibility(View.VISIBLE);
            mContactTextView.setVisibility(View.GONE);
        }

    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<ContactInfo> mContacts;

        private int position;

        public ContactAdapter(List<ContactInfo> contacts) {
            mContacts = contacts;
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from( getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_group_view, parent, false);
            return new ContactHolder(view,this);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            ContactInfo contact = mContacts.get(position);
            holder.bindContact(contact);
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public void setContacts(List<ContactInfo> contacts) {
            mContacts = contacts;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    private class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private ImageView mPhotoView;
        private ImageView mCheckBox;
        private ContactInfo mContact;
        private ContactAdapter mAdapter;
        int imageViewWidth=0;
        int imageViewHeight=0;



        public ContactHolder(View itemView, ContactAdapter adaptor ) {
            super(itemView);
            mNameTextView = (TextView)
                    itemView.findViewById(R.id.list_item_contact_name);
            mCheckBox = (ImageView) itemView.findViewById(R.id.select_contact_check);
            mPhotoView = (ImageView)
                    itemView.findViewById(R.id.list_item_contact_photo);
            final ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    imageViewWidth = mPhotoView.getWidth();
                    imageViewHeight = mPhotoView.getHeight();

                    updatePhotoView();

                    //Then remove layoutChange Listener
                    ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
                    vto.removeOnGlobalLayoutListener(this);
                }
            });

//            itemView.setOnClickListener(this);
            mCheckBox.setOnClickListener(deletecontact_listener);
            mAdapter=adaptor;

        }

        public void bindContact(ContactInfo contact) {
            mContact = contact;
            mNameTextView.setText(mContact.getName());
            updatePhotoView();

        }
        private void updatePhotoView()
        {
            if(imageViewWidth!=0 && imageViewHeight!=0) {
//                File mPhotoFile = ContactLab.get(getActivity()).getPhotoFile(mContact);
                Bitmap image = ContactUtils.getPhoto(Long.parseLong(mContact.getmContact_id()), getActivity());
                if (image == null) {
                    String name = mContact.getName();
                    if (name != null && !name.isEmpty()) {
                        String initials = String.valueOf(name.charAt(0));
                        //String char2 = String.valueOf(name.substring(name.indexOf(' ') + 1).charAt(0));
                        //if(char2!=null && !char2.isEmpty())
                        // initials = initials + char2;
                        Bitmap bitmap = PictureUtils.generateCircleBitmap(getContext(),
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

        @Override
        public void onClick(View v) {

//            mAdapter.setPosition(getAdapterPosition());
//            Intent intent = ContactViewActivity.newIntent(getActivity(), mContact.getId());
            Intent intent = new Intent(getActivity(), ContactDetail.class);
//            intent.putExtra("extra_contact_id", mContact.getmContact_id());
            intent.putExtra("extra_contact", mContact);
//            startActivity(intent);
        }

        private View.OnClickListener deletecontact_listener = new View.OnClickListener(){
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    member_rawcontact_id.add(mContact.getRaw_contact_id());
//                    String msg = "checked";
//                    Toast.makeText(getContext(), msg, msg.length()).show();
//                }
//                else{
//                    member_rawcontact_id.remove(mContact.getRaw_contact_id());
//                    String msg = "unchecked";
//                    Toast.makeText(getContext(), msg, msg.length()).show();
//                }
//            }
            @Override
            public void onClick(View v) {

                showConfirmContactDeleteDialogue(mContact.getRaw_contact_id(), groupId);
            }
        };
    }

    private void showConfirmContactDeleteDialogue(final String rawId, final String groupId) {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Contact Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rawContactId = rawId;
                        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                            deleteContact(rawContactId, groupId);
                        }
                        else{
                            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUESTCODE);
                        }

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        cancel();
                    }

                })
                .show();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkStoragePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int checkSendSmsPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.SEND_SMS);
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CALL_PHONE);
            if (checkStoragePermission + checkSendSmsPermission + checkCallPhonePermission
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.SEND_SMS) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.CALL_PHONE)) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.CALL_PHONE},
                            PERMISSIONS_MULTIPLE_REQUEST);
                }
            }
        }
    }

}
