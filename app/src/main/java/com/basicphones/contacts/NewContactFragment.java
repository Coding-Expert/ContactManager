package com.basicphones.contacts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewContactFragment extends Fragment {

    private RecyclerView mContactRecyclerView;
    private RecyclerView mSectionRecyclerView;
    private LinearLayout section_layout;
    private TextView mContactTextView;
    private ContactAdapter mAdapter;
    private SectionAdapter mSectionAdapter;
    private String mQuery;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private static final String CONTACT_ID = "contactID";

    public static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };
    private static final int READ_CONTACT_REQUEST_CODE = 101;

    private FloatingActionButton newcontact_btn;
    private ActionMode mActionMode;
    private List<String> m_Sections = new ArrayList<>();
    private List<ContactInfo> contacts = new ArrayList<>();
    private ProgressBar contact_progress;
    String[] wheelMenu1 = new String[]{""};
    Context ctx;
    private View view;
    private static NewContactFragment instance = null;
    private boolean section_flag = true;
    private boolean contact_flag = false;
    public List<ContactInfo> all_contact = new ArrayList<>();
    private int count = 0;


    public NewContactFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.newcontact_fragment, container, false);
        ctx = getContext();
        mContactRecyclerView = (RecyclerView) view
                .findViewById(R.id.contact_recycler_view);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSectionRecyclerView = (RecyclerView) view
                .findViewById(R.id.section_recycler_view);
        mSectionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contact_progress = (ProgressBar) view.findViewById(R.id.progress_contact);

        mContactTextView = (TextView) view.findViewById(R.id.empty_view);
        newcontact_btn = (FloatingActionButton) view.findViewById(R.id.newcontact);
        newcontact_btn.setOnClickListener(newcontact_listener);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            updateUI1();
//            ContactGroupData m_ContactGroupData = new ContactGroupData();
//            m_ContactGroupData.setIndex(getActivity(),0);
//            m_ContactGroupData.execute();

        }
        else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
        }

        Crashlytics.setString("crashed screen", "app is crashed" /* string value */);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == READ_CONTACT_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getContactsFromPhone();
//                updateUI1();
                ContactGroupData m_ContactGroupData = new ContactGroupData();
                m_ContactGroupData.setIndex(getActivity(),0);
                m_ContactGroupData.execute();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        all_contact = ContactUtils.getContactsFromPhone(getActivity());
        ContactGroupData m_ContactGroupData = new ContactGroupData();
        m_ContactGroupData.setIndex(getActivity(),0);
        m_ContactGroupData.execute();

    }
    public void getContactDataFromPhone(){
        new ImportContactsAsync(getActivity(), new ImportContactsAsync.ICallback()
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
                updateUI1();
            }
        }).execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dotlayout, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                mQuery=s;
                updateUI1();
//                return false;
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                searchView.setQuery(mQuery, false);
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                mQuery=null;
                updateUI1();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ContactEditFragment.ADD_CONTACT) {
            String returnValue = data.getStringExtra(ContactEditFragment.RETURN_STATE);
            if(returnValue!=null) {
                if (returnValue.equals("0")){
                    Toast.makeText(getActivity(), "deleted",
                            Toast.LENGTH_SHORT).show();
                }
                else if (returnValue.equals("1")) {
                    ContactInfo contact = (ContactInfo) data.getSerializableExtra(ContactEditFragment.CONTACT_OBJECT);
                    if(contact!=null) {
                        Toast.makeText(getActivity(), "saved",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    public void updateUI1() {
//        ContactLab contactLab = ContactLab.get(getActivity());
        contacts = new ArrayList<>();
        List<ContactInfo> search_contacts = new ArrayList<>();
        if(mQuery==null || mQuery.equals("")) {
            mSectionRecyclerView.setVisibility(View.VISIBLE);
            newcontact_btn.show();
            contacts = all_contact; //ContactUtils.getContactsFromPhone(getActivity());
//            contacts = SortUtils.getContacts(contacts);
            m_Sections = SortUtils.getSortString(contacts);
        }
        else {
            mSectionRecyclerView.setVisibility(View.INVISIBLE);
            newcontact_btn.hide();
            contacts = all_contact; //ContactUtils.getContactsFromPhone(getActivity());
            if(contacts.size() > 0){
                for(int i = 0; i < contacts.size(); i++) {
                    if(contacts.get(i).getName().contains(mQuery)){
                        search_contacts.add(contacts.get(i));
                    }
                }
                contacts = search_contacts;
            }
//            contacts = contactLab.searchContactsByName(mQuery);
        }

        if (mAdapter == null) {
            mAdapter = new ContactAdapter(contacts);
            mContactRecyclerView.setAdapter(mAdapter);

            mSectionAdapter = new SectionAdapter(m_Sections);
            mSectionRecyclerView.setAdapter(mSectionAdapter);
        } else {
            mAdapter.setContacts(contacts);
            mAdapter.notifyDataSetChanged();
            mSectionAdapter.setSections(m_Sections);
            mSectionAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mAdapter.getPosition());
        }

//        int contactSize = contactLab.getContacts().size();

        if (contacts.size() == 0) {
            mContactRecyclerView.setVisibility(View.GONE);
            mContactTextView.setVisibility(View.VISIBLE);
        }
        else {
            mContactRecyclerView.setVisibility(View.VISIBLE);
            mContactTextView.setVisibility(View.GONE);
        }

//        updateSubtitle();
    }
    private void updateSubtitle() {
        ContactLab contactLab = ContactLab.get(getActivity());
        int contactSize = contactLab.getContacts().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, contactSize, contactSize);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mNameTextView;
        private ImageView mPhotoView;
        private TextView mSectionTextView;
        private CheckBox mSolvedCheckBox;
        private ContactInfo mContact;
        private ContactAdapter mAdapter;
        int imageViewWidth=0;
        int imageViewHeight=0;
        public LinearLayout item_layout;
        public int select_pos = 0;

        public ContactHolder(View itemView, ContactAdapter adaptor ) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.list_item_contact_name);
            mSectionTextView = (TextView) itemView.findViewById(R.id.list_item_contact_section);
            item_layout = (LinearLayout) itemView.findViewById(R.id.contact_item_layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    displayContextualToolbar();

                    return true;
                }
            });
            mAdapter=adaptor;
        }

        public void bindContact(ContactInfo contact, int position) {
            mContact = contact;
            mNameTextView.setText(mContact.getName());
            select_pos = position;
//            updatePhotoView();
            updateSectionName(mContact.getName(), position);

        }
        public void updateSectionName(String name, int position) {
            String first_char = "" + name.charAt(0);
            if(first_char.toUpperCase().matches("^[0-9]*$") || first_char.toUpperCase().equals("+")){

                if(position == 0){
                    mSectionTextView.setText("#");
                }
                else {
                    mSectionTextView.setText("");
                }
            }
            if(first_char.toUpperCase().matches(".*[A-Z].*")){
                if(position == 0){
                    mSectionTextView.setText(first_char.toUpperCase());
                }
                else {
                    String compare_string = "" + contacts.get(position - 1).getName().charAt(0);
                    if (!first_char.toUpperCase().equals(compare_string.toUpperCase())){
                        mSectionTextView.setText(first_char.toUpperCase());
                    }
                    else{
                        mSectionTextView.setText("");
                    }
                }

            }
        }
        public void displayContextualToolbar() {
            if(mActionMode == null) {
                mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new Toolbar_ActionMode_Callback(getActivity()));
                mActionMode.setTitle("ActionModebar");
            }
            else {
                mActionMode.setTitle("2 selected");
            }
        }

        @Override
        public void onClick(View v) {
            section_flag = false;
            contact_flag = true;
            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem);
            contact_focusedItem = getLayoutPosition();
            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem);
            String contact_string = "" + mNameTextView.getText().toString().charAt(0);
//            for(int i = 0; i < mSections.size(); i++){
//                String compare_str = "" + mSections.get(i);
//                if(compare_str.toUpperCase().equals(contact_string.toUpperCase())){
//                    mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
//                    section_focusedItem = i;
//                    mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
//                    mSectionRecyclerView.scrollToPosition(section_focusedItem);
//                    break;
//                }
//                if(contact_string.equals("#")){
//                    mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
//                    section_focusedItem = 0;
//                    mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
//                    mSectionRecyclerView.scrollToPosition(section_focusedItem);
//                    break;
//                }
//
//            }
            Intent intent = new Intent(getActivity(), ContactDetail.class);
            intent.putExtra("extra_contact", mContact);
            startActivity(intent);
        }
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<ContactInfo> mContacts;
        private List<String> sections;
        private LinearLayout layout;
        private int position;

        public ContactAdapter(List<ContactInfo> contacts) {
            mContacts = contacts;
            setHasStableIds(true);
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from( getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_contact, parent, false);
            layout = (LinearLayout) view.findViewById(R.id.contact_item_layout);
            return new ContactHolder(view,this);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            ContactInfo contact = mContacts.get(position);
            String contact_string = "" + contact.getName().charAt(0);
            holder.bindContact(contact, position);
            if(contact_flag) {
                holder.itemView.requestFocus();
                if (contact_focusedItem == position) {
                    holder.itemView.findViewById(R.id.contact_item_layout).setBackgroundColor(Color.GRAY);
                    for(int j = 0; j < m_Sections.size(); j++){
                        String comapre_string = m_Sections.get(j);
                        if(contact_string.equals(comapre_string)){
                            mSectionRecyclerView.smoothScrollToPosition(j);
                            break;
                        }
                        if(contact_string.toUpperCase().matches("^[0-9]*$") || contact_string.toUpperCase().equals("+")){
                            mSectionRecyclerView.smoothScrollToPosition(0);
                        }
                    }
                } else {
                    holder.itemView.findViewById(R.id.contact_item_layout).setBackgroundColor(Color.parseColor("#42403c"));
                }
            }
            else{
                holder.itemView.findViewById(R.id.contact_item_layout).setBackgroundColor(Color.parseColor("#42403c"));
            }
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public void setContacts(List<ContactInfo> contacts) {
            mContacts = contacts;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public int getItemViewType(int position)
        {
            return position;
        }

    }

    private class SectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private String mSection;
        private SectionAdapter mSectionAdapter;
        private int m_position;

        public SectionHolder(View itemView, SectionAdapter adaptor ) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.list_item_section_name);
            itemView.setOnClickListener(this);
            mSectionAdapter=adaptor;
        }

        public void bindContact(String section, int position) {
//            mContact = contact;
            mNameTextView.setText(section.toUpperCase());
            m_position = position;
        }

        @Override
        public void onClick(View v) {
            section_flag = true;
            contact_flag = false;
            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
            section_focusedItem = getLayoutPosition();
            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);

        }

    }

    public class SectionAdapter extends RecyclerView.Adapter<SectionHolder> {

        private List<String> mSections;
        private int position;
        int lastItemPosition = -1;

        public SectionAdapter(List<String> sections) {
            mSections = sections;
        }

        @Override
        public SectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from( getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_section, parent, false);
            return new SectionHolder(view,this);
        }

        @Override
        public void onBindViewHolder(SectionHolder holder, int position) {
            String section = mSections.get(position);
            holder.bindContact(section, position);
//            if (position > lastItemPosition) {
                // Scrolled Down
//                String msg = "" + position;
//                Toast.makeText(getContext(), msg, msg.length()).show();
//            }
//            else {
                // Scrolled Up
//                String msg = "" + position;
//                Toast.makeText(getContext(), msg, msg.length()).show();
//            }
//            lastItemPosition = position;
            if(section_flag) {
                holder.itemView.requestFocus();
                if (section_focusedItem == position) {
                    holder.itemView.findViewById(R.id.section_item_layout).setBackgroundColor(Color.GRAY);
                    for(int i = 0; i < contacts.size(); i++){
                        String compare_str = "" + contacts.get(i).getName().charAt(0);
                        if(compare_str.toUpperCase().equals(section.toUpperCase())){
                            mContactRecyclerView.scrollToPosition(i);
                            break;
                        }
                        if(section.equals("#")){
                            mContactRecyclerView.scrollToPosition(0);
                            break;
                        }

                    }
                } else {
                    holder.itemView.findViewById(R.id.section_item_layout).setBackgroundColor(Color.parseColor("#42403c"));
                }

            }
            else{
                holder.itemView.findViewById(R.id.section_item_layout).setBackgroundColor(Color.parseColor("#42403c"));
            }
        }

        @Override
        public int getItemCount() {
            return mSections.size();
        }
        public void setSections(List<String> sections) {
            mSections = sections;
        }

    }
    private View.OnClickListener newcontact_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContactInfo contact = new ContactInfo();
            Intent intent = ContactEditActivity.newIntent(getActivity(), contact, ContactEditFragment.ADD_CONTACT);
            startActivityForResult(intent,ContactEditFragment.ADD_CONTACT);
        }
    };


    public static NewContactFragment getInstance(){
        return instance;
    }
    public void setMove(int key){
        if(key == 0){       ///  Dpad_Down
            moveDown();
        }
        if(key == 1){               ///     Dpad_Up
            moveUp();
        }
        if(key == 2){               //      Dpad_Center
            showDetail();
        }
        if(key == 3){               //      Dpad_Right
            contact_flag = false;
            section_flag = true;
            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem - 1);
            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
            mSectionRecyclerView.scrollToPosition(section_focusedItem);

            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem - 1);
            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem);
            mContactRecyclerView.scrollToPosition(contact_focusedItem);
        }
        if(key == 4){               //      Dpad_Left
            section_flag = false;
            contact_flag = true;

            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem - 1);
            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
            mSectionRecyclerView.scrollToPosition(section_focusedItem);

            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem - 1);
            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem);
            mContactRecyclerView.scrollToPosition(contact_focusedItem);
        }
    }

    private int contact_focusedItem = 0;
    private int section_focusedItem = 0;
    private void moveDown(){
        if(contact_flag) {
            RecyclerView.LayoutManager llm = mContactRecyclerView.getLayoutManager();
            tryMoveSelectionForContact(llm, 1);
        }
        if(section_flag){
            RecyclerView.LayoutManager llm = mSectionRecyclerView.getLayoutManager();
            tryMoveSelectionForSection(llm, 1);
        }
    }
    private void moveUp(){
        if(contact_flag) {
            LinearLayoutManager llm = (LinearLayoutManager) mContactRecyclerView.getLayoutManager();
            tryMoveSelectionForContact(llm, -1);
        }
        if(section_flag) {
            LinearLayoutManager llm = (LinearLayoutManager) mSectionRecyclerView.getLayoutManager();
            tryMoveSelectionForSection(llm, -1);
        }
    }
    private void tryMoveSelectionForContact(RecyclerView.LayoutManager lm, int direction) {

        if(direction == 1){
            if (contact_focusedItem == mContactRecyclerView.getAdapter().getItemCount() - 1) {
                contact_focusedItem = -1;
            }
        }
        if(direction == -1){
            if (contact_focusedItem == 0) {
                contact_focusedItem = mContactRecyclerView.getAdapter().getItemCount();
            }
        }
        int tryFocusItem = contact_focusedItem + direction;
        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem <= mContactRecyclerView.getAdapter().getItemCount()) {
            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem);
            contact_focusedItem = tryFocusItem;
            mContactRecyclerView.getAdapter().notifyItemChanged(contact_focusedItem);
            lm.scrollToPosition(contact_focusedItem);
        }
    }
    private void tryMoveSelectionForSection(RecyclerView.LayoutManager lm, int direction) {
        if(direction == 1){
            if (section_focusedItem == mSectionRecyclerView.getAdapter().getItemCount() - 1) {
                section_focusedItem = -1;
            }
        }
        if(direction == -1){
            if (section_focusedItem == 0) {
                section_focusedItem = mSectionRecyclerView.getAdapter().getItemCount();
            }
        }
        int tryFocusItem = section_focusedItem + direction;

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem <= mSectionRecyclerView.getAdapter().getItemCount()) {
            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
            section_focusedItem = tryFocusItem;
            mSectionRecyclerView.getAdapter().notifyItemChanged(section_focusedItem);
            lm.scrollToPosition(section_focusedItem);

        }

    }
    private void showDetail(){
        if(contact_flag) {
            Intent intent = new Intent(getActivity(), ContactDetail.class);
            intent.putExtra("extra_contact", contacts.get(contact_focusedItem));
            startActivity(intent);
        }
    }

}
