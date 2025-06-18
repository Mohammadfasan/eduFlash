package lk.cmb.eduflash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import lk.cmb.eduflash.ArticleDetailActivity;
import lk.cmb.eduflash.R;
import lk.cmb.eduflash.adapters.ArticleAdapter;
import lk.cmb.eduflash.models.Article;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> eventList;

    private static final String TAG = "EventsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        adapter = new ArticleAdapter(eventList, this);
        recyclerView.setAdapter(adapter);

        fetchEventsFromFirebase();

        return view;
    }

    private void fetchEventsFromFirebase() {
        // CORRECTED: Change the database reference to "news/Events"
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("news/Events");

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear(); // Clear the list to avoid duplicates on data change
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) { // Iterate through each event
                    // Manually map Firebase fields to Article model fields
                    // Your Firebase JSON under "news/Events" has 'content', 'imageurl', 'publisheddate', and 'title'.
                    // The Article model expects 'title', 'summary', 'fullSummary', 'date', and 'imageUrl'.
                    String title = eventSnapshot.child("title").getValue(String.class);
                    String fullSummary = eventSnapshot.child("content").getValue(String.class); // Use 'content' for fullSummary
                    String date = eventSnapshot.child("publisheddate").getValue(String.class);
                    String imageUrl = eventSnapshot.child("imageurl").getValue(String.class);
                    String id = eventSnapshot.getKey(); // Get the unique key as ID

                    // For the short summary displayed in the card, use a substring of the fullSummary (content).
                    String summary = fullSummary != null && fullSummary.length() > 100 ?
                            fullSummary.substring(0, 100) + "..." : fullSummary;

                    Article event = new Article(id, title, summary, fullSummary, date, imageUrl); // Create an Article object
                    if (event != null) { // Add the article if it's not null
                        eventList.add(event);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch events", error.toException()); // Log any errors
            }
        });
    }

    @Override
    public void onReadMoreClick(Article article) {
        // This method is called when the "Read more" button on an article card is clicked.
        // It starts ArticleDetailActivity and passes all relevant article data.
        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
        intent.putExtra("title", article.getTitle());
        intent.putExtra("date", article.getDate());
        intent.putExtra("fullSummary", article.getFullSummary());
        intent.putExtra("imageUrl", article.getImageUrl());
        startActivity(intent);
    }
}