package com.hagilap.talipaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView mainWebveiw;
    public ValueCallback<Uri[]> mfilePathCallback;
    private final static int FILECHOOSER_RESULTCODE=1;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mfilePathCallback != null) {
                Uri[] results = null;

                if (resultCode == Activity.RESULT_OK) {
                    // Get the URI of the selected file(s)
                    if (data != null) {
                        String dataString = data.getDataString();
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            results = new Uri[clipData.getItemCount()];
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                results[i] = clipData.getItemAt(i).getUri();
                            }
                        } else if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                // Return the selected file(s) to the WebView
                mfilePathCallback.onReceiveValue(results);
                mfilePathCallback = null;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onBackPressed() {
        if (mainWebveiw.canGoBack()) {
            mainWebveiw.goBack();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainWebveiw = findViewById(R.id.mainWebView);
        mainWebveiw.loadUrl("https://app.talipaapp.com/");
        mainWebveiw.setWebViewClient(new myWebClient());
        WebSettings webSettings = mainWebveiw.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setDomStorageEnabled(true);
        MainActivity app = this;

        mainWebveiw.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("MyApplication", consoleMessage.message() + " -- From line " +
                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                app.startActivityForResult(intent, FILECHOOSER_RESULTCODE);

                app.mfilePathCallback = filePathCallback;

                return true;
            }

        });

//        webSettings.setPluginState(WebSettings.PluginState.ON);
        try {
            MyServer server = new MyServer(this);
            Thread t = new Thread(server);
            t.start();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
//        this.getActionBar().hide();
    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            if (url.startsWith("file:")) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILECHOOSER_RESULTCODE);
            }
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

//        progressBar.setVisibility(View.GONE);
        }
    }
}

