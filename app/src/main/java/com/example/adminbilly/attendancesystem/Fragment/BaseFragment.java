package com.example.adminbilly.attendancesystem.Fragment;

import android.support.v4.app.Fragment;

import com.example.adminbilly.attendancesystem.TaskManager;

/**
 * Created by AdminBilly on 2017/4/10.
 */

public abstract class BaseFragment extends Fragment {
    //TaskManager
    protected TaskManager mTM = TaskManager.getInstance();
}
