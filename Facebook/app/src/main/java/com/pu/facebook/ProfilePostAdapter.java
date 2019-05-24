package com.pu.facebook;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pu.facebook.component.PostView;
import com.pu.facebook.database.DatabaseHandler;
import com.pu.facebook.util.OnPostActionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;
    Post post;
    private OnPostActionListener onPostActionListener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private PostView postView;

        public ViewHolder(View view) {
            super(view);
            postView = (PostView) view;
        }
    }

    public ProfilePostAdapter(Context context, List<Post> postList, OnPostActionListener onPostActionListener) {
        this.context = context;
        this.postList = postList;
        this.onPostActionListener = onPostActionListener;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
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
        post = postList.get(position);
        holder.postView.setPost(post);
        holder.postView.setPosition(position);
        holder.postView.setOnPostActionListener(onPostActionListener);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


}

