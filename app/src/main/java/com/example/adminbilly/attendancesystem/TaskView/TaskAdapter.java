package com.example.adminbilly.attendancesystem.TaskView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adminbilly.attendancesystem.R;

import java.util.List;

/**
 * Created by AdminBilly on 2017/5/8.
 */

public class TaskAdapter extends ArrayAdapter<TaskViewItem> {
    private int resourceId;

    public TaskAdapter(Context context, int textViewResourceId, List<TaskViewItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskViewItem tVI = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView taskImage = (ImageView) view.findViewById(R.id.task_image);
        TextView taskDdl = (TextView) view.findViewById(R.id.task_deadline);
        TextView taskLoc = (TextView) view.findViewById(R.id.task_location);
        taskImage.setImageResource(tVI.getImageId());
        taskDdl.setText(tVI.getDeadline());
        taskLoc.setText(tVI.getLocation());
        return view;
    }
}
