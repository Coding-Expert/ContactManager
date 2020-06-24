package com.basicphones.contacts;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

public class ContactDetail extends AppCompatActivity {

    private Toolbar mTopToolbar;
    private TextView title_view;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title_view = (TextView) findViewById(R.id.toolbar_title);
        title_view.setText("Contact Detail");

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backlistener);

//        UUID contactId = (UUID) getIntent()
//                .getSerializableExtra("extra_contact_id");
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ContactViewFragment.newInstance(contactId)).commit();

        ContactInfo contact = (ContactInfo)getIntent().getSerializableExtra("extra_contact");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ContactViewFragment.newInstance(contact)).commit();
        Crashlytics.setString("crashed screen", "app is crashed" /* string value */);

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

}
