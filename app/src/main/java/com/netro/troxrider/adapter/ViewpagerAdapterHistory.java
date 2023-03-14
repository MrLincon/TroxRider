package com.netro.troxrider.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.netro.troxrider.fragment.FragmentCancelled;
import com.netro.troxrider.fragment.FragmentDelivered;
import com.netro.troxrider.fragment.FragmentInternational;
import com.netro.troxrider.fragment.FragmentLocal;
import com.netro.troxrider.fragment.FragmentLongDistance;
import com.netro.troxrider.fragment.FragmentPickedUp;
import com.netro.troxrider.fragment.FragmentProcessing;

public class ViewpagerAdapterHistory extends FragmentStatePagerAdapter {

    public ViewpagerAdapterHistory(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment[] fragment = {null};

        if (position == 0) {
            fragment[0] = new FragmentProcessing();
        } else if (position == 1) {
            fragment[0] = new FragmentPickedUp();
        }else if (position == 2) {
            fragment[0] = new FragmentDelivered();
        }else if (position == 3) {
            fragment[0] = new FragmentCancelled();
        }
        return fragment[0];
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Processing";
        }  else if (position == 1) {
            return "Picked Up";
        } else if (position == 2) {
            return "Delivered";
        } else if (position == 3) {
            return "DeliCancelledvered";
        }
        return null;
    }

}