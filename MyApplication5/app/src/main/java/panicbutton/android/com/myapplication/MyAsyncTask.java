package panicbutton.android.com.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by dell on 2017/6/22.
 */

public class MyAsyncTask extends AsyncTask<String,Integer,Bitmap> {
    private ProgressBar mProgressBar;//进度条
    private ImageView mInageView;//图片显示控件

    public MyAsyncTask(ProgressBar pb, ImageView iv) {
        mProgressBar = pb;
        mInageView = iv;
    }

    @Override
    protected void onPreExecute() { //启动下载时调用，运行在主线程中
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... params) { //异步操作时调用，运行在子线程中
        String urlParams = params[0];//拿到execute()传过来的图片url
        Bitmap bitmap = null;
        URLConnection conn = null;
        InputStream is = null;
        try{
            URL url = new URL(urlParams);
            conn = url.openConnection();
            is = conn.getInputStream();
            //这里只是为了延时更新进度的功能，实际的进度只需要在输入流中读取时逐步获取

            for(int i = 0; i < 100; i++) {
                if(isCancelled()) {
                    break;
                }
                publishProgress(i);
                Thread.sleep(50); //为了看清效果，睡眠一段时间
            }
            //将获取的输入流转成Bitmap
            BufferedInputStream bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);
            is.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {  //在下载操作doInBackground中调用publishProgress()方法时的回调方法，用于更新下载进度，运行在主线程中
        super.onProgressUpdate(values);
        if(isCancelled()) {
            return;
        }
        mProgressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) { //后台下载操作完成后调用，运行在主线程中
        super.onPostExecute(bitmap);
        mProgressBar.setVisibility(View.GONE);
        mInageView.setImageBitmap(bitmap);
    }
}
