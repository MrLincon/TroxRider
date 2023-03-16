package com.netro.troxrider.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.netro.troxrider.fragment.assigned.FragmentAssigned;
import com.netro.troxrider.fragment.assigned.FragmentCancelledAssigned;
import com.netro.troxrider.fragment.assigned.FragmentDeliveredAssigned;
import com.netro.troxrider.fragment.assigned.FragmentPickedUpAssigned;
import com.netro.troxrider.fragment.history.FragmentCancelledHistory;
import com.netro.troxrider.fragment.history.FragmentDeliveredHistory;
import com.netro.troxrider.fragment.history.FragmentPickedUpHistory;
import com.netro.troxrider.fragment.history.FragmentProcessingHistory;

public class ViewpagerAdapterAssigned extends FragmentStatePagerAdapter {

    public ViewpagerAdapterAssigned(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment[] fragment = {null};

        if (position == 0) {
            fragment[0] = new FragmentAssigned();
        } else if (position == 1) {
            fragment[0] = new FragmentPickedUpAssigned();
        }else if (position == 2) {
            fragment[0] = new FragmentDeliveredAssigned();
        }else if (position == 3) {
            fragment[0] = new FragmentCancelledAssigned();
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
            return "Assigned";
        }  else if (position == 1) {
            return "Picked Up";
        } else if (position == 2) {
            return "Delivered";
        } else if (position == 3) {
            return "Cancelled";
        }
        return null;
    }

}