package com.example.adminbilly.attendancesystem.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adminbilly.attendancesystem.R;

/**
 * Created by AdminBilly on 2017/4/6.
 */

public class FragmentTask extends BaseFragment {

    public static FragmentTask newInstance() {
        return new FragmentTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }
}
