
package com.jckbt.github.octodex;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageGridActivity extends Activity {

    public static final String CALLBACK_SCHEME = "flickrj-android-oauth";

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_grid);

        OAuth oauth = OAuthTokenHelper.getInstance(this).getOAuthToken();
        if (oauth == null || oauth.getUser() == null) {
            OAuthTask task = new OAuthTask(this);
            task.execute();
        } else {
            load(oauth);
        }
    }

    private void load(OAuth oauth) {
        if (oauth != null) {
            OAuthToken token = oauth.getToken();
            System.out.println("Token______" + token.getOauthToken());
            System.out.println("TokenSecret______" + token.getOauthTokenSecret());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        OAuth savedToken = new OAuthTokenHelper(this).getOAuthToken();
        if (CALLBACK_SCHEME.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
            Uri uri = intent.getData();
            String query = uri.getQuery();
            String[] data = query.split("&");
            if (data != null && data.length == 2) {
                String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
                String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);

                OAuth oauth = OAuthTokenHelper.getInstance(this).getOAuthToken();
                if (oauth != null && oauth.getToken() != null
                        && oauth.getToken().getOauthTokenSecret() != null) {
                    GetOAuthTokenTask task = new GetOAuthTokenTask(this);
                    task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
                }
            }
        }
    }

    public void onOAuthDone(OAuth result) {
        if (result == null) {
            Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
        } else {
            User user = result.getUser();
            OAuthToken token = result.getToken();
            if (user == null || user.getId() == null || token == null
                    || token.getOauthToken() == null
                    || token.getOauthTokenSecret() == null) {
                Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
                return;
            }
            OAuthTokenHelper.getInstance(this).saveOAuthToken(user.getUsername(), user.getId(),
                    token.getOauthToken(),
                    token.getOauthTokenSecret());
            load(result);
        }
    }

}
