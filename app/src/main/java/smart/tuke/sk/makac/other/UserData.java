package smart.tuke.sk.makac.other;

import android.graphics.Bitmap;

public class UserData {

    private Bitmap avatar;
    private String name;
    private int id;
    private int current;

    public UserData (int id, Bitmap avatar , String name, int current){
        this.avatar = avatar;
        this.name = name;
        this.id = id;
        this.current = current;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getCurrent() {
        return current;
    }
}
