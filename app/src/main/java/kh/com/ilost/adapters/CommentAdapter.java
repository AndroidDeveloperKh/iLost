package kh.com.ilost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.models.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder>{

    private Context context;
    private List<Comment> comments;

    public CommentAdapter (Context context, List<Comment> comments){
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.view_holder_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.set(context, comment);
    }

    @Override
    public int getItemCount() {
        if (comments.size() != 0)
            return comments.size();
        return 0;
    }

}
