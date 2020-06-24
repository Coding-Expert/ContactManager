package com.basicphones.contacts;

import android.Manifest;
import android.content.ContentUris;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContactViewFragment extends Fragment {

    private ContactInfo mContact;
    private File mPhotoFile;
    private TextView mName;
    private List<TextView> mPhones;
    List<String> phones;
    private List<TextView> mEmails;
    List<String> emails;
    private ImageView mPhotoView;
    private LinearLayout mPhonesLayout;
    private LinearLayout mPhonesLayout1;
    private LinearLayout mEmailsLayout;
    private LinearLayout mEmailsLayout1;
    int imageViewWidth=0;
    int imageViewHeight=0;


    private static final String ARG_CONTACT_ID = "contact_id";
    private static final String DIALOG_CONTACT_IMAGE = "DialogContactImage";

    private Button edit_btn;
    private Button delete_btn;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private static final int SEND_SMS_REQUEST_CODE = 124;
    private static final int WRITE_CONTACT_REQUEST_CODE = 125;
    private Toolbar mTopToolbar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setHasOptionsMenu(true);

//        UUID contactId = (UUID) getArguments().getSerializable(ARG_CONTACT_ID);
//        mContact = ContactLab.get(getActivity()).getContact(contactId);
//        mPhotoFile = ContactLab.get(getActivity()).getPhotoFile(mContact);

          mContact = (ContactInfo) getArguments().getSerializable(ARG_CONTACT_ID);
//        mContact = ContactLab.get(getActivity()).getContact(contact);
//        mPhotoFile = ContactLab.get(getActivity()).getPhotoFile(mContact);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_view, container, false);

//        mTopToolbar = (Toolbar) getActivity().findViewById(R.id.tabs);
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(mTopToolbar);


        mName = (TextView) v.findViewById(R.id.view_contact_name);


        mPhotoView = (ImageView) v.findViewById(R.id.view_contact_photo);
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


        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile != null && mPhotoFile.exists()) {
                    FragmentManager manager = getFragmentManager();
                    ContactImageFragment dialog = ContactImageFragment
                            .newInstance(mPhotoFile.getPath());
                    dialog.show(manager, DIALOG_CONTACT_IMAGE);
                }
            }
        });


        mPhonesLayout = (LinearLayout) v.findViewById(R.id.view_contact_phones_list);

        mEmailsLayout = (LinearLayout) v.findViewById(R.id.view_contact_emails_list);
//        mEmailsLayout1 = (LinearLayout) v.findViewById(R.id.view_contact_emails);

//        edit_btn = (Button) v.findViewById(R.id.edit);
//        edit_btn.setOnClickListener(editlistener);
//        delete_btn = (Button) v.findViewById(R.id.delete);
//        delete_btn.setOnClickListener(deletelistener);

        checkPermissions();

        return v;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
            }
            case WRITE_CONTACT_REQUEST_CODE:{
                ContactUtils.deleteContact(getActivity(), mContact.getmContact_id());
                getActivity().finish();
                break;
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contactview, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = ContactEditActivity.newIntent(getActivity(), mContact, ContactEditFragment.UPDATE_CONTACT);
                startActivityForResult(intent,ContactEditFragment.UPDATE_CONTACT);
                return true;
            case R.id.delete:
                showConfirmDeleteDialogue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUI()
    {
        if(mContact!=null)
        {
            mName.setText(mContact.getName());

            mPhonesLayout.removeAllViews();
            phones = mContact.getPhones();
            mPhones = new ArrayList<>();
            for(int i=0;i!=phones.size();i++)
            {
                addPhoneTextView(phones.get(i));
            }

            mEmailsLayout.removeAllViews();
            emails = mContact.getEmails();
            mEmails = new ArrayList<>();
            for(int i=0;i!=emails.size();i++)
            {
                addEmailTextView(emails.get(i));
            }

            if(imageViewWidth!=0 && imageViewHeight!=0)
                updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (getPhoto(Long.parseLong(mContact.getmContact_id())) != null) {
            Bitmap photo_bitmap = PictureUtils.getCroppedBitmap(getPhoto(Long.parseLong(mContact.getmContact_id())), imageViewWidth, imageViewHeight);
            mPhotoView.setImageBitmap(photo_bitmap);
        } else {
            mPhotoView.setImageResource(R.drawable.layerlist);
        }
    }

    public Bitmap getPhoto(long contactId) {                    ///////     get photo from phone contact using contact id
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getActivity().getContentResolver().query(photoUri,
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

    private void addPhoneTextView(String text)
    {
        TextView phoneTextView = new TextView(getContext());
        mPhones.add(phoneTextView);
        int i = mPhones.size()-1;
        mPhones.get(i).setText(text);
        //mPhones.get(i).setHeight(30);
        mPhones.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        mPhones.get(i).setPadding(0,0,0,0);
        mPhones.get(i).setId(i);
        mPhones.get(i).setTextColor(Color.WHITE);
        mPhones.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                   //////  call to phone number

//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:" + mPhones.get(v.getId()).getText().toString().trim()));
//
//
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                    startActivity(intent);
//                } else {
//                    Toast toast = Toast.makeText(getActivity(), R.string.alert_no_call_perm, Toast.LENGTH_LONG);
//                    toast.show();
//                }

            Intent callIntent = new Intent(Intent.ACTION_CALL);

            callIntent.setData(Uri.parse("tel:" + mPhones.get(v.getId()).getText().toString().trim()));
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                Toast toast = Toast.makeText(getActivity(), R.string.alert_no_call_perm, Toast.LENGTH_LONG);
                toast.show();
            }
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        mPhones.get(i).setLayoutParams(params);

        ImageView sms_imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 0, 0);
        sms_imageView.setLayoutParams(params1);
        sms_imageView.setId(i);
        sms_imageView.setImageResource(R.drawable.sms);
        sms_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPhones.get(v.getId()).getText().toString().isEmpty()) {
                    if(ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendSMS(mPhones.get(v.getId()).getText().toString());
                    }
                    else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                    }
                }
            }
        });


        ImageView call_imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 25, 0);
        call_imageView.setLayoutParams(params2);
        call_imageView.setImageResource(R.drawable.call_1);


        mPhonesLayout1 = new LinearLayout(getContext());
        mPhonesLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mPhonesLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mPhonesLayout1.setPadding(7,0,7,0);
        mPhonesLayout1.addView(mPhones.get(i));
        mPhonesLayout1.addView(sms_imageView);
//        mPhonesLayout1.addView(call_imageView);
        mPhonesLayout.addView(mPhonesLayout1);


    }
    String[] sms = {"123213", "213213"};
    public void sendSMS(String phone_number) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone_number.trim()));
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + sms));
        smsIntent.putExtra("sms_body", "");
        startActivity(smsIntent);
    }

    private void addEmailTextView(String text)
    {
        TextView emailTextView = new TextView(getContext());
        mEmails.add(emailTextView);
        int i = mEmails.size()-1;
        mEmails.get(i).setText(text);
        mEmails.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        mEmails.get(i).setPadding(0,0,10,0);
        mEmails.get(i).setId(i);
        mEmails.get(i).setTextColor(Color.WHITE);
        mEmails.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       //////// send email to selected email

                String[] TO = {mEmails.get(v.getId()).getText().toString().trim()};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//                intent.setType("message/rfc822");
//                intent.putExtra(Intent.EXTRA_EMAIL, mEmails.get(v.getId()).getText().toString().trim());
                intent.putExtra(Intent.EXTRA_EMAIL, TO);
                intent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
                intent.putExtra(Intent.EXTRA_TEXT   , "BODY");

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivity(intent);
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        mEmails.get(i).setLayoutParams(params);


        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 25, 0);
        imageView.setLayoutParams(params1);
        imageView.setImageResource(R.drawable.email_1);


        mEmailsLayout1 = new LinearLayout(getContext());
        mEmailsLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mEmailsLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mEmailsLayout1.setPadding(7,0,10,0);
        mEmailsLayout1.addView(mEmails.get(i));
//        mEmailsLayout1.addView(imageView);
        mEmailsLayout.addView(mEmailsLayout1);

    }

    private ShapeDrawable getBorderDrawable()
    {
        ShapeDrawable sd = new ShapeDrawable();
        sd.setShape(new RectShape());
        sd.getPaint().setColor(Color.LTGRAY);
        sd.getPaint().setStrokeWidth(10f);
        sd.getPaint().setStyle(Paint.Style.STROKE);
        return sd;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ContactEditFragment.UPDATE_CONTACT) {

            String returnValue = data.getStringExtra(ContactEditFragment.RETURN_STATE);
            if(returnValue!=null) {
                if (returnValue.equals("0")){
                    Toast.makeText(getActivity(), "cancelled",
                            Toast.LENGTH_SHORT).show();
                }
                else if (returnValue.equals("1")) {
                    ContactInfo contact = (ContactInfo) data.getSerializableExtra(ContactEditFragment.CONTACT_OBJECT);
                    if(contact!=null) {
                        Toast.makeText(getActivity(), "saved",
                                Toast.LENGTH_SHORT).show();
                        mContact=contact;
                    }
                }
            }
        }

    }

    public static ContactViewFragment newInstance(ContactInfo contact) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT_ID, contact);
        ContactViewFragment fragment = new ContactViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void showConfirmDeleteDialogue() {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ContactLab.get(getActivity()).deleteContact(mContact);      /// remove contact in database.
                        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            ContactUtils.deleteContact(getActivity(), mContact.getmContact_id());
                            getActivity().finish();
                        }
                        else {

                            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUEST_CODE);
                        }

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();
    }

    private View.OnClickListener editlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = ContactEditActivity.newIntent(getActivity(), mContact, ContactEditFragment.UPDATE_CONTACT);
            startActivityForResult(intent,ContactEditFragment.UPDATE_CONTACT);
        }
    };

    private View.OnClickListener deletelistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showConfirmDeleteDialogue();
        }
    };



}
