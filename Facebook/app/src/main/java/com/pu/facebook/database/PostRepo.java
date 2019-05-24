package com.pu.facebook.database;

import android.content.Context;
import android.view.View;

public class PostRepo {
    private Context context;
    private DatabaseHandler databaseHandler;

    public PostRepo(Context context) {
        this.context = context;
        this.databaseHandler  = new DatabaseHandler(context);
    }
    public boolean deletePost(int postId) {
        return databaseHandler.delPost(postId);
    }

    public void insertPost(String postDesc, String imageName, int userId) {
        databaseHandler.addPost(postDesc, imageName, String.valueOf(userId));
    }
}
