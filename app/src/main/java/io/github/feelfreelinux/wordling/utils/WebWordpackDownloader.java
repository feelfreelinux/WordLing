package io.github.feelfreelinux.wordling.utils;

import android.os.AsyncTask;

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

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;

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