package panicbutton.android.com.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private static String URL = "http://c.hiphotos.baidu.com/baike/s%3D220/sign=86442af5a6c27d1ea1263cc62bd4adaf/42a98226cffc1e17d8f914604890f603738de919.jpg";
    private MyAsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.id_image);
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        asyncTask = new MyAsyncTask(mProgressBar,mImageView);
        asyncTask.execute(URL);
    }
    public void loadImage(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            asyncTask.cancel(true);
        }
    }
}
