package com.basicphones.contacts;


import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class ContactViewActivity extends SingleFragmentActivity {

    private static final String EXTRA_CONTACT_ID =
            "com.allonsy.android.contacts.crime_id";


    public static Intent newIntent(Context packageContext, UUID contactId) {
        Intent intent = new Intent(packageContext, ContactViewActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
//        UUID contactId = (UUID) getIntent()
//                .getSerializableExtra(EXTRA_CONTACT_ID);
//        return ContactViewFragment.newInstance(contactId);

        ContactInfo contact = (ContactInfo) getIntent()
                .getSerializableExtra(EXTRA_CONTACT_ID);
        return ContactViewFragment.newInstance(contact);
    }

}
