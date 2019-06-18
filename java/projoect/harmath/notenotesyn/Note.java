package projoect.harmath.notenotesyn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Harmath on 2017. 09. 14..
 */

public class Note implements Serializable {

    private String id;
    private String title;
    private String desc;
    private int icon;
    private HashMap<String,String> connectedPhones;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public HashMap<String,String> getConnectedPhones() {
        return connectedPhones;
    }

    public void setConnectedPhones(HashMap<String, String> connectedPhones) {
        this.connectedPhones = connectedPhones;
    }

    public void addConnectedPhonesItem(String connectedPhones) {

        this.connectedPhones.put(connectedPhones,connectedPhones);
    }

    public void removeConnectedPhonesItem(String connectedPhonesID) {
        this.connectedPhones.remove(connectedPhonesID);
    }

    public Note(String title, String desc, int icon) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
        connectedPhones=new HashMap<>();
    }
    public Note(String id,String title, String desc, int icon) {
        this.id=id;
        this.title = title;
        this.desc = desc;
        this.icon = icon;
        connectedPhones=new HashMap<>();
    }

    public Note(){}
}
