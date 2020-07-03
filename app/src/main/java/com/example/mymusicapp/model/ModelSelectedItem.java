package com.example.mymusicapp.model;

public class ModelSelectedItem {
    private int position;
    private boolean isSelectd;
    public ModelSelectedItem() {

    }

    public ModelSelectedItem(int position, boolean isSelectd) {
        this.position = position;
        this.isSelectd = isSelectd;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSelectd() {
        return isSelectd;
    }

    public void setSelectd(boolean selectd) {
        isSelectd = selectd;
    }
}
