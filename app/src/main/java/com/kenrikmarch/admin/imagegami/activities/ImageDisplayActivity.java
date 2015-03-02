package com.kenrikmarch.admin.imagegami.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.kenrikmarch.admin.imagegami.R;
import com.kenrikmarch.admin.imagegami.models.ImageResult;
import com.kenrikmarch.admin.imagegami.support.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDisplayActivity extends ActionBarActivity {

    private TouchImageView ivImageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_image_display);
        setupView();
        Bundle b = getIntent().getExtras();
        ImageResult result = b.getParcelable("image_data");
        setupViewData(result);
    }

    public void setupView() {
        ivImageResult = (TouchImageView) findViewById(R.id.ivImageResult);
    }

    public void setupViewData(ImageResult result) {
        getSupportActionBar().setTitle(Html.fromHtml(result.title));
        Picasso.with(this).load(result.fullURL).placeholder(R.drawable.loading).into(ivImageResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            onShareItem();
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onShareItem() {
        Uri bmpUri = getLocalBitmapUri(ivImageResult);
        if (bmpUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toast.makeText(getApplicationContext(), "FUBAR, Unable to share image", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else { return null; }
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
