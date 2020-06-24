package com.basicphones.contacts;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    private Toolbar mTopToolbar;
    private ImageView back;

    protected abstract Fragment createFragment();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backlistener);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
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
