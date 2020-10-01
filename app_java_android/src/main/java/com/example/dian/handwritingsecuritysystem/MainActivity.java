package com.example.dian.handwritingsecuritysystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AmbilDataTask.Callback  {

    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 1 ;
    public ImageButton buttonregis;
    public ImageButton btnCekDocs;
    public ImageButton btnabout;
    private Object permissions;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //new AmbilDataTask(this).execute("http://192.168.43.247/rest-api/json/coba.json");

        //new AmbilDataTask(this).execute("http://www.json-generator.com/api/json/get/ceJExUoTfS?indent=2");


       // new AmbilDataTask(this).execute("http://stevenss.pythonanywhere.com/");
        /*
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


        String prg = "import sys";
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(new File(getFilesDir(),"LVQ_hibah.py")));
            out.write(prg);
            out.close();
            Process p = Runtime.getRuntime().exec("python LVQ_hibah.py");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ret = in.readLine();
            Log.d(TAG, "ISI file python: " + ret);
        } catch (IOException e) {
            e.printStackTrace();
        }


         */



        buttonregis = (ImageButton) findViewById(R.id.buttonregis);
        btnCekDocs = (ImageButton) findViewById(R.id.buttoncheck);
        btnabout = (ImageButton) findViewById(R.id.buttonabout);


        buttonregis.setOnClickListener(this);
        btnCekDocs.setOnClickListener(this);
        btnabout.setOnClickListener(this);

    }


    @Override public void sendResult(String result) {
        Log.d(TAG, "sendResult: " + result);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttoncheck :
                Intent bb = new Intent(MainActivity.this, cekDokumen.class);
                startActivity(bb);
                break;
            case R.id.buttonregis :
                Intent cc = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(cc);
                break;
            case R.id.buttonabout :
                Intent dd = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(dd);
                break;
        }

    }

    private static final String TAG = "main";





}
