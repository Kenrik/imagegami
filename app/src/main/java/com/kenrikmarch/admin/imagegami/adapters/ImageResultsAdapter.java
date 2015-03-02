package com.kenrikmarch.admin.imagegami.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import  android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenrikmarch.admin.imagegami.R;
import com.kenrikmarch.admin.imagegami.models.ImageResult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

    private static class ViewHolder {
        TextView title;
        ImageView image;
    }


    public ImageResultsAdapter(Context context, List<ImageResult> images) {
        super(context, R.layout.item_image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageResult imageInfo = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_image_result, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.ivImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(Html.fromHtml(imageInfo.title));
        Picasso.with(getContext()).load(imageInfo.thumbURL).into(viewHolder.image);
        return convertView;
    }
}


