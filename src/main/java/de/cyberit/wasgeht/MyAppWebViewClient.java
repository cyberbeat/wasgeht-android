package de.cyberit.wasgeht;

import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyAppWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        System.out.println(url);
        if (url.startsWith("mailto:")) {
            try {
                MailTo mt = MailTo.parse(url);
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.setType("message/rfc822");
                mail.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
                view.getContext().startActivity(mail);
            } catch (Exception e) {
            }
            return true;
        } else if (Uri.parse(url) != null && Uri.parse(url).getHost().startsWith("www.wasgehtapp.de")) {
            return false;
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
            } catch (Exception e) {
            }
            return true;
        }
    }



}