package com.ueueo.photopicker.base;

import java.io.Serializable;

public class PhotoItem implements Serializable {
    public String path;
    public String name;
    public long time;

    public PhotoItem(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

}
