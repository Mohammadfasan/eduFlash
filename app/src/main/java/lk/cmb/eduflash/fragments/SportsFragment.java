package lk.cmb.eduflash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

public class SportsFragment extends Fragment implements ArticleAdapter.OnReadMoreClickListener {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articleList;
    private List<Article> filteredList;
    private EditText searchBar;

    private static final String TAG = "SportsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sports, container, false);

        searchBar = view.findViewById(R.id.searchBarSports);
        recyclerView = view.findViewById(R.id.recyclerViewSports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        articleList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ArticleAdapter(filteredList, this);
        recyclerView.setAdapter(adapter);

        fetchArticlesFromFirebase();

        setupSearchListener();

        return view;
    }

    private void fetchArticlesFromFirebase() {
        // CORRECTED: Change the database reference to "news/Sports"
        // This targets the correct path in your Firebase Realtime Database
        DatabaseReference articlesRef = FirebaseDatabase.getInstance()
                .getReference("news/Sports");

        articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articleList.clear();
                for (DataSnapshot articleSnapshot : snapshot.getChildren()) {
                    // Manually map Firebase fields to Article model fields
                    // Your Firebase JSON uses 'headline' and 'body' for news items under "Sports".
                    // The Article model expects 'title', 'summary', and 'fullSummary'.
                    String title = articleSnapshot.child("headline").getValue(String.class);
                    String fullSummary = articleSnapshot.child("body").getValue(String.class);
                    String date = articleSnapshot.child("publisheddate").getValue(String.class);
                    String imageUrl = articleSnapshot.child("imageurl").getValue(String.class);
                    String id = articleSnapshot.getKey(); // Use the unique Firebase key as the article ID

                    // Create a short summary for the card view.
                    // This uses the first 100 characters of the 'body' (fullSummary).
                    String summary = fullSummary != null && fullSummary.length() > 100 ?
                            fullSummary.substring(0, 100) + "..." : fullSummary;


                    Article article = new Article(id, title, summary, fullSummary, date, imageUrl);
                    if (article != null) {
                        articleList.add(article);
                    }
                }
                filteredList.clear();
                filteredList.addAll(articleList);
                adapter.notifyDataSetChanged(); // Notify adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch articles", error.toException());
                // Handle the error appropriately, e.g., show a message to the user
            }
        });
    }

    private void setupSearchListener() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter articles as the user types
                filterArticles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used in this implementation
            }
        });
    }

    private void filterArticles(String query) {
        String lowerCaseQuery = query.toLowerCase();
        filteredList.clear(); // Clear previous search results

        if (query.isEmpty()) {
            // If the query is empty, show all articles
            filteredList.addAll(articleList);
        } else {
            // Filter articles based on title or summary containing the query
            for (Article article : articleList) {
                if ((article.getTitle() != null && article.getTitle().toLowerCase().contains(lowerCaseQuery)) ||
                        (article.getSummary() != null && article.getSummary().toLowerCase().contains(lowerCaseQuery))) {
                    filteredList.add(article);
                }
            }
        }
        adapter.notifyDataSetChanged(); // Notify adapter to update the displayed list
    }

    @Override
    public void onReadMoreClick(Article article) {
        // This method is called when the "Read more" button on an article card is clicked.
        // It starts a new activity (ArticleDetailActivity) to show the full article content.
        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
        intent.putExtra("title", article.getTitle());
        intent.putExtra("date", article.getDate());
        intent.putExtra("fullSummary", article.getFullSummary());
        intent.putExtra("imageUrl", article.getImageUrl());
        startActivity(intent);
    }
}