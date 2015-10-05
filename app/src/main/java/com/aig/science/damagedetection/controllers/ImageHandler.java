package com.acelinkedin.imageloader.forlinkedin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
 */

public class MainActivity extends AppCompatActivity {
    final static String src[] = {
            "http://2.bp.blogspot.com/--1K042_Nqow/VTeCrXE5BWI/AAAAAAAANL8/XLQo5gp6Zyg/s1600/_01xx.png",
            "http://2.bp.blogspot.com/-CP_IoxhknOQ/VTeCrag0-eI/AAAAAAAANMk/ruCd93vcqog/s1600/_02.png",
            "http://4.bp.blogspot.com/-1SEeDGnn7D4/VTeCrciCRNI/AAAAAAAANMA/QgFw2Jf_lm0/s1600/_03.png",
            "http://2.bp.blogspot.com/-vHYm6GYJ2Tw/VTeCsNtXFTI/AAAAAAAANMI/ArqK_hgjPew/s1600/_04.png",
            "http://1.bp.blogspot.com/-VnWqZvcqNOs/VTeCse3md5I/AAAAAAAANMM/LTXVII_Wp9k/s1600/_05.png",
            "http://3.bp.blogspot.com/-4LESmLLwFjU/VTeCs45dC4I/AAAAAAAANMg/r09uJJWk6qA/s1600/_06.png",
            "http://4.bp.blogspot.com/-AMGvPNdG6j0/VTeCs2swa2I/AAAAAAAANMc/NIg_i_2Et-A/s1600/_07.png",
            "http://4.bp.blogspot.com/-B59pQ2j1EtM/VTeCtsyD_QI/AAAAAAAANMo/7RvxFyINnr8/s1600/_08.png",
            "http://2.bp.blogspot.com/-VIUzes38Bro/VTeCuBX3dvI/AAAAAAAANMs/kiJM9ldSVho/s1600/_09.png",
            "http://4.bp.blogspot.com/-v618GyhX5U4/VTeCubGY43I/AAAAAAAANMw/X0uDVglfi1Q/s1600/_10.png",
            "http://2.bp.blogspot.com/-nhmSxu382f0/VTeCueNlxUI/AAAAAAAANM0/aan0OhOAStc/s1600/_11.png",
            "http://4.bp.blogspot.com/-WhB2MPTlGJk/VTeCuiosK6I/AAAAAAAANM4/ZdThfC14dOA/s1600/_12.png"};
    ListView imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        imageList = (ListView) findViewById(R.id.imagelist);
        ArrayList<String> srcList = new ArrayList<String>(Arrays.asList(src));
        imageList.setAdapter(new CustomListAdapter(this, srcList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomListAdapter extends BaseAdapter {
        private ArrayList<String> listData;
        private LayoutInflater layoutInflater;

        public CustomListAdapter(Context context, ArrayList<String> listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.row, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(String.valueOf(position));

            if (holder.icon != null) {
                new BitmapWorkerTask(holder.icon).execute(listData.get(position));
            }
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView text;
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String imageUrl;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            return LoadImage(imageUrl);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        private Bitmap LoadImage(String URL) {
            Bitmap bitmap = null;
            InputStream in = null;
            try {
                in = OpenHttpConnection(URL);
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
            } catch (IOException e1) {
            }
            return bitmap;
        }

        private InputStream OpenHttpConnection(String strURL)
                throws IOException {
            InputStream inputStream = null;
            URL url = new URL(strURL);
            URLConnection conn = url.openConnection();

            try {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                }
            } catch (Exception ex) {
            }
            return inputStream;
        }
    }
}
