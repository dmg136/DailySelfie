package examples.course.dailyselfie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        Intent callingIntent = getIntent();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        String imageLocation = callingIntent.getStringExtra("imageLocation");
        imageView.setImageURI(Uri.parse(imageLocation));
    }
}
