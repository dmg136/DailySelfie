package examples.course.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
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
    static final String imageFolderName = "my_selfies_lol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the recurring alarm to remind user to take another selfie
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        selfieReminderIntent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);
        selfieReminderPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, selfieReminderIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY, AlarmManager.INTERVAL_FIFTEEN_MINUTES, selfieReminderPendingIntent);

        listViewPictures = (ListView) findViewById(R.id.picture_list_view);

        /*
        Start the DisplayImageActivity to view the enlarged photo corresponding to the item that user selected in the listView
         */
        listViewPictures.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RelativeLayout newRelativeLayout = (RelativeLayout) adapterView.getItemAtPosition(i);
                TextView newTextView = (TextView) newRelativeLayout.findViewById(R.id.selfieText);

                Intent displayImageIntent = new Intent(getApplicationContext(), DisplayImageActivity.class);
                displayImageIntent.putExtra("imageLocation", newTextView.getText().toString());

                if (displayImageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(displayImageIntent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Could not display image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageViewList = new ArrayList<RelativeLayout>();

        File imageDirectory = new File(Environment.getExternalStorageDirectory(), imageFolderName);

        // Set up the image directory if it doesn't exist on filesystem
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs();
        }

        // Only add png files to listView
        File[] fileNames = imageDirectory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("png");
            }
        });

        // Create a view for each image in the directory and add to listView
        RelativeLayout tempRelativeLayout;
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

            // Start the Camera activity if user clicked the camera icon in the ActionBar
            dispatchTakePictureIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                currentFile = createImageFile();
            } catch (IOException ex) {

                // Error occurred while creating the File
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (currentFile != null) {
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
        File storageDir = new File(Environment.getExternalStorageDirectory(), imageFolderName);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName + ".png");

        // Store the location where the photo is saved
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User took a picture using the Camera app, now add the image to the listView
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    /*
    Add an image to the listView after user takes picture
     */
    private void setPic() {

        RelativeLayout newRelativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.picture_list_item, null);
        TextView newTextView = (TextView) newRelativeLayout.findViewById(R.id.selfieText);
        newTextView.setText(mCurrentPhotoPath);

        imageViewListAdapter.add(newRelativeLayout);
    }
}
