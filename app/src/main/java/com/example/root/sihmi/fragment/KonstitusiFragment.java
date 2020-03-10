package com.example.root.sihmi.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.sihmi.R;
import com.example.root.sihmi.activity.DetailKonstitusiActivity;
import com.example.root.sihmi.activity.MainActivity;
import com.example.root.sihmi.adapter.ChatAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class KonstitusiFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;


    public KonstitusiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ((MainActivity)getActivity()).setActionBarTitle("KONSTITUSI");
        ButterKnife.bind(this, v);

        ChatAdapter a = new ChatAdapter(getContext().getApplicationContext(), 4, new ChatAdapter.itemClickListener() {
            @Override
            public void onItemClick() {
                startActivity(new Intent(getContext().getApplicationContext(), DetailKonstitusiActivity.class));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setAdapter(a);

        return v;
    }

}
