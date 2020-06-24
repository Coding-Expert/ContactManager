package com.basicphones.contacts.group;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.basicphones.contacts.R;

public class GroupDetailActivity extends AppCompatActivity {

    private Toolbar mTopToolbar;
    private TextView title_view;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title_view = (TextView) findViewById(R.id.toolbar_title);
        title_view.setText("Group Detail");

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backlistener);

        String groupId = (String) getIntent()
                .getSerializableExtra("extra_group_id");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GroupViewFragment.newInstance(groupId)).commit();
        String groupName = (String) getIntent()
                .getSerializableExtra("extra_group_name");
        title_view.setText(groupName);
    }

    private View.OnClickListener backlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            goPreviousScreen();
        }
    };

    public void goPreviousScreen() {
//        super.onBackPressed();
        finish();
    }
}
