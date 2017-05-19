package io.github.feelfreelinux.wordling.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebWordpackDownloader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result;
            StringBuilder content = new StringBuilder();
            try {
                // Send GET request
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                HttpURLConnection.setFollowRedirects(true);
                String line;

                boolean redirect = false;

                // normally, 3xx is redirect
                int status = urlConnection.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                }

                if (redirect) {
                    // get redirect url from "location" header field
                    String newUrl = urlConnection.getHeaderField("Location");
                    // open the new connnection again
                    urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                // Read response data
                while ((line = bufferedReader.readLine()) != null)
                    content.append(line + "\n");

                bufferedReader.close();
                result = content.toString();
            } catch (IOException e) {
                return "";
            }
            return result;
        }
}