package com.basicphones.contacts;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.basicphones.contacts.group.NewGroupFragment;

public class TabFragment extends Fragment {

    private TabLayout tabLayout;
    public ViewPager viewPager;
    public int index = 0;
    private TabLayout.Tab contact;
    private TabLayout.Tab group;

    public TabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        index = getArguments().getInt("tab_index");

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        contact = tabLayout.getTabAt(0);
        group = tabLayout.getTabAt(1);
        if(index == 0){
            contact.setIcon(R.drawable.person_2);
            group.setIcon(R.drawable.person_group_1);
            contact.select();
        }
        else{
            contact.setIcon(R.drawable.person_1);
            group.setIcon(R.drawable.person_group_2);
            group.select();
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    TabLayout.Tab contact = tabLayout.getTabAt(0);
                    TabLayout.Tab group = tabLayout.getTabAt(1);
                    contact.setIcon(R.drawable.person_2);
                    group.setIcon(R.drawable.person_group_1);
                }
                else {
                    TabLayout.Tab contact1 = tabLayout.getTabAt(0);
                    TabLayout.Tab group1 = tabLayout.getTabAt(1);
                    contact1.setIcon(R.drawable.person_1);
                    group1.setIcon(R.drawable.person_group_2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getChildFragmentManager());
        adapter.addFragment(new NewContactFragment(), "");
//        adapter.addFragment(new ContactFragment(), "");
        adapter.addFragment(new NewGroupFragment(), "");
        viewPager.setAdapter(adapter);
    }

    public void setDpadKey(int key){
        NewContactFragment.getInstance().setMove(key);
    }

}
