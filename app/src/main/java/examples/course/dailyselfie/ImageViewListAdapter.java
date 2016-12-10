package examples.course.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;


public class ImageViewListAdapter extends ArrayAdapter<RelativeLayout> {

    public ImageViewListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ImageViewListAdapter(Context context, int resource, List<RelativeLayout> items) {
        super(context, resource, items);
    }

    public ImageViewListAdapter(Context context, int resource, int textViewResourceId, List<RelativeLayout> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.picture_list_item, null);
        }

        RelativeLayout relativeLayout = getItem(position);
        ImageView currentImageView = (ImageView) relativeLayout.findViewById(R.id.selfieImage);
        TextView currentTextView = (TextView) relativeLayout.findViewById(R.id.selfieText);

        ImageView displayImageView = (ImageView) v.findViewById(R.id.selfieImage);
        TextView displayTextView = (TextView) v.findViewById(R.id.selfieText);

        if (currentImageView != null) {
            //int targetW = displayImageView.getWidth();
            //int targetH = displayImageView.getHeight();
            int targetW = 1;
            int targetH = 1;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentTextView.getText().toString(), bmOptions);
            //int photoW = bmOptions.outWidth;
            //int photoH = bmOptions.outHeight;
            int photoW = 2;
            int photoH = 2;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentTextView.getText().toString(), bmOptions);

            displayImageView.setImageBitmap(bitmap);
        }

        File directoryFolder = new File(currentTextView.getText().toString());
        String fileName = directoryFolder.getName();

        if (currentTextView != null) {
            displayTextView.setText(fileName);
        }

        return v;
    }
}

