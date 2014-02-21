
package com.jckbt.github.octodex;

import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;

public class GetOAuthTokenTask extends AsyncTask<String, Integer, OAuth> {

    private ImageGridActivity imageGridActivity;

    public GetOAuthTokenTask(ImageGridActivity activity) {
        imageGridActivity = activity;
    }

    @Override
    protected OAuth doInBackground(String... params) {
        String oauthToken = params[0];
        String oauthTokenSecret = params[1];
        String verifier = params[2];
        Flickr f = FlickrHelper.getInstance().getFlickr();
        OAuthInterface oauthApi = f.getOAuthInterface();
        try {
            return oauthApi.getAccessToken(oauthToken, oauthTokenSecret,
                    verifier);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(OAuth result) {
        if (imageGridActivity != null) {
            imageGridActivity.onOAuthDone(result);
        }
    }

}
