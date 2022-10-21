package de.cyberit.wasgeht;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.webkit.JavascriptInterface;

import org.json.JSONObject;

public class JSObject {

    Context mContext;

    JSObject(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void share(String title, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setData(Uri.parse(url));
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TITLE, title);
            intent.putExtra(Intent.EXTRA_TEXT, title + ":\n" + url);
            intent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(intent, "Veranstaltung teilen");
        mContext.startActivity(shareIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void addEvent(String eventJson) {
        try {
            JSONObject event = new JSONObject(eventJson);
            Intent intent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getLong("datum"))
                        .putExtra(Events.TITLE, event.getString("titel"))
                        .putExtra(Events.DESCRIPTION, event.getString("beschreibung"))
                        .putExtra(Events.EVENT_LOCATION, event.getString("location"))
                        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
            } else {
                intent = new Intent(Intent.ACTION_INSERT)
                        .setType("vnd.android.cursor.item/event")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getLong("datum"))
                        .putExtra(Events.TITLE, event.getString("titel"))
                        .putExtra(Events.DESCRIPTION, event.getString("beschreibung"))
                        .putExtra(Events.EVENT_LOCATION, event.getString("location"))
                        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
            }
            mContext.startActivity(intent);
        } catch (Exception e) {

        }
    }
}
