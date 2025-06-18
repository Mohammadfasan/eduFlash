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

public class AcademicFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articleList; // Renamed from eventList for clarity as it holds articles

    private static final String TAG = "AcademicFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAcademic);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        articleList = new ArrayList<>();
        adapter = new ArticleAdapter(articleList, this);
        recyclerView.setAdapter(adapter);

        fetchArticlesFromFirebase();

        return view;
    }

    private void fetchArticlesFromFirebase() {
        // CORRECTED: Change the database reference to "news/Academics"
        DatabaseReference academicRef = FirebaseDatabase.getInstance()
                .getReference("news/Academics"); //

        academicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articleList.clear(); // Clear the list to avoid duplicates on data change
                for (DataSnapshot articleSnapshot : snapshot.getChildren()) { // Iterate through each academic article
                    // Manually map Firebase fields to Article model fields
                    // Your Firebase JSON under "news/Academics" has 'content', 'imageurl', 'publisheddate', and 'title'.
                    // The Article model expects 'title', 'summary', 'fullSummary', 'date', and 'imageUrl'.
                    String id = articleSnapshot.getKey(); // Get the unique Firebase key as the article ID
                    String title = articleSnapshot.child("title").getValue(String.class); //
                    String fullSummary = articleSnapshot.child("content").getValue(String.class); // Map 'content' to fullSummary
                    String date = articleSnapshot.child("publisheddate").getValue(String.class); // Map 'publisheddate' to date
                    String imageUrl = articleSnapshot.child("imageurl").getValue(String.class); // Map 'imageurl' to imageUrl

                    // Create a short summary for the card view from the fullSummary (content).
                    String summary = fullSummary != null && fullSummary.length() > 100 ?
                            fullSummary.substring(0, 100) + "..." : fullSummary;

                    Article article = new Article(id, title, summary, fullSummary, date, imageUrl); // Create an Article object
                    if (article != null) { // Add the article if it's not null
                        articleList.add(article);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch academic articles", error.toException()); // Log any errors
            }
        });
    }

    @Override
    public void onReadMoreClick(Article article) {
        // This method is called when the "Read more" button on an article card is clicked.
        // It creates an Intent to start ArticleDetailActivity and passes all relevant article data.
        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
        intent.putExtra("title", article.getTitle()); //
        intent.putExtra("date", article.getDate()); //
        intent.putExtra("fullSummary", article.getFullSummary()); //
        intent.putExtra("imageUrl", article.getImageUrl()); //

        startActivity(intent);
    }
}