package com.pu.facebook;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pu.facebook.component.PostView;
import com.pu.facebook.dto.PostAction;
import com.pu.facebook.util.BitmapUtil;
import com.pu.facebook.util.OnPostActionListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private OnPostActionListener onPostActionListener;

    class ViewHolder extends RecyclerView.ViewHolder {
        private PostView postView;
        ViewHolder(View view) {
            super(view);
            postView = (PostView) view;
        }
    }

    public PostAdapter(Context context, List<Post> postList, OnPostActionListener onPostActionListener) {
        this.context = context;
        this.postList = postList;
        this.onPostActionListener = onPostActionListener;
    }

    public void setData(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        postList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, postList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Post post = postList.get(position);
        holder.postView.setPost(post);
        holder.postView.setPosition(position);
        holder.postView.setOnPostActionListener(onPostActionListener);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}

