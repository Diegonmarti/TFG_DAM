package com.example.proyecto;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class login_cardview extends FragmentPagerAdapter {

    private Context context;
    private int totalTabs;

    public login_cardview(FragmentManager fm, Context context, int totalTabs){
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public Fragment getItem(int position){
        switch(position){
            case 0:
                login loginTabFragment = new login();
                return loginTabFragment;
            case 1:
                register signTabFragment = new register();
                return signTabFragment;
            default:
                return null;
        }
    }
}
