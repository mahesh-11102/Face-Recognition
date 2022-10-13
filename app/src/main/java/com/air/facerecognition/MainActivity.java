package com.air.facerecognition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final int GALLERY_REQ_CODE_1 = 100, GALLERY_REQ_CODE_2 = 101;
    ImageView i1, i2;
    Button btn;
    String url = "http://15ad-34-86-121-254.ngrok.io/predict";
    String encd1 = null, encd2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i1 = findViewById(R.id.imageView1);
        i2 = findViewById(R.id.imageView2);
        btn = findViewById(R.id.button);


        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(
                        Intent.createChooser(
                                i,
                                "Select Picture"
                        ),
                        GALLERY_REQ_CODE_1
                );
            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(
                        Intent.createChooser(
                                i,
                                "Select Picture"
                        ),
                        GALLERY_REQ_CODE_2

                );
                Log.d("Image2", encd1 + " " + encd2);

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                RequestQueue rq = Volley.newRequestQueue(this);

                ImageRequest im = new ImageRequest(
                        Request.Method.POST,url, new  Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.
                    }
                }
                  new Response.Listener<>(){
                            @Override
                            public void onResponse(Bitmap response) {
                                ImageView imageView = (ImageView) findViewById(
                                        R.id.imageView1);

                                imageView.setImageBitmap(response);
                            }

                        }, 300, 300,null

                );
                requestQueue.ad(im)
                startActivity(
                        new Intent(
                        getApplicationContext(),
                        activity_final_selction.class)
                );
                */
                Log.d("Image", (encd1 != null)+" ");

                if(encd2 != null && encd1 != null)
                    SendImage(encd1,encd2);
                else
                    Toast.makeText(getApplicationContext(), "No Image is Selcted", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SendImage(final String image1,final String image2) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("uploade", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("Result");

                            Intent i = new Intent(MainActivity.this, activity_final_selction.class);
                            i.putExtra("Result", data);
                            startActivity(i);

                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                params.put("img1", image1);
                params.put("img2", image2);
                return params;
            }
        };

        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            Uri path = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
//                Log.d("path",requestCode+" \n"+bitmap+" \n"+path);
                Bitmap lastBitmap = null;
                if (requestCode == GALLERY_REQ_CODE_1) {
                    i1.setImageBitmap(bitmap);
                    lastBitmap = bitmap;
                    encd1 = getStringImage(lastBitmap);
                }
                if (requestCode == GALLERY_REQ_CODE_2) {
                    i2.setImageBitmap(bitmap);
                    lastBitmap = bitmap;
                    encd1 = getStringImage(lastBitmap);
                }
                if(encd2 != null && encd1 != null)
                    SendImage(encd1,encd2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
}