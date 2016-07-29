package com.ueueo.photopicker.base;

import java.io.Serializable;
import java.util.List;

public class PhotoAlbum implements Serializable {
    public String name;
    public String path;
    public PhotoItem cover;
    public List<PhotoItem> photoItems;

}
