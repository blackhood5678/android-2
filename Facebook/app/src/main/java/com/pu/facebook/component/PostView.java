package com.pu.facebook.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pu.facebook.Post;
import com.pu.facebook.R;
import com.pu.facebook.dto.PostAction;
import com.pu.facebook.util.BitmapUtil;
import com.pu.facebook.util.OnPostActionListener;

public class PostView extends LinearLayout {
    private OnPostActionListener mOnPostActionListener;
    private int position;
    private Post mPost;
    private TextView mPostDescription, mUserName;
    private ImageView mPostImage, mUserProfileImage;
    private FrameLayout mEditPost, mDeletePost;

    public void setPost(Post post) {
        this.mPost = post;
        mPostDescription.setText(post.getPostDesc());
        mUserName.setText(post.getUserId());
        if (TextUtils.isEmpty(mPost.getPostImage())) {
            mPostImage.setVisibility(View.GONE);
        } else {
            Bitmap b = BitmapUtil.loadBitmap(getContext(), post.getPostImage());
            if (b != null) {
                mPostImage.setImageBitmap(b);
                mPostImage.setVisibility(View.VISIBLE);
            }
        }
    }

    public Post getPost() {
        return mPost;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setOnPostActionListener(OnPostActionListener onPostActionListener) {
        this.mOnPostActionListener = onPostActionListener;
    }

    public PostView(Context context) {
        this(context, null);
    }

    public PostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_post, this);

        mPostDescription = findViewById(R.id.tvPost);
        mUserName = findViewById(R.id.tvUserName);
        mPostImage = findViewById(R.id.ivPostImage);
        mUserProfileImage = findViewById(R.id.ivProfile);
        mEditPost = findViewById(R.id.edit_post_button_wrapper);
        mDeletePost = findViewById(R.id.delete_post_button_wrapper);

        mEditPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPostActionListener != null) {
                    mOnPostActionListener.onPostAction(v, mPost, position, PostAction.EDIT_POST);
                }
            }
        });

        mDeletePost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPostActionListener != null) {
                    mOnPostActionListener.onPostAction(v, mPost, position, PostAction.DELETE_POST);
                }
            }
        });
    }
}
