package com.example.dian.handwritingsecuritysystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

public class cekDokumen extends AppCompatActivity {

    DatabaseHelper myDb;
    Intent intent;
    Uri fileUri;

    public int cacah;

    public double lebar;
    public double tinggi;
    Bitmap bitmap, decoded;

    public TextView info;
    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;

    int bitmap_size = 40; // image quality 1 - 100;
    int max_resolution_image = 800;

    double[][] rgb = new double[500][200];
    double[] prob = new double[256];

    public double[] jarak = new double[500];
    public double buffer_jarak;

    public double nilai_mean, nilai_invar, nilai_entro, nilai_skew, nilai_smooth, nilai_ener, nilai_contra;



    public double greyscale1;
    public double nilai_buffer;
    public double rgb2;
    public double nilai_invariance;
    public double nilai_invariance_buffer;
    public double nilai_entropy;
    public double nilai_entropy_buffer;

    public double akar_variance;
    public double pangkat3_variance;
    public double nilai_skewness;
    public double nilai_skewness_buffer;

    public double relative_smoothness;
    public double relative_smoothness_buffer;

    public double nilai_energy;
    public double nilai_energy_buffer;

    public double nilai_contrast;
    public double nilai_contrast_buffer;
    public int nilai_id;
    public String[] nama_pemilik = new String[1000];
    public int jumlah_data;
    public double[] jarak_buffer = new double[1000];
    public double nilai_buffer_min;
    public double jarak_terkecil;
    StringBuffer ttt;
    int clicks = 0;
    int id;
    JSONArray jsonArray_db=new JSONArray();


    private static final String TAG = "main";
    String str_jsonArray_ditambah_data_baru,Nama,Kelas,No_gambar;



    Button button1; //browse
    Button button2; //check
    Button button3; //test

    TextView textTargetUri;
    ImageView imageView;
    TextView nama_pemilik_dokumen;
    // TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
        setContentView(R.layout.activity_cek_dokumen);
        //button1 = (Button) findViewById(R.id.btn_check);

        button1 = (Button) findViewById(R.id.btn_browse);
        button2 = (Button) findViewById(R.id.btn_check);
        button3 = (Button) findViewById(R.id.test);

        textTargetUri = (TextView) findViewById(R.id.targeturi);
        imageView = (ImageView) findViewById(R.id.targetimage);
        info = (TextView)findViewById(R.id.text_info);
        nama_pemilik_dokumen = (TextView)findViewById(R.id.nama_pemilik);

        viewAll();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // ====  mengambil data dari DB =======

        Cursor res = myDb.getAllData();
        if(res.getCount() == 0) {
            // show message
            showMessage("Error","Data Tidak ditemukan");
            return;
        }


        while (res.moveToNext()) {

            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("ID",res.getString(0));
                jsonParam.put("NAMA",res.getString(1));
                jsonParam.put("NO_GAMBAR",res.getString(2));
                jsonParam.put("KTP",res.getString(3));
                jsonParam.put("NILAI_GREYSCALE",res.getDouble(4));
                jsonParam.put("NILAI_INVARIANCE",res.getDouble(5));
                jsonParam.put("NILAI_ENTROPY",res.getDouble(6));
                jsonParam.put("NILAI_SKEWNESS",res.getDouble(7));
                jsonParam.put("RELATIVE_SMOOTHNESS",res.getDouble(8));
                jsonParam.put("NILAI_ENERGY",res.getDouble(9));
                jsonParam.put("NILAI_CONTRAST",res.getDouble(10));

                jsonArray_db.put(jsonParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void selectImage() {
        nama_pemilik_dokumen.setText("");

        imageView.setImageResource(0);
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(cekDokumen.this);
        builder.setTitle("Add Photo!");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "requestCode " + requestCode + ", resultCode " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Log.e("CAMERA", fileUri.getPath());

                    bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    // mengambil gambar dari Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(cekDokumen.this.getContentResolver(), data.getData());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                    ambil_fitur(bitmap);

                    //ambil greyscale

                    //


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Untuk menampilkan bitmap pada ImageView
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }

    // Untuk resize bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        lebar = width;
        tinggi = height;


        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    public void ambil_fitur(final Bitmap image) {
        button2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StringBuffer mmm = new StringBuffer();

                        rgb2 = 0;
                        for (int xx = 0; xx < lebar; xx++) {
                            for (int yy = 0; yy < tinggi; yy++) {
                                //Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                int pixel = image.getPixel(xx, yy);

                                int r = Color.red(pixel);
                                int g = Color.green(pixel);
                                int b = Color.blue(pixel);

                                //info.setText("R(" + r + ")\n" + "G(" + g + ")\n" + "B(" + b + ")\n" + "X=" + xx + "Y=" + yy);
                                nilai_buffer = (r + g + b) / 3;
                                //if (nilai_buffer > 150)
                                 //   nilai_buffer = 255;


                                rgb[xx][yy] = nilai_buffer;

                                rgb2 = rgb2 + nilai_buffer;
                            }
                        }
                        greyscale1 = 0;
                        greyscale1 = rgb2 / (lebar * tinggi);
                        //textView.setText("Ukuran gambar = " + lebar + " x " + tinggi + " piksel\n" + "Nilai Fitur A (greyscale) = " + greyscale1);
                        //info.setText(rgb[0][0]+ ", " + rgb[1][0]+ ", " + rgb[2][0]+ ", " + rgb[3][0]+ ", " + rgb[4][0]+ ", " + rgb[0][1]+ "," + rgb[1][1]+ "," + rgb[2][1]+ "," +rgb[3][1]+ "," + rgb[4][1] );


                        //fitur kedua (variance)
                        for (int interval = 0; interval < 256; interval++) {
                            prob[interval] = 0;
                            for (int xxv = 0; xxv < lebar; xxv++) {
                                for (int yyv = 0; yyv < tinggi; yyv++) {
                                    if (interval == rgb[xxv][yyv]) {
                                        prob[interval] = prob[interval] + 1;
                                    }
                                }
                            }
                        }

                        nilai_invariance = 0;
                        for(int interval=0; interval<256; interval++)
                        {
                            prob[interval] = prob[interval]/256;

                            nilai_invariance_buffer = 0;
                            nilai_invariance_buffer = (Math.pow((interval - greyscale1),2)) * prob[interval];
                            nilai_invariance_buffer = Math.round(nilai_invariance_buffer * 100)/100;
                            nilai_invariance = nilai_invariance + nilai_invariance_buffer;
                        }
                        akar_variance = Math.sqrt(nilai_invariance);
                        pangkat3_variance = Math.pow(akar_variance, 3);
                        pangkat3_variance = Math.round(pangkat3_variance*100)/100;

                        //info.setText("Ukuran gambar = " + lebar + " x " + tinggi + " piksel\n" + "Nilai Fitur A (greyscale) = " + greyscale1 + "\n" + "Nilai Fitur B (variance) = " + nilai_invariance);

                        //nilai fitur ketiga (entropy)
                        nilai_entropy_buffer = 0;
                        nilai_entropy = 0;
                        for(int interval=0; interval<256; interval++)
                        {
                            nilai_entropy_buffer = 0;
                            nilai_entropy_buffer = prob[interval];
                            nilai_entropy = nilai_entropy + (nilai_entropy_buffer * Math.log(nilai_entropy_buffer));
                            nilai_entropy = Math.floor(nilai_entropy);
                        }
                        //info.setText("Ukuran gambar = " + lebar + " x " + tinggi + " piksel\n" + "Nilai Fitur A (greyscale) = " + greyscale1 + "\n" + "Nilai Fitur B (variance) = " + nilai_invariance + "\n" + "Nilai Fitur C (entropy) = " + nilai_entropy);

                        //nilai fitur keempat (skewness)
                        nilai_skewness = 0;
                        nilai_skewness_buffer = 0;

                        for(int interval=0; interval < 256; interval++)
                        {
                            double buff = interval - greyscale1;
                           // if (buff < 0)
                             //   buff = buff * -1;
                            nilai_skewness_buffer = (Math.pow(buff,3)) * prob[interval];
                            nilai_skewness = nilai_skewness + Math.floor(nilai_skewness_buffer);
                            nilai_skewness = Math.floor(nilai_skewness / pangkat3_variance);

                        }

                        //info.setText("Ukuran gambar = " + lebar + " x " + tinggi + " piksel\n" + "Nilai Fitur A (greyscale) = " + greyscale1 + "\n" + "Nilai Fitur B (variance) = " + nilai_invariance + "\n" + "Nilai Fitur C (entropy) = " + nilai_entropy + "\n" + "Nilai Fitur D (skewness) = " + nilai_skewness + "\n" + "Pangkat 3 invariance = " + pangkat3_variance);

                        //-------------------------

                        //fitur kelima (relative smoothness)
                        relative_smoothness = 0;
                        relative_smoothness_buffer = 0;
                        relative_smoothness_buffer = 1 - (1/ (1+nilai_invariance));

                        relative_smoothness = Math.round(relative_smoothness_buffer * 10000)/10000;

                        //info.setText("Ukuran gambar = " + lebar + " x " + tinggi + " piksel\n" + "Nilai Fitur A (greyscale) = " + greyscale1 + "\n" + "Nilai Fitur B (variance) = " + nilai_invariance + "\n" + "Nilai Fitur C (entropy) = " + nilai_entropy + "\n" + "Nilai Fitur D (skewness) = " + nilai_skewness + "\n" + "Nilai Fitur E (relative smoothness) = " + relative_smoothness);

                        //fitur keenam (Energy)
                        nilai_energy = 0;
                        nilai_energy_buffer = 0;
                        for(int interval=0; interval<256; interval++)
                        {
                            nilai_energy_buffer = Math.pow(prob[interval],2);
                            nilai_energy = nilai_energy + nilai_energy_buffer;
                            nilai_energy = Math.floor(nilai_energy);
                        }
                        // info.setText("Ukuran gambar = " + lebar + " x " + tinggi + " piksel\n" + "Nilai Fitur A (greyscale) = " + greyscale1 + "\n" + "Nilai Fitur B (variance) = " + nilai_invariance + "\n" + "Nilai Fitur C (entropy) = " + nilai_entropy + "\n" + "Nilai Fitur D (skewness) = " + nilai_skewness + "\n" + "Nilai Fitur E (relative smoothness) = " + relative_smoothness + "\n" + "Nilai Fitur F (energy) = " + nilai_energy);


                        //fitur ketujuh Contrast
                        nilai_contrast = 0;
                        nilai_contrast_buffer = 0;

                        nilai_contrast = greyscale1 * relative_smoothness_buffer;


                        //info.setText("Image size = " + lebar + " x " + tinggi + " pixel\n" + "Mean = " + greyscale1 + "\n" + "Variance = " + nilai_invariance + "\n" + "Entropy = " + nilai_entropy + "\n" + "Skewness = " + nilai_skewness + "\n" + "Relative Smoothness = " + relative_smoothness + "\n" + "Energy = " + nilai_energy + "\n" + "Contrast = " + nilai_contrast);
                        //info.setText("Image size = " + lebar + " x " + tinggi + " pixel\n" + "Mean = " + greyscale1 + "\n" + "Variance = " + nilai_invariance + "\n" + "Entropy = " + nilai_entropy + "\n" + "Skewness = " + nilai_skewness + "\n" + "Relative Smoothness = " + relative_smoothness);
                        //info.setText("Prediction :" + "\n" + "Distance | Owner" + "\n" + "0.33 | Anung" + "\n" + "0.32 | Anung" + "\n" + "0.41 | Sabil" + "\n" + "0.01 | Dian" + "\n" + "0.5 | Sabil" + "\n" + "0.10 | Dian" + "\n" + "0.09 | Syaifudin" + "\n" + "0.03 | Syaikhu" + "\n" + "0.15 | Sabil" + "\n" + "0.09 | Syaikhu");
                        Toast.makeText(cekDokumen.this,"Feature value has been checked!",Toast.LENGTH_LONG).show();

                        System.out.println("\nISI EXTRACT FITUR DATA TEST sebelum di 0 kan : \n"+"\n Greyscale : "+greyscale1+"\n Invariance : "+nilai_invariance+"\n Entropy : "
                                +nilai_entropy+"\n Skewness :"+nilai_skewness+"\n Relative Smoothness : "+relative_smoothness+"\n Energy :"+nilai_energy+"\n Contrast :"+nilai_contrast);


                        Log.i("NILAI ENTROPY sebelum diproses : ", Double.toString(nilai_entropy));
                        if(Double.toString(nilai_entropy).equals("NaN"))
                            nilai_entropy=0;
                        Log.i("NILAI ENTROPY jadi 0 : ", Double.toString(nilai_entropy));


                        Log.i("NILAI Skewness sebelum diproses : ", Double.toString(nilai_skewness));
                        if(Double.toString(nilai_skewness).equals("NaN") || Double.toString(nilai_skewness).equals("-Infinity") || Double.toString(nilai_skewness).equals("Infinity"))
                            nilai_skewness=0;
                        Log.i("NILAI Skewness jadi 0 : ", Double.toString(nilai_skewness));

                        System.out.println("\nISI EXTRACT FITUR DATA TEST SETELAH di 0 kan : \n"+"\n Greyscale : "+greyscale1+"\n Invariance : "+nilai_invariance+"\n Entropy : "
                                +nilai_entropy+"\n Skewness :"+nilai_skewness+"\n Relative Smoothness : "+relative_smoothness+"\n Energy :"+nilai_energy+"\n Contrast :"+nilai_contrast);



                    }







                }
        );
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DeKa");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_DeKa_" + timeStamp + ".jpg");

        return mediaFile;
    }



    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void viewAll() {
        button3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Tidak ditemukan");
                            return;
                        }



                        cacah = 0;
                        StringBuffer buffer = new StringBuffer();
                        ttt = new StringBuffer();
                        StringBuffer xxx = new StringBuffer();

                        while (res.moveToNext()) {
                            cacah = cacah + 1;

                            //ttt.append(cacah);
                            // buffer.append("Id :"+ res.getString(0)+"\n");
                            //buffer.append("No.KTP :"+ res.getString(1)+"\n");
                            //buffer.append("Fitur A :"+ res.getString(2)+"\n");
                            //buffer.append("Fitur B :"+ res.getString(3)+"\n");
                            //buffer.append("Fitur C :"+ res.getString(4)+"\n");
                            //buffer.append("Fitur D :"+ res.getString(5)+"\n");
                            //buffer.append("Fitur E :"+ res.getString(6)+"\n");
                            //buffer.append("Fitur F :"+ res.getString(7)+"\n");
                            //buffer.append("Fitur G :"+ res.getString(8)+"\n\n");

                            buffer.append(res.getString(0)+" | " + res.getString(1)+" | " + res.getString(2)+" | "+ res.getString(3)+" | "+ res.getString(4)+" | "+ res.getString(5)+" | "+ res.getString(6)+" | "+ res.getString(7)+" | "+ res.getString(8)+ " | "+ res.getString(9)+ " | "+ res.getString(10)+" \n " );

                            id =res.getInt(0);
                            nilai_mean = res.getDouble(4);
                            nilai_invar = res.getDouble(5);
                            nilai_entro = res.getDouble(6);
                            nilai_skew = res.getDouble(7);
                            nilai_smooth = res.getDouble(8);

                            nilai_ener = res.getDouble(9);
                            nilai_contra = res.getDouble(10);

                            //nama_pemilik[cacah] = res.getString(1);

                           // jarak[cacah] = Math.round(Math.sqrt((Math.pow(greyscale1 - nilai_mean,2) + Math.pow(nilai_invariance - nilai_invar,2) + Math.pow(nilai_entropy - nilai_entro, 2) + Math.pow(nilai_skewness - nilai_skew, 2)+ Math.pow(relative_smoothness - nilai_smooth, 2) + Math.pow(nilai_energy - nilai_ener, 2) + Math.pow(nilai_contrast - nilai_contra, 2)))*100)/100;
                            jarak[cacah] = Math.round(Math.sqrt(Math.pow(greyscale1 - nilai_mean,2) + Math.pow(nilai_invariance - nilai_invar,2)+ Math.pow(relative_smoothness - nilai_smooth, 2) + Math.pow(nilai_energy - nilai_ener, 2) + Math.pow(nilai_contrast - nilai_contra, 2))*100)/100;
                            jarak_buffer[cacah] = jarak[cacah];
                            //jarak[cacah] = nilai_mean;
                            //info.setText("nilai mean:" + nilai_mean + "\n" + "nilai variance:" + nilai_invar + "\n" + "nilai entropy : " + nilai_entro + "\n" + "nilai skewness : " + nilai_skew + "\n" + "nilai smoothness :" + nilai_smooth);
                           jumlah_data = cacah;
                            ttt.append(nama_pemilik[cacah] + " : " + jarak[cacah] +"\n");
                        }
                        //info.setText("jarak 1 : " + jarak[1] + "\n" + "Jarak 2 : " + jarak[2] + "\n" + "Jarak 3 : " + jarak[3] + "\n" + "Jarak 4 : " + jarak[4]);
                        for(int i=1; i< cacah; i++)
                        {
                            for(int j=1; j< cacah; j++)
                            {
                                if (jarak_buffer[j+1] < jarak_buffer[j])
                                {
                                        nilai_buffer_min = jarak_buffer[j];
                                        jarak_buffer[j] = jarak_buffer[j+1];
                                        jarak_buffer[j+1] = nilai_buffer_min;
                                }
                            }
                        }
                        jarak_terkecil = jarak_buffer[1];
                        for(int xx=1; xx<= cacah; xx++)
                        {
                            if (jarak[xx] == jarak_terkecil)
                            {
                                xxx.append("Pemilik dokumen : " + nama_pemilik[xx] + " , " + jarak_terkecil);
                                nama_pemilik_dokumen.setText(nama_pemilik[xx]);

                            }
                        }


                            // Show all data
                       // showMessage("Data",buffer.toString());
                        sendPost_data_testing();

                        //showMessage("urutan", xxx.toString());


                    }
                }
        );
    }
    RegisterActivity obj_register = new RegisterActivity();



    public void sendPost_data_testing() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String jsonString="";



                InputStream inputStream = null;
                HttpURLConnection httpURLConnection = null;
                String hasil = null;

                try {


                    URL obj = new URL("http://2f2058116494.ngrok.io");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    // script untuk menangkap string jsonArray yang dipanggil dari class RegisterActivity
                    SharedPreferences settings = getSharedPreferences(
                            "pref", 0);
                    jsonString= settings
                            .getString("jsonString", null);




                    //str_jsonArray_ditambah_data_baru = obj_register.str_jsonarray;

                    Log.i("DATA json dari DB : ", jsonArray_db.toString());



                    clicks=clicks+1;
                    System.out.println("jumlah click tombol TEST:"+clicks);

                    //JSONArray jsonArray_ditambah_data_baru = new JSONArray(jsonArray_db.toString());
                    JSONObject jsonParam = new JSONObject();

                    System.out.println("PANJANG INDEX JSONARRAY dari db :"+jsonArray_db.length());


                    jsonParam.put("NAMA", "");
                    jsonParam.put("NO_GAMBAR", "");
                    jsonParam.put("KTP", "");
                    jsonParam.put("NILAI_GREYSCALE", greyscale1);
                    jsonParam.put("NILAI_INVARIANCE", nilai_invariance);
                    jsonParam.put("NILAI_ENTROPY", nilai_entropy);
                    jsonParam.put("NILAI_SKEWNESS", nilai_skewness);
                    jsonParam.put("RELATIVE_SMOOTHNESS", relative_smoothness);
                    jsonParam.put("NILAI_ENERGY", nilai_energy);
                    jsonParam.put("NILAI_CONTRAST", nilai_contrast);

                    jsonArray_db.put(jsonParam);

                    System.out.println("ISI JSON PARAM test : "+jsonParam.toString());

                    String str_json_array = jsonArray_db.toString();
                    Log.i("DATA yang dikirim ke NGROK : ", str_json_array.substring(str_json_array.length() - 420));


                    os.write(jsonArray_db.toString().getBytes());
                    Log.d(TAG, "BERHASIL KIRIM DATA ");
                    os.flush();
                    os.close();

                    JSONArray jsonArray_hapus_last = new JSONArray();

                    int len_json_before = jsonArray_db.length();

                    System.out.println("Panjang jsonArray sebelum  : "+len_json_before);
                    for (int i = 0; i < len_json_before; i++) {
                        if(i==len_json_before-1) {
                            //JSONObject objek = jsonArray_db.getJSONObject(i);

                            jsonArray_db.remove(i);

                        }
                    }
                    int panjang_jsonArray_setelah_last_dihapus = jsonArray_db.length();
                    System.out.println("Panjang jsonArray setelah remove last : "+panjang_jsonArray_setelah_last_dihapus);
                    //Remove the element from arraylist
                    //list.remove(position);
                    //Recreate JSON Array
                    //JSONArray jsArray = new JSONArray(list);



                    int responseCode = con.getResponseCode();
                    System.out.println("POST Response Code :: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) { //success
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // print result
                        String output_respon=response.toString();
                        System.out.println("ISI output yang dikembalikan NGROK = "+output_respon);
                        Log.d(TAG, "DATA DIAMBIL dari Flask-NGROK: "+output_respon);

                        String strNew = output_respon.substring(1, output_respon.length()-1);

                        String[] parts = strNew.split(",");
                        Kelas = parts[0];
                        Nama = parts[1];



                        Log.d(TAG, "Data gambar uji, masuk ke dalam kelas : "+Kelas);
                        Log.d(TAG, "NAMA PEMILIKNYA : "+Nama);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(cekDokumen.this);
                                builder.setCancelable(true);
                                builder.setTitle("LVQ prediction results :");
                                builder.setMessage("\n\nOwner :\n\n"+Nama);
                                builder.show();
                            }
                        });



                        nama_pemilik_dokumen.setText(Nama);

                    } else {
                        System.out.println("POST request not worked");
                        Log.d(TAG, "GAGAL DIAMBIL: ");
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        });

        thread.start();

          //  showMessage("Hasil prediksi dan jarak terdekat :","\n\nPrediksi Nama Pemilik :\n\n"+Nama+"\n\nMasuk ke kelas :\n\n"+Kelas+"\n\njarak setiap data\n\n"+ ttt.toString());




    }


}
