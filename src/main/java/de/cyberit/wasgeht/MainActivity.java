package de.cyberit.wasgeht;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import static de.cyberit.wasgeht.R.id.activity_main_webview;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    private String MY_URL = "https://www.wasgehtapp.de/webapp/";

    public void setStartPage() {
        CookieManager cookieManager = CookieManager.getInstance();

        String cookiesString = cookieManager.getCookie(MY_URL);

        if (cookiesString!=null && cookiesString.contains("webapp=off")){
            Log.d("Wasgehtapp", "Old Design");
            MY_URL = "https://www.wasgehtapp.de/";
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStartPage();

        String myUrl = MY_URL;
        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                myUrl = intent.getDataString();
            }
        }

        setContentView(R.layout.activity_main);
        mWebView = findViewById(activity_main_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setWebViewClient(new MyAppWebViewClient());
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // callback.invoke(String origin, boolean allow, boolean remember);
                callback.invoke(origin, true, true);
            }
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("WasGehtApp", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });
        mWebView.addJavascriptInterface(new JSObject(this),"WasGehtApp");

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Standortbestimmung in WasGehtAPP")
                            .setMessage(R.string.geo_location_text)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                }
            } else {
                mWebView.loadUrl(myUrl);
            }
        } else {

            mWebView.loadUrl(myUrl);
        }

    }

	@Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null)
            mWebView.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                mWebView.loadUrl(MY_URL);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
