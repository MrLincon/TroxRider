package com.netro.troxrider.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.netro.troxrider.fragment.FragmentInternational;
import com.netro.troxrider.fragment.FragmentLocal;
import com.netro.troxrider.fragment.FragmentLongDistance;

public class ViewpagerAdapterHome extends FragmentStatePagerAdapter {

    public ViewpagerAdapterHome(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment[] fragment = {null};

        if (position == 0) {
            fragment[0] = new FragmentLocal();
        } else if (position == 1) {
            fragment[0] = new FragmentLongDistance();
        }else if (position == 2) {
            fragment[0] = new FragmentInternational();
        }
        return fragment[0];
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Local";
        }  else if (position == 1) {
            return "Long Distance";
        } else if (position == 2) {
            return "International";
        }
        return null;
    }

}