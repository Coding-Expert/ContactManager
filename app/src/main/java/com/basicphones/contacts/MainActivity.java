package com.basicphones.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.LauncherActivity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;

import com.basicphones.contacts.contactbackup.CustomFileListAdapter;
import com.basicphones.contacts.contactbackup.DialogProperties;
import com.basicphones.contacts.contactbackup.DialogSelectionListener;
import com.basicphones.contacts.contactbackup.FileListAdapter;
import com.basicphones.contacts.contactbackup.FilePickerDialog;
import com.basicphones.contacts.contactbackup.ListItem;
import com.basicphones.contacts.contactbackup.VcfContactManagement;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.appcompat.widget.Toolbar;
import ezvcard.VCard;
import ezvcard.android.AndroidCustomFieldScribe;
import ezvcard.android.ContactOperations;
import ezvcard.io.text.VCardReader;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static ezvcard.util.IOUtils.closeQuietly;


public class MainActivity extends AppCompatActivity {


    private FirebaseAnalytics mFirebaseAnalytics;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView nv;
    private Toolbar mTopToolbar;
    private int tab_index = 0;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    public ImageView user_photo;
    private File mTempPhotoFile;
    private String mTempPhotoName;
    private File mUserPhotoFile;
    private String mUserPhotoName;
    private String pictureStoragePath;
    private ImagePicker imagePicker;
    public Bundle m_savedInstanceState;
    public boolean menu_flag = false;
    private TabFragment tabFragment;

    int imageViewWidth=0;
    int imageViewHeight=0;

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int READ_CONTACT_REQUEST_CODE = 101;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 102;
    private FilePickerDialog dialog;
    private ArrayList<ListItem> listItem;
    private CustomFileListAdapter mFileListAdapter;
    private RecyclerView fileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_savedInstanceState = savedInstanceState;

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, mTopToolbar, R.string.Open, R.string.Close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        getSupportActionBar().setTitle("Contact Manager");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        imagePicker = new ImagePicker();

        nv = (NavigationView)findViewById(R.id.nv);
        View header_layout = nv.getHeaderView(0);
        user_photo = (ImageView) header_layout.findViewById(R.id.profile_image);
        final ViewTreeObserver vto = user_photo.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                user_photo.getViewTreeObserver().removeOnPreDrawListener(this);
                imageViewWidth = user_photo.getMeasuredWidth();
                imageViewHeight = user_photo.getMeasuredHeight();
                File appDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                mUserPhotoFile = new File(appDir.getAbsolutePath() + File.separator + "myphoto.png");
                if(imageViewWidth!=0 && imageViewHeight!=0) {
                    if (mUserPhotoFile.exists() && mUserPhotoFile.canRead()) {
                        Bitmap image = PictureUtils.getCircularBitmap(mUserPhotoFile.getPath(), 80, imageViewWidth, imageViewHeight);
                        user_photo.setImageBitmap(image);
                    }
                }

                return true;
            }
        });

        user_photo.setOnClickListener(photo_listener);
        if(nv != null) {
            Menu menu = nv.getMenu();
            MenuItem cart_item = menu.findItem(R.id.mycart);
//            group_menu = cart_item.getSubMenu();
//            MenuItem social_item = cart_item.getSubMenu().findItem(R.id.social_items);
//            social_item.setActionView(R.layout.connect_us);
//            View v = (View) social_item.getActionView();
//            contact_group = v.findViewById(R.id.contact_group);

            nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.contact:
//                            ContactGroupData m_ContactGroupData = new ContactGroupData();
//                            m_ContactGroupData.setIndex(MainActivity.this, 0);
//                            m_ContactGroupData.execute();
                            loadingContactandGroup(0);
                            break;
                        case R.id.group:
//                            ContactGroupData m_ContactGroupData1 = new ContactGroupData();
//                            m_ContactGroupData1.setIndex(MainActivity.this, 1);
//                            m_ContactGroupData1.execute();
                            loadingContactandGroup(1);
                            break;
                        case R.id.mycart:
                            Toast.makeText(MainActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.save_contact:
//                            showFileDialog(true);
                            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 42);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;
                        case R.id.read_contact:
                            showFileDialog(false);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;
                        default:
                            return true;
                    }

                    return true;

                }
            });
        }
        checkPermission();
        Crashlytics.setString("crashed screen", "app is crashed" /* string value */);
    }
    public void loadingContactandGroup(int index){
        tabFragment = new TabFragment();
        Bundle bundle = new Bundle();
        if(index == 0){
            bundle.putInt("tab_index", 0);
        }
        else{
            bundle.putInt("tab_index", 1);
        }
        tabFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tabFragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    public void showFileDialog(boolean flag){
        mFileListAdapter = new CustomFileListAdapter(listItem, MainActivity.this);
        final DialogProperties properties=new DialogProperties();
        dialog=new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle("Select a File");
        dialog.setPositiveBtnName("Select");
        dialog.setNegativeBtnName("Cancel");
        dialog.setExport_flag(flag);
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if(dialog.getExport_flag()){
                    if(files.length > 0){
//                        VcfContactManagement contactManagement = new VcfContactManagement();
//                        contactManagement.setExportData(MainActivity.this, files[0], true);
//                        contactManagement.execute(true);
                    }
                }
                else{
                    if(files.length > 0) {

                        String[] filelist = new String[files.length]; int j = 0;
                        for(int i = 0; i < files.length; i++) {
                            String extension = files[i].substring(files[i].lastIndexOf("."));
                            if(extension.equals(".vcf")){
                                filelist[j] = files[i];
                                j++;
                            }
                        }
                        if(filelist.length > 0) {
                            List<ContactInfo> phone_contacts = ContactUtils.getContactsFromPhone(MainActivity.this);
                            VcfContactManagement contactManagement = new VcfContactManagement();
                            contactManagement.setImportData(MainActivity.this, filelist, false, MainActivity.this, phone_contacts);
                            contactManagement.execute(false);

                        }
                        else{
                            String error_msg = "no vcf file";
                            Toast.makeText(getApplicationContext(), error_msg, error_msg.length()).show();
                        }
                    }
                }
            }
        });
        dialog.show();
    }

    public void refreshContactView(){
        TabFragment tabFragment2 = new TabFragment();
        Bundle new_bundle = new Bundle();
        new_bundle.putInt("tab_index", 0);
        tabFragment2.setArguments(new_bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tabFragment2).commit();
        drawerLayout.closeDrawer(GravityCompat.START);

    }


    @Override
    public void onResume() {
        super.onResume();
        imageViewWidth = user_photo.getWidth();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.dotlayout, menu);
//        return true;
//    }

    private View.OnClickListener photo_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setPhotoFile();
            addImage();
        }
    };

    public void setPhotoFile() {
        mTempPhotoFile = null;
        mUserPhotoFile = null;
        File appDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(appDir!=null) {
            if (appDir.exists()) {
                pictureStoragePath = appDir.getAbsolutePath() + File.separator;
                mTempPhotoFile = new File(appDir.getAbsolutePath() + File.separator + "temp.png");
                mUserPhotoFile = new File(appDir.getAbsolutePath() + File.separator + "myphoto.png");
                mTempPhotoName = "temp.png";
                mUserPhotoName = "myphoto.png";
            }
        }

    }

    public void addImage() {                //////////      add photo to imageview from phone using camera or gallery

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        else {
            imagePicker.withActivity(this).withCompression(true).start();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_CAMERA_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                imagePicker.withActivity(this).withCompression(true).start();
//                Toast.makeText(getApplicationContext(), "camera permission granted", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "camera permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
        if(requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        if(requestCode == MY_CAMERA_REQUEST_CODE) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
        }
        if(requestCode == READ_CONTACT_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                imagePicker.withActivity(this).withCompression(true).start();
                if(m_savedInstanceState == null) {
                    tabFragment = new TabFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tab_index", 0);
                    tabFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tabFragment).commit();
                    nv.setCheckedItem(R.id.contact);
                }
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
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
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String Fpath = data.getDataString();
            Toast.makeText(getApplicationContext(), Fpath, Fpath.length()).show();
        }
        if(requestCode == 42){
            Uri treeUri = data.getData();
            if(treeUri != null) {

                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                VcfContactManagement contactManagement = new VcfContactManagement();
                contactManagement.setExportData(MainActivity.this, pickedDir, true);
                contactManagement.execute(true);
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
                updatePhotoView();
//                deletePhoto=false;
            }
            else {
                Toast.makeText(getApplicationContext(), "Sorry, Picture Storage Support not available",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException ioException) {

        }

    }

    private void updatePhotoView() {
//        if (mTempPhotoFile == null || !mTempPhotoFile.exists()) {
//            mPhotoView.setImageDrawable(null);
//        } else {
        Bitmap image = PictureUtils.getCircularBitmap(mTempPhotoFile.getAbsolutePath(), 80, imageViewWidth, imageViewHeight);
        Log.d("file path : ", mTempPhotoFile.getAbsolutePath());
        user_photo.setImageBitmap(image);
        PictureUtils.moveFile(pictureStoragePath, mTempPhotoName, pictureStoragePath, mUserPhotoName);
//        PictureUtils.deleteFile(pictureStoragePath, mTempPhotoName);

//        }
    }

    private boolean checkPermission() {                 ///////         camera permission check
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        if (m_savedInstanceState == null) {
                            TabFragment tabFragment = new TabFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("tab_index", 0);
                            tabFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tabFragment).addToBackStack(null).commit();
                            nv.setCheckedItem(R.id.contact);
                        }
                    } else {
                        readcontact_RequestPermission();
                    }
                } else {
                    cameraRequestPermission();
                }

            return true;
        } else {
            externalStorageRequestPermission();
            return true;
        }
    }

    private void externalStorageRequestPermission() {

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
    }
    private void cameraRequestPermission() {

        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
    }
    private void readcontact_RequestPermission() {

        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
    }

    public boolean WriteCheckPermission(Context context, String Permission) {
        if (ContextCompat.checkSelfPermission(context,
                Permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        // Ignore certain special keys so they're handled by Android
        if (keyCode == KeyEvent.KEYCODE_MENU){
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else{
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            drawerLayout.closeDrawer(GravityCompat.START);
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            if(event.getAction() == KeyEvent.ACTION_UP) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                TabFragment t_fragment = (TabFragment) fragment;
                t_fragment.setDpadKey(0);
                return true;
            }

        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
            if(event.getAction() == KeyEvent.ACTION_UP) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                TabFragment t_fragment = (TabFragment) fragment;
                t_fragment.setDpadKey(1);
                return true;
            }
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
            if(event.getAction() == KeyEvent.ACTION_UP){
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                TabFragment t_fragment = (TabFragment) fragment;
                t_fragment.setDpadKey(2);
                return true;
            }
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            if(event.getAction() == KeyEvent.ACTION_UP){
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                TabFragment t_fragment = (TabFragment) fragment;
                t_fragment.setDpadKey(3);
                return true;
            }
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
            if(event.getAction() == KeyEvent.ACTION_UP){
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                TabFragment t_fragment = (TabFragment) fragment;
                t_fragment.setDpadKey(4);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    public void getCrashGenerate(){
        Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Crashlytics.getInstance().crash(); // Force a crash
//                Crashlytics.setString(key, "foo" /* string value */);
//
//                Crashlytics.setBool(key, true /* boolean value */);
//
//                Crashlytics.setDouble(key, 1.0 /* double value */);
//
//                Crashlytics.setFloat(key, 1.0f /* float value */);
//
//                Crashlytics.setInt(key, 1 /* int value */);
            }
        });
        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
    public void getAnalysticsGenerate() {
        //        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }



}
