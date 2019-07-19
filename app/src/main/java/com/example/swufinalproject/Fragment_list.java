package com.example.swufinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_list extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView;
    private List<BoardBean> mBoardList = new ArrayList<>();
    private List<BoardBean> mBoardList2 = new ArrayList<>();
    private BoardAdapter mBoardAdapter;
    private DatabaseReference mReference;

    private ChildEventListener mChild;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment UI 생성
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = view.findViewById(R.id.lstBoard);

        //최초 데이터 셋팅
        mBoardAdapter = new BoardAdapter(getContext(), mBoardList);
        mBoardAdapter = new BoardAdapter(getContext(), mBoardList2);
        mListView.setAdapter(mBoardAdapter);
        view.findViewById(R.id.btnWrite).setOnClickListener(mClicks);

        //데이터 취득
        mFirebaseDB.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mBoardList.clear();
                mBoardList2.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardBean bean = snapshot.getValue(BoardBean.class);

                    if (TextUtils.equals(bean.full, "unfull")) {
                        mBoardList.add(0, bean);
                    } if (TextUtils.equals(bean.full, "full")) {
                        mBoardList2.add(0, bean);
                    }
                }
                mBoardList.addAll(mBoardList2);

                //바뀐 데이터로 Refresh 한다.
                if(mBoardAdapter != null) {
                    mBoardAdapter.setBoardList(mBoardList);
                    mBoardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnWrite:
                    Intent i = new Intent(getContext(), WriteActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };


}