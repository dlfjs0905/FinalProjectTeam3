package com.example.swufinalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class TabActivity extends AppCompatActivity {

    private TabLayout tabLayout; // Tab 영역
    private ViewPager viewPager; // Tab별 표시할 영역
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        tabLayout = findViewById(R.id.tabLaout);
        viewPager = findViewById(R.id.viewPager);

        // Tab 생성
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home2));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.list1));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.join1));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.logout1));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setTabRippleColor(null);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // ViewPager 생성
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setIcon(R.drawable.home2);
                } else {
                    tabLayout.getTabAt(0).setIcon(R.drawable.home1);
                }

                if(tab.getPosition() == 1) {
                    tabLayout.getTabAt(1).setIcon(R.drawable.list2);
                } else {
                    tabLayout.getTabAt(1).setIcon(R.drawable.list1);
                }

                if(tab.getPosition() == 2) {
                    tabLayout.getTabAt(2).setIcon(R.drawable.join2);
                } else {
                    tabLayout.getTabAt(2).setIcon(R.drawable.join1);
                }

                if(tab.getPosition() == 3) {
                    new AlertDialog.Builder(TabActivity.this).setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                            .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    signOut();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    viewPager.setCurrentItem(0);
                                }
                            })
                            .show();
                    tabLayout.getTabAt(3).setIcon(R.drawable.logout2);

                }
                else {
                    tabLayout.getTabAt(3).setIcon(R.drawable.logout1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void signOut() {
        try{
            mFirebaseAuth.signOut();
            Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        int tabSize; // TAB 수

        public MyPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.tabSize = count; // TAB 수
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Fragment_main();
                case 1:
                    return new Fragment_list();
                case 2:
                    return new Fragment_join();
                case 3:
                    return new Fragment_logout();
            }
            return null;
        }
        @Override
        public int getCount () {
            return this.tabSize;
        }
    }
}
