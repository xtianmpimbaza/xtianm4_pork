package com.example.liew.idelivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.liew.idelivery.Common.Common;
import com.example.liew.idelivery.Database.Database;
import com.example.liew.idelivery.Interface.ItemClickListener;
import com.example.liew.idelivery.Model.Banner;
import com.example.liew.idelivery.Model.Category;
import com.example.liew.idelivery.Model.Token;
import com.example.liew.idelivery.ViewHolder.MenuViewHolder;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPagerEx.OnPageChangeListener {

    FirebaseDatabase database;
    DatabaseReference category;
    public TextView txtFullName;
    RecyclerView recycler_menu;
    //    RecyclerView.LayoutManager layoutManager;
//    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;

    //slider
//    HashMap<String, String> image_list;
    SliderLayout sliderLayout;

    //    @BindView(R.id.slider)
//    SliderLayout mDemoSlider;
    SharedPreferences sharedPreferences;

    @OnClick(R.id.card_view_fresh)
    void goToFresh(View view) {
        Intent in = new Intent(HomeActivity.this, FoodDetail.class);
        in.putExtra("name", "Fresh Pork");
        in.putExtra("image", "slider3");
        in.putExtra("price", "35000");
        in.putExtra("description", "Fresh Pork");
        in.putExtra("menuId", "1");
        startActivity(in);
    }

    @OnClick(R.id.card_view_roasted)
    void goToRoast(View view) {
        Intent in = new Intent(HomeActivity.this, FoodDetail.class);

        in.putExtra("name", "Roasted Pork");
        in.putExtra("image", "slider1");
        in.putExtra("price", "40000");
        in.putExtra("description", "Roasted Pork");
        in.putExtra("menuId", "2");
        startActivity(in);
    }

    @OnClick(R.id.card_view_fried)
    void goToFry(View view) {

        Intent in = new Intent(HomeActivity.this, FoodDetail.class);
        in.putExtra("name", "Fried Pork");
        in.putExtra("image", "slider2");
        in.putExtra("price", "51000");
        in.putExtra("description", "Fried Pork");
        in.putExtra("menuId", "3");
        startActivity(in);
    }

    @OnClick(R.id.card_view_sausages)
    void goToSaurcages(View view) {
        Intent in = new Intent(HomeActivity.this, FoodDetail.class);
        in.putExtra("name", "Sourceges");
        in.putExtra("image", "sourcages");
        in.putExtra("price", "51000");
        in.putExtra("description", "Roasted Pork");
        in.putExtra("menuId", "4");
        startActivity(in);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        //for first-time login, pop up notification to complete profile.
        sharedPreferences = getSharedPreferences("com.example.liew.idelivery", MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        //Init SwipeRefreshLayout view
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext()))
//                    loadMenu();
                    Toast.makeText(getBaseContext(), "Refreshed!", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getBaseContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (!Common.isConnectedToInternet(getBaseContext()))
                    Toast.makeText(getBaseContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        });

        //Init Firebase

//        database = FirebaseDatabase.getInstance();
//        category = database.getReference("Category");

        //put under firebasedatabase.getinstance
//        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
//                .setQuery(category, Category.class)
//                .build();

//        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
//                viewHolder.txtMenuName.setText(model.getName());
//                Picasso.with(getBaseContext()).load(model.getImage()).into
//                        (viewHolder.imageView);
//                final Category clickItem = model;
//                viewHolder.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//                        //get CategoryId and sent to new activity
//                        Intent foodList = new Intent(HomeActivity.this, FoodList.class);
//
//                        //because CategoryId is a key, so we just get key of this item
//                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
//                        startActivity(foodList);
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View itemView = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.menu_item, parent, false);
//                return new MenuViewHolder(itemView);
//            }
//        };

//        Paper.init(this);

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(HomeActivity.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name for user at navigation header

        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.nav_header_name_id);
        txtFullName.setText(Common.currentUser.getName());

        // Load menu

//        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
//        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));
//        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
//                R.anim.layout_fall_down);
//        recycler_menu.setLayoutAnimation(controller);

//        updateToken(FirebaseInstanceId.getInstance().getToken());

        //setup slider
        setupSlider();
//        makeslider();
    }

    void setupSlider() {

        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Fast Services", R.drawable.slider1);
        file_maps.put("Instant delivery", R.drawable.slider2);
        file_maps.put("Affordable", R.drawable.slider3);
        file_maps.put("Well Prepared", R.drawable.slider4);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
        sliderLayout.addOnPageChangeListener(this);
    }

    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false);
        // false because token send from client app

        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

//    private void loadMenu() {
//
//        adapter.startListening();
//        recycler_menu.setAdapter(adapter);
//
//        //Animation
//        recycler_menu.getAdapter().notifyDataSetChanged();
//        recycler_menu.scheduleLayoutAnimation();
//
//    }

    private void CompleteProfileNotification() {

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Incomplete Profile");
        alertDialog.setMessage("Please Add Username and HomeActivity Address before ordering.");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_profile = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_profile);
        alertDialog.setIcon(R.drawable.ic_person_black_24dp);

        alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent profileIntent = new Intent(HomeActivity.this, Profile.class);
                startActivity(profileIntent);
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
        sliderLayout.stopAutoCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtFullName.setText(Common.currentUser.getName());
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
//        if (adapter != null)
//            adapter.startListening();
//        if (sharedPreferences.getBoolean("firstrun", true)) {
//            CompleteProfileNotification();
//            sharedPreferences.edit().putBoolean("firstrun", false)
//                    .commit();
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search)
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(HomeActivity.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(HomeActivity.this, OrderStatus.class);
            startActivity(orderIntent);

//        }
//        else if (id == R.id.nav_logout) {
//
//            ConfirmSignOutDialog();
        } else if (id == R.id.nav_profile) {

            Intent profileIntent = new Intent(HomeActivity.this, Profile.class);
            startActivity(profileIntent);
//        } else if (id == R.id.nav_settings) {
//
//            showSettingDialog();
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
        } else if (id == R.id.nav_about) {
            Intent aboutIntent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (id == R.id.nav_contact) {
            Intent contactIntent = new Intent(HomeActivity.this, ContactUs.class);
            startActivity(contactIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ConfirmSignOutDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Confirm Sign Out?");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);

        alertDialog.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Delete remember user && password
                Paper.book().destroy();

                //log out
                Intent logout = new Intent(HomeActivity.this, MainActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                AccountKit.logOut();
                startActivity(logout);

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showSettingDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("SETTINGS");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_setting = inflater.inflate(R.layout.setting_layout, null);

        final CheckBox ckb_sub_new = (CheckBox) layout_setting.findViewById(R.id.ckb_sub_new);
        //remember checkbox
        Paper.init(this);
        String isSubscribe = Paper.book().read("sub_new");
        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false"))
            ckb_sub_new.setChecked(false);
        else
            ckb_sub_new.setChecked(true);

        alertDialog.setView(layout_setting);
        alertDialog.setIcon(R.drawable.ic_settings_black_24dp);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (ckb_sub_new.isChecked()) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new", "true");
                    Toast.makeText(HomeActivity.this, "Subscribe Success!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                    Paper.book().write("sub_new", "false");
                    Toast.makeText(HomeActivity.this, "Unsubscribe Success!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

