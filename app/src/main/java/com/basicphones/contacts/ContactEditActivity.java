package com.basicphones.contacts;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

public class ContactEditActivity extends SingleFragmentActivity {

    private static final String EXTRA_CONTACT =
            "com.allonsy.android.contacts.contact";

    private static final String EXTRA_REQUEST_TYPE =
            "com.allonsy.android.contacts.request_type";

    public static Intent newIntent(Context packageContext, ContactInfo contact, int requestType) {
        Intent intent = new Intent(packageContext, ContactEditActivity.class);
        intent.putExtra(EXTRA_CONTACT, contact);
        intent.putExtra(EXTRA_REQUEST_TYPE, requestType);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        ContactInfo contact = (ContactInfo) getIntent()
                .getSerializableExtra(EXTRA_CONTACT);
        int requestType = (int) getIntent()
                .getSerializableExtra(EXTRA_REQUEST_TYPE);
        return ContactEditFragment.newInstance(contact, requestType);
    }

}
