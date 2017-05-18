package com.example.adminbilly.attendancesystem.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.adminbilly.attendancesystem.Activity.HistoryActivity;
import com.example.adminbilly.attendancesystem.Activity.SignUpActivity;
import com.example.adminbilly.attendancesystem.Activity.WelcomeActivity;
import com.example.adminbilly.attendancesystem.R;

import static com.example.adminbilly.attendancesystem.Activity.LoginActivity.id_token;
import static com.example.adminbilly.attendancesystem.Activity.LoginActivity.curUser;

/**
 * Created by AdminBilly on 2017/4/6.
 */

public class FragmentMe extends BaseFragment {
    private RelativeLayout button_history;
    private RelativeLayout button_track;
    private RelativeLayout button_settings;
    private RelativeLayout button_logout;

    public FragmentMe() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        button_history = (RelativeLayout)view.findViewById(R.id.history_button);
        button_track = (RelativeLayout)view.findViewById(R.id.track_button);
        button_settings = (RelativeLayout)view.findViewById(R.id.settings_button);
        button_logout = (RelativeLayout)view.findViewById(R.id.logout_button);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FragmentMe.this.getContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        button_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_token = null;
                curUser = null;
                Intent intent = new Intent(FragmentMe.this.getActivity(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
