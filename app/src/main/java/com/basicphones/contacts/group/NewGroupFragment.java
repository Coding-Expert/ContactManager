package com.basicphones.contacts.group;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.basicphones.contacts.ContactGroupData;
import com.basicphones.contacts.NewContactFragment;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basicphones.contacts.PictureUtils;
import com.basicphones.contacts.R;

import java.util.ArrayList;
import java.util.List;

public class NewGroupFragment extends Fragment {

    private FloatingActionButton newgroup_btn;
    public TextView ok_btn;
    public TextView cancel_btn;
    public EditText group_text;
    public Group mGroup;
    private String mQuery;
    private GroupAdapter mAdapter;
    private RecyclerView mGroupRecyclerView;
    private TextView mGroupTextView;
    private List<String> mContacts_ID;
    private List<Group> groups;
    public List<Group> all_group = new ArrayList<>();
    private static NewGroupFragment instance = null;

    private int WRITE_GROUP_REQUEST_CODE = 100;
    private int READ_GROUP_REQUEST_CODE = 101;


    public NewGroupFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.newgroup_fragment, container, false);
        mGroupRecyclerView = (RecyclerView) view.findViewById(R.id.group_recycler_view);
        mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGroupTextView = (TextView) view.findViewById(R.id.empty_view);
        newgroup_btn = (FloatingActionButton) view.findViewById(R.id.group);
        newgroup_btn.setOnClickListener(newgroup_listener);
//        all_group = GroupUtils.getGroupList(getActivity());

//        updateUI();
        ContactGroupData m_ContactGroupData = new ContactGroupData();
        m_ContactGroupData.setIndex(getActivity(),1);
        m_ContactGroupData.execute();

        Crashlytics.setString("crashed screen", "app is crashed" /* string value */);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
//        all_group = GroupUtils.getGroupList(getActivity());
//        updateUI();
        ContactGroupData m_ContactGroupData = new ContactGroupData();
        m_ContactGroupData.setIndex(getActivity(),1);
        m_ContactGroupData.execute();
    }

    public void updateUI() {
//        GroupLab groupLab = GroupLab.get(getActivity());
        groups = new ArrayList<>();
        List<Group> search_groups = new ArrayList<>();
        if(mQuery==null || mQuery.equals("")) {
//            groups = groupLab.getGroups();
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                groups = all_group; //GroupUtils.getGroupList(getActivity());
            }
            else{
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_GROUP_REQUEST_CODE);
            }

        }
        else {          ////////// get group list by search key word
//            groups = groupLab.searchGroupsByName(mQuery);
            groups = all_group; //GroupUtils.getGroupList(getActivity());
            if(groups.size() > 0){
                for(int i = 0; i < groups.size(); i++) {
                    if(groups.get(i).getName().contains(mQuery)){
                        search_groups.add(groups.get(i));
                    }
                }
                groups = search_groups;
            }
        }

        if (mAdapter == null) {
            mAdapter = new GroupAdapter(groups);
            mGroupRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setContacts(groups);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mAdapter.getPosition());
        }

//        int groupSize = groupLab.getGroups().size();
        int groupSize = groups.size();

        if (groupSize==0) {
            mGroupRecyclerView.setVisibility(View.GONE);
            mGroupTextView.setVisibility(View.VISIBLE);
        }
        else {
            mGroupRecyclerView.setVisibility(View.VISIBLE);
            mGroupTextView.setVisibility(View.GONE);
        }

//        updateSubtitle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    public static NewGroupFragment getInstance(){
        return instance;
    }

    private View.OnClickListener newgroup_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alertdialog_custom_view, null);
            builder.setView(dialogView);

            ok_btn = (TextView) dialogView.findViewById(R.id.ok);
            cancel_btn = (TextView) dialogView.findViewById(R.id.cancel);
            group_text = (EditText) dialogView.findViewById(R.id.group_text);
            group_text.requestFocus();

            final AlertDialog dialog = builder.create();
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validationCheck()){
                        createGroup();
                        dialog.cancel();
                    }
                    else {
                        String msg = "input group name";
                        Toast.makeText(getContext(), msg, msg.length()).show();
                    }

                }
            });
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    };

    public void createGroup() {           ////////////  store new group to phone contact

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            GroupUtils.createGroup(getActivity(), group_text.getText().toString());
        }
        else{
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_GROUP_REQUEST_CODE);
        }
        all_group = GroupUtils.getGroupList(getActivity());
        updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_GROUP_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                GroupUtils.createGroup(getActivity(), group_text.getText().toString());
                all_group = GroupUtils.getGroupList(getActivity());
                updateUI();
            }
        }
        if(requestCode == READ_GROUP_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                groups = all_group; //GroupUtils.getGroupList(getActivity());
            }
        }

    }

    public boolean validationCheck() {
        if(group_text.getText().toString().isEmpty() || group_text.getText().toString().equals("")) {
           return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dotlayout, menu);

        MenuItem searchItem = menu.findItem(R.id.search);       //// find group by search key word
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                mQuery=s;
//                updateUI();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                mQuery=s;
                updateUI();
                return true;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(mQuery, false);
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                mQuery=null;
                updateUI();
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_item_new_contact:
//                Contact contact = new Contact();
//                Intent intent = ContactEditActivity.newIntent(getActivity(), contact, ContactEditFragment.ADD_CONTACT);
//                startActivityForResult(intent,ContactEditFragment.ADD_CONTACT);
//                return true;
//            case R.id.menu_item_export_contacts:
//                boolean permissionsGranted = true;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                    permissionsGranted = checkPermissions(getActivity());
//
//                if(!permissionsGranted)
//                    askPermissions();
//                else {
//                    ExportDatabaseCSVTask task = new ExportDatabaseCSVTask();
//                    task.execute();
//                }
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {

        private List<Group> mGroups;

        private int position;

        public GroupAdapter(List<Group> group) {
            mGroups = group;
        }

        @Override
        public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from( getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_group, parent, false);
            return new GroupHolder(view,this);
        }

        @Override
        public void onBindViewHolder(GroupHolder holder, int position) {
            Group group = mGroups.get(position);
            holder.bindContact(group);
        }

        @Override
        public int getItemCount() {
            return mGroups.size();
        }

        public void setContacts(List<Group> groups) {
            mGroups = groups;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    private class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private ImageView mPhotoView;
        private CheckBox mSolvedCheckBox;
        private Group mGroup;
        private GroupAdapter mAdapter;
        int imageViewWidth=0;
        int imageViewHeight=0;



        public GroupHolder(View itemView, GroupAdapter adaptor ) {
            super(itemView);
            mNameTextView = (TextView)
                    itemView.findViewById(R.id.list_item_group_name);
            mPhotoView = (ImageView)
                    itemView.findViewById(R.id.list_item_group_photo);
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

            itemView.setOnClickListener(this);
            mAdapter=adaptor;
        }

        public void bindContact(Group group) {
            mGroup = group;
            mNameTextView.setText(mGroup.getName());
            updatePhotoView();

        }
        private void updatePhotoView()
        {
            if(imageViewWidth!=0 && imageViewHeight!=0) {
//                File mPhotoFile = GroupLab.get(getActivity()).getPhotoFile(mGroup);
//                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    String name = mGroup.getName();
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

//                } else {
//                    mPhotoView.setImageBitmap(PictureUtils.getCircularBitmap(mPhotoFile.getPath(), 40,imageViewWidth, imageViewHeight));
//                }
            }

        }

        @Override
        public void onClick(View v) {

            mAdapter.setPosition(getAdapterPosition());
//            Intent intent = ContactViewActivity.newIntent(getActivity(), mGroup.getmId());
            Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
            intent.putExtra("extra_group_id", mGroup.getmGroupId());
            intent.putExtra("extra_group_name", mGroup.getName());
            startActivity(intent);
        }
    }
}
