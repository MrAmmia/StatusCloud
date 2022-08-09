package net.thebookofcode.www.statuscloud;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> s = new MutableLiveData<>();
    private MutableLiveData<ArrayList<File>> imagePagerSource = new MutableLiveData<>();
    private MutableLiveData<Integer> position;
    private MutableLiveData<ArrayList<String>> path;
    public SharedViewModel(){
        position = new MutableLiveData<>();
        imagePagerSource = new MutableLiveData<>();
        path = new MutableLiveData<>();
    }

    public void setText(String s) {
        this.s.setValue(s);
    }

    public MutableLiveData<String> getText() {
        return this.s;
    }

    public void setImagePagerArray(ArrayList<File> imagePagerArray){this.imagePagerSource.setValue(imagePagerArray);}

    public LiveData<ArrayList<File>> getImagePagerArray() {
        return imagePagerSource;
    }

    public void setPosition(int position) {
        this.position.setValue(position);
    }
    public LiveData<Integer> getPosition(){
        return position;
    }
    public void setPath(ArrayList<String> path){
        this.path.setValue(path);}
    public LiveData<ArrayList<String>> getPath() {
        return path;
    }
}
