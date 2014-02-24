
package com.jckbt.github.octodex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ImageGridActivity extends Activity {

    public static final String CALLBACK_SCHEME = "flickrj-android-oauth";

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    protected AbsListView listView;

    String[] imageUrls;

    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_grid);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        listView = (GridView) findViewById(R.id.gridview);
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, false, true));

        OAuthTokenHelper oAuthTokenHelper = OAuthTokenHelper.getInstance(this);
        OAuth oauth = oAuthTokenHelper.getOAuthToken();
        if (oauth == null || oauth.getUser() == null) {
            oAuthTokenHelper.saveOAuthToken("jckbte", "114004506@N06", "72157641292143614",
                    "891d7f78b7308673");
            oauth = oAuthTokenHelper.getOAuthToken();
            if (oauth == null || oauth.getUser() == null) {
                OAuthTask task = new OAuthTask(this);
                task.execute();
            } else {
                load(oauth);
            }
        } else {
            load(oauth);
        }
    }

    private void load(OAuth oauth) {
        if (oauth != null) {
            new LoadPhotostreamTask(this).execute(oauth);
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

    public void getImageUrlsDone(String[] imageUrls) {
        this.imageUrls = imageUrls;

        ((GridView) listView).setAdapter(new ImageGridAdapter());
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });
    }

    private void startImagePagerActivity(int position) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        intent.putExtra("images", imageUrls);
        intent.putExtra("positin", position);
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
        imageLoader.stop();
        super.onBackPressed();
    }

    private class ImageGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            imageLoader.displayImage(imageUrls[position], holder.imageView, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current,
                                int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    }
                    );

            return view;
        }

        class ViewHolder {
            ImageView imageView;
            ProgressBar progressBar;
        }

    }

}
