package com.vaye.app.Util.BottomSheetHelper;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetModel {

    ArrayList <String> items;
    String target;
    ArrayList<Integer> imagesHolder;

    public BottomSheetModel(ArrayList<String> items, String target, ArrayList<Integer> imagesHolder) {
        this.items = items;
        this.target = target;
        this.imagesHolder = imagesHolder;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public ArrayList<Integer> getImagesHolder() {
        return imagesHolder;
    }

    public void setImagesHolder(ArrayList<Integer> imagesHolder) {
        this.imagesHolder = imagesHolder;
    }

    public BottomSheetModel() {
    }
}
