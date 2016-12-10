package examples.course.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000L;
    private String mCurrentPhotoPath;
    private File currentFile;
    private ListView listViewPictures;
    private List<RelativeLayout> imageViewList;
    private ImageViewListAdapter imageViewListAdapter;
    private AlarmManager alarmManager;
    private Intent selfieReminderIntent;
    private PendingIntent selfieReminderPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        selfieReminderIntent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);
        selfieReminderPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, selfieReminderIntent, 0);

        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis() + INITIAL_ALARM_DELAY, AlarmManager.INTERVAL_FIFTEEN_MINUTES, selfieReminderPendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INITIAL_ALARM_DELAY, selfieReminderPendingIntent);

        Toast.makeText(this, "Alarm should be set", Toast.LENGTH_SHORT).show();


        listViewPictures = (ListView) findViewById(R.id.picture_list_view);

        listViewPictures.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RelativeLayout newRelativeLayout = (RelativeLayout) adapterView.getItemAtPosition(i);
                ImageView newImageView = (ImageView) newRelativeLayout.findViewById(R.id.selfieImage);
                TextView newTextView = (TextView) newRelativeLayout.findViewById(R.id.selfieText);

                //Toast.makeText(getApplicationContext(), "Clicked listView " + i, Toast.LENGTH_SHORT).show();

                Intent displayImageIntent = new Intent(getApplicationContext(), DisplayImageActivity.class);
                //Intent displayImageIntent = new Intent(Intent.ACTION_VIEW);
                //displayImageIntent.setData(Uri.parse(newTextView.getText().toString()));
                displayImageIntent.putExtra("imageLocation", newTextView.getText().toString());

                if (displayImageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(displayImageIntent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Could not display image", Toast.LENGTH_SHORT).show();
                }
                //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                //based on item add info to intent
                //startActivity(intent);
            }
        });

        imageViewList = new ArrayList<RelativeLayout>();

        File imageDirectory = new File(Environment.getExternalStorageDirectory(), "my_selfies_lol");
        imageDirectory.mkdirs();
        File[] fileNames = imageDirectory.listFiles();

        RelativeLayout tempRelativeLayout;
        ImageView tempImageView;
        TextView tempTextView;
        for (File tempFile : fileNames) {
            tempRelativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.picture_list_item, null);
            tempTextView = (TextView) tempRelativeLayout.findViewById(R.id.selfieText);

            tempTextView.setText(tempFile.getAbsolutePath());
            imageViewList.add(tempRelativeLayout);

        }

        imageViewListAdapter = new ImageViewListAdapter(this, R.layout.picture_list_item, imageViewList);

        listViewPictures.setAdapter(imageViewListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            dispatchTakePictureIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = null;
            try {
                currentFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (currentFile != null) {
                //Uri photoURI = FileProvider.getUriForFile(this, "example.courses.dailyselfie", photoFile);
                //System.out.println("mCurrentPath: " + mCurrentPhotoPath);
                //Uri photoURI = Uri.parse("file://" + mCurrentPhotoPath);
                Uri photoURI = Uri.fromFile(currentFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory(), "my_selfies_lol");
        storageDir.mkdirs();


        File image = new File(storageDir, imageFileName + ".png");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            setPic();


        }
    }

    private void setPic() {

        RelativeLayout newRelativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.picture_list_item, null);
        ImageView newImageView = (ImageView) newRelativeLayout.findViewById(R.id.selfieImage);
        TextView newTextView = (TextView) newRelativeLayout.findViewById(R.id.selfieText);

        // Get the dimensions of the View
        //int targetW = newImageView.getWidth();
        //int targetH = newImageView.getHeight();
        int targetW = 100;
        int targetH = 100;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentFile.getAbsolutePath(), bmOptions);

        //int photoW = bmOptions.outWidth;
        //int photoH = bmOptions.outHeight;
        int photoW = 100;
        int photoH = 100;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath(), bmOptions);
        newImageView.setImageBitmap(bitmap);
        newTextView.setText(mCurrentPhotoPath);

        imageViewListAdapter.add(newRelativeLayout);
    }
}
