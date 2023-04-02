package com.wiss.cameraview;

import java.io.File;

/**
 * simple file wrapper class that provides an easy to read filename in the toString method
 */
public class MyPictureFile {

    private File f;
    public MyPictureFile(File f){
        this.f = f;
    }

    @Override
    public String toString(){
        return f.getName();
    }

    public File getFile(){
        return f;
    }

}

