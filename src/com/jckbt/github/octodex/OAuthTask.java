
package com.jckbt.github.octodex;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import java.net.URL;

public class OAuthTask extends AsyncTask<Void, Integer, String> {

    private static final String OAUTH_CALLBACK_URI = "flickrj-android-oauth://oauth";

    private Context mContext;

    public OAuthTask(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mContext, "OAuth...", Toast.LENGTH_LONG).show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            // get a request token from Flickr
            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
                    OAUTH_CALLBACK_URI);

            OAuthTokenHelper.getInstance(mContext).saveOAuthToken(null, null, null,
                    oauthToken.getOauthTokenSecret());

            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
                    Permission.READ, oauthToken);
            return oauthUrl.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "error:" + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && !result.startsWith("error")) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse(result)));
        } else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }

}
