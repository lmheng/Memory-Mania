package com.example.androidcalm;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;


public class DownloadService extends Service {
    Thread background;
    public volatile boolean running ;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        running=true;
        background = new Thread(new Runnable() {
                @Override
                public void run() {
                        String website = intent.getStringExtra("website");
                        try {
                            URL webUrl = new URL(website);
                            URLConnection webConnection = webUrl.openConnection();
                            webConnection.setConnectTimeout(5000);
                            webConnection.setReadTimeout(5000);
                            webConnection.connect();
                            InputStream in = webConnection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String line = "";
                            int count = 1;
                            while ((line = reader.readLine()) != null && count <= 20 ) {
                                if(running==false) {
                                    reader.close();
                                    in.close();
                                    Intent intent = new Intent();
                                    intent.setAction("ThreadInterupt");
                                    sendBroadcast(intent);
                                    return;
                                }
                                int imgIndex = line.indexOf("img src");
                                int httpIndex = line.indexOf("http", imgIndex);
                                if (imgIndex != -1 && httpIndex != -1) {
                                    int startIndex = line.indexOf("\"", imgIndex) + 1;
                                    int endIndex = line.indexOf("\"", startIndex);
                                    String imageUrl = line.substring(startIndex, endIndex);
                                    if (imageUrl.contains("&"))
                                        continue;
                                    if (downloadToSave(imageUrl, "image" + count)) {
                                        Intent intent = new Intent();
                                        intent.setAction("download_ok");
                                        intent.putExtra("filename", "image" + count);
                                        count++;
                                        sendBroadcast(intent);
                                    }
                                    //For demo purpose to show that clicking fetch reloads the images
                                    //Thread.sleep(1000);
                                }
                            }
                            reader.close();
                            in.close();
                            if (count < 20) {
                                throw new InsufficientImageException("Insufficient Images");
                            }
                        }
                        catch (MalformedURLException e) {
                            e.printStackTrace();
                            Intent intent = new Intent();
                            intent.setAction("UrlException");
                            sendBroadcast(intent);
                        }
                        catch(UnknownHostException e){
                            e.printStackTrace();
                            Intent intent = new Intent();
                            intent.setAction("UrlException");
                            sendBroadcast(intent);
                        }
                        catch(FileNotFoundException e){
                            Intent intent = new Intent();
                            intent.setAction("DLException");
                            sendBroadcast(intent);
                        }
                        catch(InsufficientImageException e) {
                            e.printStackTrace();
                            Intent intent = new Intent();
                            intent.setAction("IIException");
                            sendBroadcast(intent);
                        }
                        catch(NullPointerException e){
                            e.printStackTrace();
                            Intent intent = new Intent();
                            intent.setAction("DLException");
                            sendBroadcast(intent);
                        }
                        catch(SocketTimeoutException e){
                            e.printStackTrace();
                            Intent intent = new Intent();
                            intent.setAction("DLException");
                            sendBroadcast(intent);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            });
            background.start();

        // don't restart this task if killed by Android system
        return START_NOT_STICKY;
    }

    public boolean downloadToSave(String imageUrl,String filename) throws NullPointerException, IOException, IllegalStateException {

            File mTargetFile =new File(getApplicationContext().getFilesDir(),"images/"+filename+".jpg");
            File parent = mTargetFile .getParentFile();
            if (!parent.exists () && !parent.mkdirs ()) {
                throw new IllegalStateException("Couldn't create dir : " + parent);
            }
            if (mTargetFile.exists())
            {
                mTargetFile.delete();
            }
            mTargetFile.createNewFile();

            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            InputStream in = connection.getInputStream();
            FileOutputStream fOutStream = new FileOutputStream(mTargetFile);

            Bitmap b = BitmapFactory.decodeStream(in);
            b.compress(Bitmap.CompressFormat.PNG,100, fOutStream);
            fOutStream.flush();
            fOutStream.close();;
            in.close();
            return true;

    }


    @Override
    public void onDestroy() {
        running =false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }
}