package com.example.adminbilly.attendancesystem.TaskView;

/**
 * Created by AdminBilly on 2017/5/8.
 */

public class TaskViewItem {
    private String deadline;
    private String location;
    private int imageId;

    public TaskViewItem(String deadline, String location, int imageId) {
        this.deadline = deadline;
        this.location = location;
        this.imageId = imageId;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getLocation(){
        return location;
    }

    public int getImageId() {
        return imageId;
    }

    public void setLocation(String loc){
        location = loc;
    }
}
