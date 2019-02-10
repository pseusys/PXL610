package com.ekdorn.pixel610.pixeldungeon.internet;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;

public class GiftDialog extends Dialog {
    public GiftDialog(Context context) {
        super(context);

        WebView donate = new WebView(context);
        donate.getSettings().setJavaScriptEnabled(true);
        donate.setWebViewClient(new MyWebViewClient());
        donate.loadUrl("https://classic-dungeon-50052917.firebaseapp.com/");

        this.addContentView(donate, new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
    }



    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        // Для старых устройств
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}