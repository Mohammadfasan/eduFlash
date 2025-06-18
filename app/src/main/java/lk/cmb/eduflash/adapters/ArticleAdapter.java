package lk.cmb.eduflash.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.cmb.eduflash.ArticleDetailActivity;
import lk.cmb.eduflash.R;
import lk.cmb.eduflash.models.Article;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    public interface OnReadMoreClickListener {
        void onReadMoreClick(Article article);
    }

    private List<Article> articleList;
    private OnReadMoreClickListener readMoreClickListener;

    public ArticleAdapter(List<Article> articleList, OnReadMoreClickListener listener) {
        this.articleList = articleList;
        this.readMoreClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);

        holder.articleTitle.setText(article.getTitle());
        holder.articleSummary.setText(article.getSummary());
        holder.articleDate.setText(article.getDate());

        // Image loading with debug and fallback
        String imageUrl = article.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d("ArticleAdapter", "Loading image URL: " + imageUrl);

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.image) // shown while loading
                    .error(R.drawable.image)       // shown if load fails
                    .into(holder.articleImage);
        } else {
            Log.d("ArticleAdapter", "Image URL is empty or null, loading default image.");
            holder.articleImage.setImageResource(R.drawable.image); // fallback
        }

        // Read more button click
        holder.readMore.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ArticleDetailActivity.class);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("date", article.getDate());
            intent.putExtra("fullSummary", article.getFullSummary());
            intent.putExtra("imageUrl", article.getImageUrl());

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView articleTitle, articleSummary, articleDate, readMore, fullSummary;
        ImageView articleImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleSummary = itemView.findViewById(R.id.articleSummary);
            articleDate = itemView.findViewById(R.id.articleDate);
            articleImage = itemView.findViewById(R.id.articleImage);
            readMore = itemView.findViewById(R.id.readMore);
            fullSummary = itemView.findViewById(R.id.fullSummary);
        }
    }
}
