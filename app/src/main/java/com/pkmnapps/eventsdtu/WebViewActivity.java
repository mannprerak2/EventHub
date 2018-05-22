package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.nio.file.WatchEvent;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    SwipeRefreshLayout swp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        webView = (WebView)findViewById(R.id.webView);
        swp = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swp.setRefreshing(true);
                invalidateOptionsMenu();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swp.setRefreshing(false);
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                swp.setRefreshing(false);
                invalidateOptionsMenu();
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);

       // LoadWebsite(getIntent().getStringExtra("website"));

    }

    private void back() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    private void forward() {
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    private void setTitles(String title,String subtitle){
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
    }

    private void LoadWebsite(String s){
        webView.loadUrl(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.open_with:
                //open externally here...
                return true;
            case R.id.copy_link:
                //copy link to clipboard here...
                Snackbar.make(webView,"Link Copied to Clipboard",Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.back:
                back();
                return true;
            case R.id.forward:
                forward();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }


    }

}
