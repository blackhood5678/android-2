package com.pu.facebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pu.facebook.database.DatabaseHandler;
import com.pu.facebook.database.PostRepo;
import com.pu.facebook.dto.PostAction;
import com.pu.facebook.util.OnPostActionListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements OnPostActionListener {

    public static int userid;
    TextView tvName;
    DatabaseHandler databaseHandler;
    RecyclerView recyclerView;
    List<Post> postList;
    ProfilePostAdapter adapter;
    Button btnAddPost;
    private PostRepo postRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        postRepo = new PostRepo(getApplicationContext());
        databaseHandler = new DatabaseHandler(this);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(databaseHandler.getName(userid));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(3), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        postList = new ArrayList<Post>();

        adapter = new ProfilePostAdapter(this, postList, this);
        recyclerView.setAdapter(adapter);
        btnAddPost = (Button) findViewById(R.id.btnAddPost);
        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, AddPostActivity.class);
                startActivity(i);
                finish();
            }
        });

        getPOSTFromDB();
    }

    public void clearData() {
        final int size = this.postList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.postList.remove(0);

            }
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemRangeRemoved(0, size);
                }
            });


        }
    }

    public void getPOSTFromDB() {

        clearData();
        Log.e("Size of adapter", adapter.getItemCount() + "");
        try {


            postList = databaseHandler.getPOSTsById(userid);
            adapter.setPostList(postList);
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:


                return true;
            case R.id.menu_quiz:
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);

                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    @Override
    public void onPostAction(View v, final Post item, final int position, PostAction postAction) {
        if (postAction == PostAction.EDIT_POST) {
            Intent i = new Intent(getApplicationContext(), EditPostActivity.class);
            i.putExtra("desc", item.getPostDesc());
            i.putExtra("postid", item.getPostId());
            i.putExtra("image", item.getPostImage());
            startActivity(i);
        } else if (postAction == PostAction.DELETE_POST) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
            dialogBuilder.setTitle("Warning: DELETE POST")
                    .setMessage("Do you want to delete this post?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (postRepo.deletePost(Integer.parseInt(item.postId))) {
                                adapter.removeItem(position);
                            } else {
                                Toast.makeText(getApplicationContext(), "Unknown error occurs", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialogBuilder.create().show();
        }
    }
}
