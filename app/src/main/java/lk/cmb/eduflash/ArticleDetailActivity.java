package lk.cmb.eduflash;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView detailTitle, detailDate, detailContent;
    private ImageView detailImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail); // Connect your layout

        // Initialize all views
        detailTitle = findViewById(R.id.detailTitle);
        detailDate = findViewById(R.id.detailDate);
        detailContent = findViewById(R.id.detailContent);
        detailImage = findViewById(R.id.detailImage);

        // Receive data passed from adapter or previous activity
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String content = getIntent().getStringExtra("fullSummary");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Debugging: Show Toast messages to confirm data received correctly
        Toast.makeText(this, "Title: " + title, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Date: " + date, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Content length: " + (content != null ? content.length() : "null"), Toast.LENGTH_SHORT).show();

        // Set data to TextViews
        detailTitle.setText(title != null ? title : "No Title");
        detailDate.setText(date != null ? date : "No Date");
        detailContent.setText(content != null ? content : "No Content Available");

        // Load image using Glide, or show placeholder if no image URL
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.image) // placeholder image while loading
                    .error(R.drawable.image)       // error image if fail to load
                    .into(detailImage);
        } else {
            // No image URL provided, use default placeholder
            detailImage.setImageResource(R.drawable.image);
        }
    }
}
