package com.pu.facebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pu.facebook.database.DatabaseHandler;
import com.pu.facebook.database.PostRepo;
import com.pu.facebook.dto.PostAction;
import com.pu.facebook.util.OnPostActionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnPostActionListener {

    RecyclerView recyclerView;
    List<Post> postList;
    PostAdapter adapter;
    DatabaseHandler databaseHandler;
    private PostRepo postRepo;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circle = (Circle) findViewById(R.id.circle);
        postRepo = new PostRepo(getApplicationContext());

        databaseHandler = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = null;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this);
        } else {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(3), true));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        postList = new ArrayList<Post>();

        adapter = new PostAdapter(this, postList, this);
        recyclerView.setAdapter(adapter);
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
        //Going to load data: show loader
        circle.setVisibility(View.VISIBLE);
        clearData();
        Log.e("Size of adapter", adapter.getItemCount() + "");
        try {
            //Data Loaded: hide loader
            circle.setVisibility(View.GONE);
            postList = databaseHandler.getPOSTs();
            adapter.setData(postList);
        } catch (NullPointerException n) {
            circle.setVisibility(View.GONE);
            n.printStackTrace();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_quiz, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:


                return true;
            case R.id.menu_quiz:
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
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
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
