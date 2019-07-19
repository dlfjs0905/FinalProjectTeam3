package com.example.swufinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class JoinAdapter extends BaseAdapter {

    private int mPrice;
    private int mWriterPrice;

    private Context mContextJoin;
    private List<BoardBean> mJoinList;


    public JoinAdapter(Context context, List<BoardBean> joinList) {
        mContextJoin = context;
        mJoinList = joinList;
    }

    @Override
    public int getCount() {
        return mJoinList.size();
    }

    @Override
    public Object getItem(int i) {
        return mJoinList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)mContextJoin.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view =  inflater.inflate(R.layout.view_join_item, null);

        TextView leastPrice = view.findViewById(R.id.leastPrice);
        TextView currentPrice = view.findViewById(R.id.currentPrice);
        TextView myJoinPrice = view.findViewById(R.id.myJojnPrice);
        TextView foodName = view.findViewById(R.id.foodName);
        ImageView imgBar = view.findViewById(R.id.imgBar);

        final BoardBean boardBean2 = mJoinList.get(i);

        leastPrice.setText(boardBean2.price);
        currentPrice.setText(boardBean2.totalprice);
        myJoinPrice.setText(boardBean2.myprice);
        foodName.setText(boardBean2.food);

        mWriterPrice = (Integer.parseInt(boardBean2.totalprice)*1); //현재 참여 금액 int로 변경
        mPrice = (Integer.parseInt(boardBean2.price)/5); //최소 금액 int로 변경


        //바 이미지 변경
        if (mWriterPrice <= mPrice){
            imgBar.setImageResource(R.drawable.bar_20per);
        } else if (((mPrice * 2) < mWriterPrice) && (mWriterPrice <= (mPrice * 3))){
            imgBar.setImageResource(R.drawable.bar_40per);
        } else if (((mPrice * 3) < mWriterPrice) && (mWriterPrice <= (mPrice * 4))){ //
            imgBar.setImageResource(R.drawable.bar_60per);
        } else if (((mPrice* 4) < mWriterPrice) && (mWriterPrice < (mPrice * 5))){
            imgBar.setImageResource(R.drawable.bar_80per);
        } else if (mWriterPrice >= (mPrice * 5)){
            imgBar.setImageResource(R.drawable.bar_100per);
        }


        return view;
    }
}