package com.example.swufinalproject;

import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoardBean implements Serializable {

    public String id;
    public String writerId;
    public String joinId;
    public String full;
    public String food;
    public String starttime;
    public String endtime;
    public String price;        //배달 위한 최저 금액
    public String myprice;      //내가 참여하는 금액
    public String totalprice;   //총 모인 금액
    public String minjoinprice; //참가자들의 최소 참여 금액
    public String choice_foodWrite;
    public String choice_location;
    public String memo;
    public String kakaolink;
    public String kakaopwd;

    private List<String> userList;

    public List<String> getUserList() {
        if(userList == null) {
            userList = new ArrayList<>();
        }
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}