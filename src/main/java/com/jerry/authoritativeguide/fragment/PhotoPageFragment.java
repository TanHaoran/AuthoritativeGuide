package com.jerry.authoritativeguide.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jerry.authoritativeguide.R;

/**
 * Created by Jerry on 2017/1/19.
 */

public class PhotoPageFragment extends VisibleFragment {

    private static final String ARGS_PHOTO_PAGE_URI = "args_photo_page_uri";

    public WebView mWebView;
    private ProgressBar mProgressBar;
    private Uri mUri;

    public static PhotoPageFragment newInstance(Uri uri) {

        Bundle args = new Bundle();
        args.putParcelable(ARGS_PHOTO_PAGE_URI, uri);

        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUri = getArguments().getParcelable(ARGS_PHOTO_PAGE_URI);

        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);
        mWebView = (WebView) v.findViewById(R.id.wv_photo_page);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb_photo_page);
        mProgressBar.setMax(100);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(title);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                Uri uri = request.getUrl();
                if (uri.toString().startsWith("http") || uri.toString().startsWith("https")) {
                    return false;
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                }
            }
        });
        mWebView.loadUrl(mUri.toString());

        return v;
    }
}
