package ru.mihver1.android.yaph;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by mihver1 on 19.01.14.
 */
public class ImagePage extends Activity {

    public static int W, H;

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        W = size.x;
        H = size.y;

        Bundle bundle = getIntent().getExtras();

        ImageView fullscreen = (ImageView) findViewById(R.id.imageView2);
        fullscreen.setTag(bundle.getString("image"));
        fullscreen.setImageResource(R.drawable.placeholder);

        Log.d("YOLO", bundle.getString("image"));

        new DownloadTask(fullscreen, bundle.getString("image")).execute();
    }

    private class DownloadTask extends AsyncTask<Void, Void, Bitmap> {
        final ImageView view;
        final String url;

        private DownloadTask(ImageView view, String url) {
            this.view = view;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Bitmap temp;
                if(isOnline())
                    temp = BitmapFactory.decodeStream(new URL(url).openStream());
                else
                    temp = BitmapFactory.decodeFile(url);

                int w,h;
                w = temp.getWidth();
                h = temp.getHeight();
                double ratio = 1.0f;
                if(w > W || h > H) {
                    ratio = Math.min(W*1.0 / w, H*1.0 / h);
                    Log.d("YOLO", ">>"+Integer.toString((int)(w * ratio))+" "+Integer.toString((int)(h * ratio)));
                }
                return Bitmap.createScaledBitmap(temp, (int)(w * ratio), (int)(h * ratio), false);
            } catch (IOException e) {
                return null;
            } catch (NullPointerException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap == null)
                return;
            if(view.getTag() == url) {
                view.setImageBitmap(bitmap);
            }
        }
    }
}