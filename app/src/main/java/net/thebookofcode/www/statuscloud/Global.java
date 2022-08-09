package net.thebookofcode.www.statuscloud;

import android.net.Uri;
import android.view.ActionMode;

import java.util.ArrayList;

public class Global {
    public static ActionMode aActionMode = null;
    public static ActionMode sActionMode = null;
    public static ArrayList<String> urlArrayList = new ArrayList<>();

    public static void addUri(String uri){
        urlArrayList.add(uri);
    }
    public static void removeUri(String uri){
        urlArrayList.remove(uri);
    }
}
