package com.basicphones.contacts;

import android.Manifest;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ContactEditFragment extends Fragment {

    private ContactInfo mContact;
    private int mRequestType;
    private File mTempPhotoFile;
    private String mTempPhotoName;
    private File mContactPhotoFile;
    private String mContactPhotoName;
    private String pictureStoragePath;
    private ExtendedEditText mName;
    String name;
    private ImageView mPhotoView;
    private ImageButton mPhotoAdd;
    private ImageButton mPhotoDelete;
    private List<ExtendedEditText> mPhones;
    List<String> phones;
    private Button mAddPhoneButton;
    private List<ExtendedEditText> mEmails;
    List<String> emails;
    private Button mAddEmailButton;

    private LinearLayout mPhonesLayout;
    private LinearLayout mEmailsLayout;
    private LinearLayout mPhoneDeleteLayout;
    private LinearLayout mEmailDeleteLayout;


    int imageViewWidth=0;
    int imageViewHeight=0;


    private static final String ARG_CONTACT = "contact";
    private static final String ARG_REQUEST_TYPE = "request_type";
    private static final String DIALOG_CONTACT_IMAGE = "DialogContactImage";
    public static final String CONTACT_OBJECT = "contactObject";
    private static final int REQUEST_PHOTO= 0;
    public static final int ADD_CONTACT = 0;
    public static final int UPDATE_CONTACT = 1;
    public static final String RETURN_STATE = "contactState";
    private static boolean deletePhoto;
    private static boolean addPhoto = false;
    private static final String TEMP_IMAGE_NAME = "tempImage";
    private static final int EXTERNAL_PERMISSION_CODE = 1234;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int WRITE_CONTACT_REQUEST_CODE = 101;
    private boolean isCamera = true, isGallery = true;
    private ImagePicker imagePicker;
    private Bitmap bitmap_image;
    private ContactInfo old_contact;
    private boolean old_photo_flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mContact = (ContactInfo) getArguments().getSerializable(ARG_CONTACT);
        mRequestType = (int) getArguments().getSerializable(ARG_REQUEST_TYPE);

        if (savedInstanceState != null) {
            mContact = (ContactInfo) savedInstanceState.getSerializable(ARG_CONTACT);
            mRequestType = (int) savedInstanceState.getSerializable(ARG_REQUEST_TYPE);
        }

        if(mContact==null)
            getActivity().finish();

        mTempPhotoFile = null;
        mContactPhotoFile = null;
        File appDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(appDir!=null)
            if (appDir.exists()) {
                pictureStoragePath = appDir.getAbsolutePath() + File.separator;
                mTempPhotoFile = new File(appDir.getAbsolutePath() + File.separator + "temp.png");
                mContactPhotoFile = new File(appDir.getAbsolutePath() + File.separator + mContact.getPhotoFilename());
                mTempPhotoName = "temp.png";
                mContactPhotoName = mContact.getPhotoFilename();
            }
        }

    @Override
    public void onResume() {
        super.onResume();
        if(getView() == null){
            return;
        }

        overrideBackButton();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_edit, container, false);

        if(mRequestType == 1){
            old_contact = mContact;
        }
        mName = (ExtendedEditText) v.findViewById(R.id.edit_contact_name);
        name=mContact.getName();
        mName.setText(name);
        setEditTextKeyImeChangeListener(mName);
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name=s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        //copy contacts photo to temp.png if it exists
        if ( mContactPhotoFile != null && mContactPhotoFile.exists()) {
            PictureUtils.copyFile(pictureStoragePath, mContactPhotoName, pictureStoragePath , mTempPhotoName);
        }


        mPhotoView = (ImageView) v.findViewById(R.id.edit_contact_photo);
        final ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                imageViewWidth = mPhotoView.getWidth();
                imageViewHeight = mPhotoView.getHeight();
                deletePhoto = false;
                updatePhotoView();

                //Then remove layoutChange Listener
                ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
                vto.removeOnGlobalLayoutListener(this);
            }
        });


        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTempPhotoFile != null && mTempPhotoFile.exists()) {
                    FragmentManager manager = getFragmentManager();
                    ContactImageFragment dialog = ContactImageFragment
                            .newInstance(mTempPhotoFile.getAbsolutePath());
                    dialog.show(manager, DIALOG_CONTACT_IMAGE);
                }
            }
        });


        final Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        deletePhoto =false;
        mPhotoAdd = (ImageButton) v.findViewById(R.id.edit_contact_photo_add);
        mPhotoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(pickImage, REQUEST_PHOTO);
                addImage();

            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickImage,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mPhotoAdd.setEnabled(false);
        }

        mPhotoDelete = (ImageButton) v.findViewById(R.id.edit_contact_photo_delete);
        mPhotoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhoto=true;
                old_photo_flag = true;
//                PictureUtils.deleteFile(pictureStoragePath, mTempPhotoName);
//                updatePhotoView();
                bitmap_image = null;
//                mPhotoView.setImageBitmap(bitmap_image);
                mPhotoView.setImageResource(R.drawable.layerlist);
            }
        });

        mPhonesLayout = (LinearLayout) v.findViewById(R.id.edit_contact_phones_list);
        phones = mContact.getPhones();
        mPhones = new ArrayList<>();
        if(phones.size()>0) {
            for (int i = 0; i != phones.size(); i++) {
                addPhoneEditText(phones.get(i));
            }
        }
        else
        {
            addPhoneEditText("");
            phones.add("");
        }

        mAddPhoneButton = (Button)v.findViewById(R.id.edit_contact_add_phone_button);
        mAddPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhones.size()<=10) {
                    addPhoneEditText("");
                    phones.add("");
                }
                else
                    Toast.makeText(getActivity(), "Max limit is 10",
                            Toast.LENGTH_LONG).show();
            }
        });


        mEmailsLayout = (LinearLayout) v.findViewById(R.id.edit_contact_emails_list);
        emails = mContact.getEmails();
        mEmails = new ArrayList<>();
        if(emails.size()>0) {
            for (int i = 0; i != emails.size(); i++) {
                addEmailEditText(emails.get(i));
            }
        }

        else {
            addEmailEditText("");
            emails.add("");
        }

        mAddEmailButton = (Button)v.findViewById(R.id.edit_contact_add_email_button);
        mAddEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEmails.size()<=10) {
                    addEmailEditText("");
                    emails.add("");
                }
                else {
                    Toast.makeText(getActivity(), "Max limit is 10",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

//        save_btn = (Button) v.findViewById(R.id.save);
//        save_btn.setOnClickListener(savelistener);
//        cancel_btn = (Button) v.findViewById(R.id.cancel);
//        cancel_btn.setOnClickListener(cancellistener);

        checkPermission();

        return v;
    }

    public void addImage() {                //////////      add photo to imageview from phone using camera or gallery
        imagePicker = new ImagePicker();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        else {
            imagePicker.withFragment(this).withCompression(true).start();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contactadd, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if(validate())
                    showConfirmSaveDialogue();
                return true;
            case R.id.cancel:
                cancel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validate() {            //////          validation check section
        boolean valid = true;
        boolean validBefore=true;

        //check name validity
        //p{L} matches any kind of letter from any language.
        if (name.isEmpty() || (!name.isEmpty() && !name.matches("^[\\p{L} .'-{0-9}]+$"))) {
            mName.setError("valid characters include (a-z)(0-9)-_'.()");
            valid = false;
        } else if (name.length() > 25) {
            mName.setError("name can only be 25 chars");
            valid = false;
        } else
            mName.setError(null);

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid contact name", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i != mPhones.size(); i++) {
            String phone = phones.get(i);
            //check phone validity
            if (!phone.isEmpty() && !android.util.Patterns.PHONE.matcher(phone).matches()) {
                mPhones.get(i).setError("only standard phone number format is valid");
                valid = false;
            }
            if (phone.length() > 25) {
                mPhones.get(i).setError("phone number can only be 25 chars");
                valid = false;
            } else
                mPhones.get(i).setError(null);
        }

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter valid phone numbers", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i != mEmails.size(); i++) {
            String email = emails.get(i);

            //check email validity
            if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmails.get(i).setError("only standard email format is valid");
                valid = false;
            } else if (email.length() > 25) {
                mEmails.get(i).setError("email can only be 25 chars");
                valid = false;
            } else
                mEmails.get(i).setError(null);
        }

        if(!valid && validBefore) {
            validBefore=false;
            Toast.makeText(getActivity(), "enter a valid email address", Toast.LENGTH_SHORT).show();
        }

        if (valid)
            return true;
        else
            return false;
    }


    private void save() {                       //  save new contact to phone contact
        mContact.setName(name);
        List<String> newPhones = new ArrayList<>();

        for (int i = 0; i != phones.size(); i++) {
            if(!phones.get(i).isEmpty())
                newPhones.add(phones.get(i));
        }
        mContact.setPhones(newPhones);

        List<String> newEmails = new ArrayList<>();

        for (int i = 0; i != emails.size(); i++) {
            if(!emails.get(i).isEmpty())
                newEmails.add(emails.get(i));
        }
        mContact.setEmails(newEmails);

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if(mRequestType == 0) {

                ContactUtils.saveContactInPhone(mContact, getActivity(), getSavingImage());
//                PictureUtils.deleteFile(pictureStoragePath, mTempPhotoName);
            }
            else {
                ContactUtils.updateContact(mContact, getActivity(), getUpdateImage());
            }
            finishActivity();

        }
        else {

            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUEST_CODE);
        }

    }
    public Bitmap getSavingImage() {
        if(bitmap_image == null){
            return null;
        }
        else{
            bitmap_image = PictureUtils.getScaledBitmap(mTempPhotoFile.getAbsolutePath(), imageViewWidth, imageViewHeight);
        }
        return bitmap_image;
    }
    public Bitmap getUpdateImage() {
        if(!old_photo_flag){
            bitmap_image = ContactUtils.getPhoto(Long.parseLong(mContact.getmContact_id()), getActivity());
            return bitmap_image;
        }
        else{
            if(bitmap_image == null){
                return bitmap_image;
            }
            else{
                bitmap_image = PictureUtils.getCircularBitmap(mTempPhotoFile.getAbsolutePath(), 80, imageViewWidth, imageViewHeight);
            }
        }
        return bitmap_image;
    }

    private void cancel() {
        //delete temp.png if it exists
        if ( mTempPhotoFile != null && mTempPhotoFile.exists()) {
            PictureUtils.deleteFile(pictureStoragePath, mTempPhotoName);
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RETURN_STATE, "0");
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
        getActivity().finish();
    }

    private void updatePhotoView() {                /////////// update photo using camera or gallery
        if(mRequestType == 0) {
            if(!deletePhoto){
                mPhotoView.setImageResource(R.drawable.layerlist);
            }
            if(addPhoto){
                bitmap_image = PictureUtils.getCircularBitmap(
                        mTempPhotoFile.getAbsolutePath(), 80, imageViewWidth, imageViewHeight);
                mPhotoView.setImageBitmap(bitmap_image);
                addPhoto = false;
            }

        }
        else {
            if(!addPhoto && !deletePhoto){
                Bitmap photo = ContactUtils.getPhoto(Long.parseLong(mContact.getmContact_id()), getActivity());
//                Bitmap image = PictureUtils.getCroppedBitmap(ContactUtils.getPhoto(Long.parseLong(mContact.getmContact_id()), getActivity()), imageViewWidth, imageViewHeight);
                if(photo != null){
                    bitmap_image = PictureUtils.getCroppedBitmap(photo, imageViewWidth, imageViewHeight);;
                    mPhotoView.setImageBitmap(bitmap_image);
                }
                else{
                    mPhotoView.setImageResource(R.drawable.layerlist);
                }
            }
            if(addPhoto){
                bitmap_image = PictureUtils.getCircularBitmap(
                        mTempPhotoFile.getAbsolutePath(), 80, imageViewWidth, imageViewHeight);
                mPhotoView.setImageBitmap(bitmap_image);
                addPhoto = false;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            addPhoto = true;
            old_photo_flag = true;
            imagePicker.addOnCompressListener(new ImageCompressionListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompressed(String filePath) {
//                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
//                    imageView.setImageBitmap(selectedImage);
                    getBitmapImage(filePath);


                }
            });
            String filePath = imagePicker.getImageFilePath(data);
            if (filePath != null) {
                Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
//                imageView.setImageBitmap(selectedImage);
            }
        }
    }
    public void getBitmapImage(String path) {                   //// get bitmap of image file from phone using camera or gallery
        Bitmap image = PictureUtils.getScaledBitmap(path, imageViewWidth, imageViewHeight);
        try {
            if(mTempPhotoFile != null) {
                try (FileOutputStream out = new FileOutputStream(mTempPhotoFile)) {
                    image.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
                addPhoto = true;
                updatePhotoView();
                deletePhoto=false;
            }
            else {
                Toast.makeText(getActivity(), "Sorry, Picture Storage Support not available",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException ioException) {

        }

    }

    private void setEditTextKeyImeChangeListener(final ExtendedEditText extendedEditText)
    {
        extendedEditText.setKeyImeChangeListener(new ExtendedEditText.KeyImeChange(){
            @Override
            public boolean onKeyIme(int keyCode, KeyEvent event)
            {
                if (event.getAction()==KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK )
                {
                    extendedEditText.clearFocus();
                    View view = getActivity().getCurrentFocus();

                    if (view != null) {
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0)) //if keyboard was opened then only validate without showing save prompt
                            validate();
                        else { //if keyboard was already closed then show save prompt if validation passed
                            if (validate())
                                showConfirmSaveDialogue();
                        }
                    }
                    //Toast.makeText(getActivity(), "back", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                    return false;
            }});
    }

    private void addPhoneEditText(String text)
    {
        ExtendedEditText phoneEditText = new ExtendedEditText(getContext());
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneEditText.setTextColor(Color.WHITE);
        ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
        ViewCompat.setBackgroundTintList(phoneEditText,colorStateList);
        mPhones.add(phoneEditText);
        int i = mPhones.size()-1;
        mPhones.get(i).setText(text);
        mPhones.get(i).setId(i);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f);
        params1.setMargins(0, 0, 10, 0);
        mPhones.get(i).setLayoutParams(params1);


        mPhones.get(i).addTextChangedListener(new PhoneTextWatcher(mPhones.get(i)));
        setEditTextKeyImeChangeListener(mPhones.get(i));

        ImageView deletephone = new ImageView(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER;
        deletephone.setLayoutParams(params2);
        deletephone.setId(i);
        deletephone.setImageResource(R.drawable.phone_email_delete);
        deletephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                for(int i = 0; i < mPhonesLayout.getChildCount(); i++){
                    if(mPhonesLayout.getChildAt(i).getId() == id){
                        mPhonesLayout.removeViewAt(i);
                        phones.remove(i);
                        mPhones.remove(i);
                        resetAllIDofphoneLayout();
                        break;
                    }
                }

            }
        });

        mPhoneDeleteLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPhoneDeleteLayout.setLayoutParams(params3);
        mPhoneDeleteLayout.setOrientation(LinearLayout.HORIZONTAL);
        mPhoneDeleteLayout.setId(i);

        mPhoneDeleteLayout.addView(mPhones.get(i));
        mPhoneDeleteLayout.addView(deletephone);
        mPhonesLayout.addView(mPhoneDeleteLayout);

    }
    public void resetAllIDofphoneLayout(){
        if(phones.size() > 0){
            for(int i = 0; i < mPhonesLayout.getChildCount(); i++){
                mPhonesLayout.getChildAt(i).setId(i);
                LinearLayout layout = (LinearLayout)mPhonesLayout.getChildAt(i);
                for(int j = 0; j < layout.getChildCount(); j++){
                    layout.getChildAt(j).setId(i);
                }
            }
        }
    }

    private void addEmailEditText(String text)
    {
        ExtendedEditText emailEditText = new ExtendedEditText(getContext());
        emailEditText.setTextColor(Color.WHITE);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
        ViewCompat.setBackgroundTintList(emailEditText,colorStateList);
        mEmails.add(emailEditText);
        int i = mEmails.size()-1;
        mEmails.get(i).setText(text);
        mEmails.get(i).setId(i);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f);
        params1.setMargins(0, 0, 10, 0);
        mEmails.get(i).setLayoutParams(params1);

        mEmails.get(i).addTextChangedListener(new EmailTextWatcher(mEmails.get(i)));
        setEditTextKeyImeChangeListener(mEmails.get(i));

        ImageView deleteemail = new ImageView(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER;
        deleteemail.setLayoutParams(params2);
        deleteemail.setId(i);
        deleteemail.setImageResource(R.drawable.phone_email_delete);
        deleteemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                for(int i = 0; i < mEmailsLayout.getChildCount(); i++){
                    if(mEmailsLayout.getChildAt(i).getId() == id){
                        mEmailsLayout.removeViewAt(i);
                        emails.remove(i);
                        mEmails.remove(i);
                        resetAllIDofemailLayout();
                        break;
                    }
                }

            }
        });
        mEmailDeleteLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mEmailDeleteLayout.setLayoutParams(params3);
        mEmailDeleteLayout.setOrientation(LinearLayout.HORIZONTAL);
        mEmailDeleteLayout.setId(i);
        mEmailDeleteLayout.addView(mEmails.get(i));
        mEmailDeleteLayout.addView(deleteemail);
        mEmailsLayout.addView(mEmailDeleteLayout);
    }

    public void resetAllIDofemailLayout(){
        if(emails.size() > 0){
            for(int i = 0; i < mEmailsLayout.getChildCount(); i++){
                mEmailsLayout.getChildAt(i).setId(i);
                LinearLayout layout = (LinearLayout)mEmailsLayout.getChildAt(i);
                for(int j = 0; j < layout.getChildCount(); j++){
                    layout.getChildAt(j).setId(i);
                }
            }
        }
    }
    public class PhoneTextWatcher implements TextWatcher {
        private ExtendedEditText mEditText;

        public PhoneTextWatcher(ExtendedEditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phones.set(mEditText.getId(),s.toString());

        }

        public void afterTextChanged(Editable s) {
        }
    }

    public class EmailTextWatcher implements TextWatcher {
        private ExtendedEditText mEditText;

        public EmailTextWatcher(ExtendedEditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emails.set(mEditText.getId(),s.toString());
        }

        public void afterTextChanged(Editable s) {
        }
    }

    private void overrideBackButton() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    if(validate())
                        showConfirmSaveDialogue();
                    return true;
                }
                return false;
            }
        });
    }

    public static ContactEditFragment newInstance(ContactInfo contact, int requestType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT, contact);
        args.putSerializable(ARG_REQUEST_TYPE, requestType);

        ContactEditFragment fragment = new ContactEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mContact.setName(name);
        mContact.setPhones(phones);
        mContact.setEmails(emails);
        savedInstanceState.putSerializable(ARG_CONTACT, mContact);
        savedInstanceState.putSerializable(ARG_REQUEST_TYPE, mRequestType);
    }

    private void showConfirmSaveDialogue() {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Save")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //writeLogsToFile();
                        save();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancel();
                    }

                })
                .show();
    }

    private View.OnClickListener savelistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(validate())
                showConfirmSaveDialogue();
        }
    };

    private View.OnClickListener cancellistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancel();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.withFragment(this).withCompression(true).start();
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == WRITE_CONTACT_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(mRequestType == 1) {
                    ContactUtils.updateContact(mContact, getActivity(), getUpdateImage());
                    finishActivity();
                }
                else{
//                    saveContactInPhone(mContact);
                    ContactUtils.saveContactInPhone(mContact, getActivity(), getSavingImage());
                    finishActivity();
                }

            }
        }

    }

    public void finishActivity() {
//        if (deletePhoto) {
//            PictureUtils.deleteFile(pictureStoragePath, mContactPhotoName);
//        }
//        if (!deletePhoto && mTempPhotoFile != null && mTempPhotoFile.exists()) {
//            PictureUtils.moveFile(pictureStoragePath, mTempPhotoName, pictureStoragePath, mContactPhotoName);
//        } else {
            PictureUtils.deleteFile(pictureStoragePath, mTempPhotoName);
//        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(RETURN_STATE, "1");
        resultIntent.putExtra(CONTACT_OBJECT, mContact);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);

        getActivity().finish();
    }

    private boolean checkPermission() {                 ///////         camera permission check
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermission();
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

}
