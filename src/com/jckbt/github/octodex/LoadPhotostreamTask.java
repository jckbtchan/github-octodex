
package com.jckbt.github.octodex;

import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

public class LoadPhotostreamTask extends AsyncTask<OAuth, Void, String[]> {

    private ImageGridActivity imageGridActivity;

    public LoadPhotostreamTask(ImageGridActivity activity) {
        imageGridActivity = activity;
    }

    @Override
    protected String[] doInBackground(OAuth... oauth) {
        OAuthToken token = oauth[0].getToken();
        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(),
                token.getOauthTokenSecret());
        User user = oauth[0].getUser();
        try {
            PhotoList photoList = f.getPeopleInterface().getPublicPhotos(user.getId(), 500, 1);
            int length = photoList.size();
            if (length > 0) {
                String[] imageUrls = new String[length];
                for (int i = 0; i < photoList.size(); i++) {
                    Photo photo = photoList.get(i);
                    imageUrls[i] = photo.getLargeSquareUrl();
                }
                return imageUrls;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (imageGridActivity != null) {
            imageGridActivity.getImageUrlsDone(result);
        }
    }

}
