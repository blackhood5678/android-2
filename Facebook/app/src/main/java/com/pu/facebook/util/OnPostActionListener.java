package com.pu.facebook.util;

import android.view.View;

import com.pu.facebook.Post;
import com.pu.facebook.dto.PostAction;

public interface OnPostActionListener {
    void onPostAction(View v, Post item, int position, PostAction postAction);
}
