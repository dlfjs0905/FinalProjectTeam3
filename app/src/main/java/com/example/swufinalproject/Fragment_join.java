package com.example.swufinalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Fragment_join extends Fragment {

    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView_Join;
    private List<BoardBean> mJoinList = new ArrayList<>();
    private List<BoardBean> mJoinList2 = new ArrayList<>();
    private JoinAdapter mJoinAdapter;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment UI 생성
        View view = inflater.inflate(R.layout.fragment_join, container, false);


        mListView_Join = view.findViewById(R.id.lstJoin);

        //최초 데이터 셋팅
        mJoinAdapter = new JoinAdapter(getContext(), mJoinList);
        mJoinAdapter = new JoinAdapter(getContext(), mJoinList2);
        mListView_Join.setAdapter(mJoinAdapter);

        //데이터 취득
        mFirebaseDB.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mJoinList.clear();
                mJoinList2.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardBean bean = snapshot.getValue(BoardBean.class);

                    if(bean.getUserList().contains(mFirebaseAuth.getCurrentUser().getEmail())){
                        mJoinList.add(0, bean);/*
                        if (TextUtils.equals(bean.full, "unfull")) {
                            mJoinList.add(0, bean);
                        } if (TextUtils.equals(bean.full, "full")) {
                            mJoinList2.add(0, bean);
                        }
                    mJoinList.addAll(mJoinList2);*/
                    }
                }
                //바뀐 데이터로 Refresh 한다.
                if(mJoinAdapter != null) {
                    mJoinAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}