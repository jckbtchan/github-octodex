
package com.jckbt.github.octodex;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

public class OAuthTokenHelper {

    public static final String PREFS_NAME = "flickrj-android-sample-pref";
    public static final String KEY_OAUTH_TOKEN = "flickrj-android-oauthToken";
    public static final String KEY_TOKEN_SECRET = "flickrj-android-tokenSecret";
    public static final String KEY_USER_NAME = "flickrj-android-userName";
    public static final String KEY_USER_ID = "flickrj-android-userId";

    private Context mContext;

    private static OAuthTokenHelper instance = null;

    public static OAuthTokenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new OAuthTokenHelper(context);
        }
        return instance;
    }

    public OAuthTokenHelper(Context context) {
        mContext = context;
    }

    public OAuth getOAuthToken() {
        // Restore preferences
        SharedPreferences settings = mContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String oauthTokenString = settings.getString(KEY_OAUTH_TOKEN, null);
        String tokenSecret = settings.getString(KEY_TOKEN_SECRET, null);
        if (oauthTokenString == null && tokenSecret == null) {
            return null;
        }
        OAuth oauth = new OAuth();
        String userName = settings.getString(KEY_USER_NAME, null);
        String userId = settings.getString(KEY_USER_ID, null);
        if (userId != null) {
            User user = new User();
            user.setUsername(userName);
            user.setId(userId);
            oauth.setUser(user);
        }
        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        return oauth;
    }

    public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(KEY_OAUTH_TOKEN, token);
        editor.putString(KEY_TOKEN_SECRET, tokenSecret);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

}
