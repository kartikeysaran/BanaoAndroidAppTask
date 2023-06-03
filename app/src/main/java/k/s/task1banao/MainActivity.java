package k.s.task1banao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    @Inject
    Retrofit retrofit;

    private PhotosAPI photosAPI;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ProgressBar progressBar;
    private int curr_Page = 1;
    private int curr_limit = 50;
    private ArrayList<PhotosBean> photosBeanArrayList;
    private CustomAdapter customAdapter;
    private GridLayoutManager layoutManager;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
        setUpNavBar();
        photosBeanArrayList = new ArrayList<>();
        customAdapter = new CustomAdapter(photosBeanArrayList, MainActivity.this);
        layoutManager=new GridLayoutManager(MainActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(customAdapter);
        getImagesFromAPI(curr_Page, curr_limit);
        loadImages();
    }

    private void loadImages() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + 1)) {
                    loading = true;
                    progressBar.setVisibility(View.VISIBLE);
                    getImagesFromAPI(curr_Page+1, curr_limit);
                }
            }
        });

    }
    private void setUpViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progress_bar);
        drawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setUpNavBar() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getImagesFromAPI(int page, int limit) {
        if(page > limit) {
            Toast.makeText(this, "That's all the data..", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        } else {
            ((BaseApplication)getApplication()).getNetworkComponent().inject(this);
            photosAPI = retrofit.create(PhotosAPI.class);
            photosAPI.getPhotos("flickr.photos.getRecent",
                    20,
                    page,
                    "6f102c62f41998d151e5a1b48713cf13",
                    "json",
                    1,
                    "url_s"
            ).enqueue(new Callback<PhotoResBean>() {
                @Override
                public void onResponse(Call<PhotoResBean> call, Response<PhotoResBean> response) {
                    Log.d("RETROFIT", response.message().toString());
                    curr_Page = response.body().getPhotos().getPage();
                    curr_limit = response.body().getPhotos().getPages();
                    for(PhotosBean photosBean: response.body().getPhotos().getPhoto()) {
                        photosBeanArrayList.add(photosBean);
                    }
                    //TODO:Notify Adapter
//                    CustomAdapter customAdapter = new CustomAdapter(response.body().getPhotos().getPhoto(), MainActivity.this);
//                    curr_Page = response.body().getPhotos().getPage();
//                    curr_limit = response.body().getPhotos().getPages();
//                    GridLayoutManager layoutManager=new GridLayoutManager(MainActivity.this,2);
//                    recyclerView.setLayoutManager(layoutManager);
//                    recyclerView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                    loading = false;
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<PhotoResBean> call, Throwable t) {
                    Log.d("RETROFIT", t.toString());
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}