package com.example.dian.handwritingsecuritysystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dian.handwritingsecuritysystem.model.NilaiFitur;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.HashMap;
import java.util.Locale;

import static android.app.PendingIntent.getActivity;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Intent intent;
    Uri fileUri;
    Button btn_choose_image;
    Button btn_proses;
    Button btn_simpan,btn_lihatData;
    ImageView imageView;
    Bitmap bitmap, decoded;
    private TextView textView;



    public EditText idtest;
    public EditText no_ktp, nama, no_hp;
    public TextView info;
    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;

    public int lebar;
    public int tinggi;

    int bitmap_size = 40; // image quality 1 - 100;
    int max_resolution_image = 800;

    double[][] rgb = new double[500][200];
    double[] prob = new double[256];

    public static String namaPemilik;
    public String noHP;
    public String ktp;
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
    int clicks = 0;


    private static final String TAG = "main";
    static StringBuilder sb_data_fitur = new StringBuilder();
    JSONArray jsonArray=new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myDb = new DatabaseHelper(this);

        imageView = (ImageView) findViewById(R.id.image_view);
        textView = (TextView) findViewById(R.id.text);
        btn_proses = (Button)findViewById(R.id.button_proses) ;
        btn_simpan = (Button)findViewById(R.id.button_simpan);
        btn_lihatData = (Button) findViewById(R.id.button_view);

        info = (TextView)findViewById(R.id.textView2);
        no_ktp = (EditText)findViewById(R.id.no_ktp) ;
        nama = (EditText)findViewById(R.id.nama) ;
        no_hp = (EditText)findViewById(R.id.no_hp) ;
//idtest = (EditText)findViewById(R.id.no_id);

        // imageView.setDrawingCacheEnabled(true);
        //  imageView.buildDrawingCache(true);

        //ambil_fitur();

        AddData();
        viewAll();

        btn_choose_image = (Button) findViewById(R.id.btn_choose_image);
        imageView = (ImageView) findViewById(R.id.image_view);

        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private void selectImage() {
        imageView.setImageResource(0);
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                    bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), data.getData());
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
        btn_proses.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        namaPemilik = nama.getText().toString();
                        noHP = no_hp.getText().toString();
                        ktp = no_ktp.getText().toString();



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
                        info.setText("Image size = " + lebar + " x " + tinggi + " pixel\n" + "Mean = " + greyscale1 + "\n" + "Variance = " + nilai_invariance + "\n" + "Entropy = " + nilai_entropy + "\n" + "Skewness = " + nilai_skewness + "\n" + "Relative Smoothness = " + relative_smoothness);



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



    public  void AddData() {

        btn_simpan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //DatabaseHelper dbb = new DatabaseHelper();
                        //DatabaseHelper.insertData("training",null ,nama.getText().toString(), no_hp.getText().toString(), no_ktp.getText().toString(), greyscale1,nilai_invariance, nilai_entropy, nilai_skewness, relative_smoothness, nilai_energy, nilai_contrast);

                        Log.i("NILAI ENTROPY sebelum diproses : ", Double.toString(nilai_entropy));
                        if(Double.toString(nilai_entropy).equals("NaN"))
                            nilai_entropy=0;
                        Log.i("NILAI ENTROPY jadi 0 : ", Double.toString(nilai_entropy));

                        Log.i("NILAI Skewness sebelum diproses : ", Double.toString(nilai_skewness));
                        if(Double.toString(nilai_skewness).equals("NaN") || Double.toString(nilai_skewness).equals("-Infinity") || Double.toString(nilai_skewness).equals("Infinity"))
                            nilai_skewness=0;
                        Log.i("NILAI Skewness jadi 0 : ", Double.toString(nilai_skewness));

                        boolean isInserted = myDb.insertData( nama.getText().toString(), no_hp.getText().toString(), no_ktp.getText().toString(), greyscale1,nilai_invariance, nilai_entropy, nilai_skewness, relative_smoothness, nilai_energy, nilai_contrast);
                        //boolean isInserted = myDb.insertData(idtest.getText().toString(), no_ktp.getText().toString());

                        //membentuk_paket_data_fitur();

                        /*
                        JSONObject jsonParam = new JSONObject();
                        try {
                            jsonParam.put("NAMA",namaPemilik);
                            jsonParam.put("NO_HP",noHP.toString());
                            jsonParam.put("KTP",ktp.toString());
                            jsonParam.put("NILAI_GREYSCALE",greyscale1);
                            jsonParam.put("NILAI_INVARIANCE",nilai_invariance);
                            jsonParam.put("NILAI_ENTROPY",nilai_entropy);
                            jsonParam.put("NILAI_SKEWNESS",nilai_skewness);
                            jsonParam.put("RELATIVE_SMOOTHNESS",relative_smoothness);
                            jsonParam.put("NILAI_ENERGY",nilai_energy);
                            jsonParam.put("NILAI_CONTRAST",nilai_contrast);

                            jsonArray.put(jsonParam);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        */

                        clicks=clicks+1;


                          /*
                            String str_jsonarray = jsonArray.toString();

                            // script untuk mempersiapkan string jsonArray yang mau dipanggil di class lain
                            SharedPreferences settings = getSharedPreferences(
                                    "pref", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("jsonString", str_jsonarray);
                            editor.commit();
                            */



                            System.out.println("jumlah click :"+clicks);



                        // myDb.insertBitmap(editTextId.getText().toString(), imageView.setImageBitmap(bitmap););
                        if(isInserted == true)
                            Toast.makeText(RegisterActivity.this,"A new data has been added successfully!",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(RegisterActivity.this,"A new data has been failed to added",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }






    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void viewAll() {
        btn_lihatData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Tidak ditemukan");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            // buffer.append("Id :"+ res.getString(0)+"\n");
                            //buffer.append("No.KTP :"+ res.getString(1)+"\n");
                            //buffer.append("Fitur A :"+ res.getString(2)+"\n");
                            //buffer.append("Fitur B :"+ res.getString(3)+"\n");
                            //buffer.append("Fitur C :"+ res.getString(4)+"\n");
                            //buffer.append("Fitur D :"+ res.getString(5)+"\n");
                            //buffer.append("Fitur E :"+ res.getString(6)+"\n");
                            //buffer.append("Fitur F :"+ res.getString(7)+"\n");
                            //buffer.append("Fitur G :"+ res.getString(8)+"\n\n");

                            buffer.append(res.getString(0)+" | " + res.getString(1)+" | " + res.getString(2)+" | "+ res.getString(3)+" | "+ res.getString(4)+" | "+ res.getString(5)+" | "+ res.getString(6)+ " | "+ res.getString(7) + " | "+ res.getString(8)+" | "+ res.getString(9)+ " | "+ res.getString(10)+" \n\n " );
                        }

                        // Show all data
                        showMessage("Data \n","\n ID | Name |  Image Number | ID card | Greyscale \n | Invariance | Entropy | " +
                                " Skewness | Relative Smoothness | Energy | Contrast \n\n\n"+buffer.toString());
                        /*
                        try {
                            JSONObject obj = new JSONObject(loadJSONFromAsset());
                            JSONArray m_jArry = obj.getJSONArray("formules");
                            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
                            HashMap<String, String> m_li;

                            for (int i = 0; i < m_jArry.length(); i++) {
                                JSONObject jo_inside = m_jArry.getJSONObject(i);
                                Log.d("Details-->", jo_inside.getString("formule"));
                                String formula_value = jo_inside.getString("formule");
                                String url_value = jo_inside.getString("url");

                                //Add your values in your `ArrayList` as below:
                                m_li = new HashMap<String, String>();
                                m_li.put("formule", formula_value);
                                m_li.put("url", url_value);

                                formList.add(m_li);
                                Log.d("ISI JSON = ", String.valueOf(formList));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                         */
                    }
                }
        );
    }



    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                HttpURLConnection httpURLConnection = null;
                String hasil = null;

                try {
                    //URL url = new URL("http://ptsv2.com/t/xflhz-1591625971/post");
                    /*
                    URL url = new URL("http://stevenss.pythonanywhere.com/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept","application/json");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    */



                    /*
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonArray.toString(), "UTF-8"));
                    os.write(jsonArray.toString().getBytes("UTF-8"));
                    //os.writeBytes(jsonArray.toString());
                   
                    os.flush();
                    os.close();
                    */

                    URL obj = new URL("http://stevenss.pythonanywhere.com/");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    //con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                    //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    //os.write(jsonArray.toString().getBytes());
                    //String data_fitur = "Nama=Steven";
                    //String str_json_array = jsonArray.toString();
                    //Log.i("DATA yang dikirim ke Web : ", str_json_array);

                    String str_sb_fitur = sb_data_fitur.toString();
                    Log.i("SB yang dibentuk ke JSON : ", str_sb_fitur);
                    // os.write(data_fitur.getBytes());

                    os.write(jsonArray.toString().getBytes());
                    Log.d(TAG, "BERHASIL KIRIM POST: ");
                    os.flush();
                    os.close();

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
                      //  System.out.println(response.toString());
                       // Log.d(TAG, "ISI DATA DIAMBIL: "+response.toString());

                    } else {
                        System.out.println("POST request not worked");
                        Log.d(TAG, "GAGAL DIAMBIL: ");
                    }




                    // Retrieve the response body as an InputStream.
/*

                    //conn.disconnect();
                    // conn2.disconnect();

*/
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        });

        thread.start();
    }



}

