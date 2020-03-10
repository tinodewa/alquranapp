package com.example.root.sihmi.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.root.sihmi.R;
import com.example.root.sihmi.activity.MainActivity;
import com.example.root.sihmi.adapter.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    @BindView(R.id.frameTab)
    FrameLayout frameLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);
        ((MainActivity)getActivity()).setActionBarTitle("SIHMI");
        ButterKnife.bind(this, v);

        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PersonalFragment(), "Personal Chat");
        adapter.addFragment(new GroupFragment(), "Group Chat");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                session.setPosIncident(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
