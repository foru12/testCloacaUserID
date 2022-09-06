package com.example.testcloacauserid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    Activity activity;
    String URL;

    public Settings(Activity activity, String URL) {
        this.activity = activity;
        this.URL = URL;
    }

    public void webSetings(){
        WebView webView = activity.findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + " MobileAppClient/Android/0.9");

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        CookieSyncManager.createInstance(activity);
        CookieSyncManager.getInstance().startSync();

        webView.setWebViewClient(new WebViewClient() {

            String lastUrl = "";

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                lastUrl = url;
                CookieManager.getInstance().flush();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if ( url.startsWith("mailto") ) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { url.replace("mailto:", "") }); activity.startActivity(Intent.createChooser(intent, "Mail to Support"));

                    return false;
                } else if ( url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    activity.startActivity(intent);

                    return false;
                } else if (url.startsWith("https://t.me/joinchat")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); activity.startActivity(intent);

                    return false;
                } else {
                    Map headers = new HashMap<>();
                    if (lastUrl != null) {
                        headers.put("Referer", lastUrl);
                    }
                    view.loadUrl(url, headers);
                    lastUrl = url;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.setVisibility(View.VISIBLE);

        webView.loadUrl(URL);

    }
}
