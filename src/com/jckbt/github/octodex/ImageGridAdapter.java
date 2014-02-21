
package com.jckbt.github.octodex;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageGridAdapter extends BaseAdapter {

    private String[] imageUrls;
    private LayoutInflater layoutInflater;

    public ImageGridAdapter(Activity activity, String[] imageUrls) {
        this.imageUrls = imageUrls;
        layoutInflater = activity.getLayoutInflater();
    }

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
            view = layoutInflater.inflate(R.layout.item_grid_image, parent, false);
            holder = new ViewHolder();
            assert view != null;
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }

    class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

}
