package com.example.dian.handwritingsecuritysystem;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;

public class IOHelper {

    public static void writeToFile(Context context, String fileName, String str){
        try {
            FileOutputStream fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            fos.write(str.getBytes(), 0, str.length());
            fos.close();
        }
        catch(IOException e){
            e.printStackTrace();
            }
        }
    }


