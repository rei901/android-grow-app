package org.tndata.android.grow.activity;

import java.util.ArrayList;

import org.tndata.android.grow.GrowApplication;
import org.tndata.android.grow.R;
import org.tndata.android.grow.adapter.DrawerAdapter;
import org.tndata.android.grow.adapter.MainViewPagerAdapter;
import org.tndata.android.grow.fragment.CategoryFragment.CategoryFragmentListener;
import org.tndata.android.grow.fragment.MyGoalsFragment.MyGoalsFragmentListener;
import org.tndata.android.grow.model.Category;
import org.tndata.android.grow.model.DrawerItem;
import org.tndata.android.grow.model.Goal;
import org.tndata.android.grow.task.GetUserCategoriesTask;
import org.tndata.android.grow.task.GetUserCategoriesTask.GetUserCategoriesListener;
import org.tndata.android.grow.task.GetUserGoalsTask;
import org.tndata.android.grow.task.GetUserGoalsTask.GetUserGoalsListener;
import org.tndata.android.grow.util.Constants;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements
        GetUserCategoriesListener, GetUserGoalsListener,
        MyGoalsFragmentListener, CategoryFragmentListener {
    private static final int IMPORTANT_TO_ME = 0;
    private static final int MY_GOALS = 1;
    private static final int MY_PRIVACY = 2;
    private static final int SETTINGS = 3;
    private static final int DRAWER_COUNT = 4;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ArrayList<DrawerItem> mDrawerItems;
    private DrawerAdapter mDrawerAdapter = null;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private MainViewPagerAdapter mAdapter;
    private boolean mDrawerIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.transparent_tool_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.main_left_drawer_list);
        mDrawerItems = drawerItems();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerAdapter = new DrawerAdapter(this, R.layout.list_item_nav_drawer,
                mDrawerItems);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.nav_drawer_action, R.string.nav_drawer_action) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mDrawerIsOpen = false;
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerIsOpen = true;
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setAdapter(mAdapter);

        showCategories();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.LOGGED_OUT_RESULT_CODE) {
            finish();
        } else if (requestCode == Constants.CHOOSE_CATEGORIES_REQUEST_CODE) {
            showCategories();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void chooseCategories() {
        Intent intent = new Intent(getApplicationContext(), ChooseCategoriesActivity.class);
        startActivityForResult(intent, Constants.CHOOSE_CATEGORIES_REQUEST_CODE);
    }

    public void showCategories() {
        if (((GrowApplication) getApplication()).getCategories() == null) {
            new GetUserCategoriesTask(this).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    ((GrowApplication) getApplication()).getToken());
        } else {
            for (Category cat : ((GrowApplication) getApplication())
                    .getCategories()) {
                Log.d("Category", cat.getTitle());
            }
            mAdapter.setCategories(((GrowApplication) getApplication())
                    .getCategories());
            showGoals();

            mAdapter.notifyDataSetChanged();
        }
    }

    public void showGoals() {
        if (((GrowApplication) getApplication()).getGoals() == null) {
            new GetUserGoalsTask(this).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    ((GrowApplication) getApplication()).getToken());
        } else {
            for (Goal goal : ((GrowApplication) getApplication()).getGoals()) {
                Log.d("Goal", goal.getTitle());
            }
            Intent intent = new Intent(Constants.GOAL_UPDATED_BROADCAST_ACTION);
            sendBroadcast(intent);
        }
    }

    protected class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = null;
            switch (position) {
                case IMPORTANT_TO_ME:
                    break;
                case MY_GOALS:
                    break;
                case MY_PRIVACY:
                    break;

                case SETTINGS:
                    intent = new Intent(getApplicationContext(),
                            SettingsActivity.class);
                    startActivityForResult(intent, Constants.SETTINGS_REQUEST_CODE);
                    break;
            }
            mDrawerLayout.closeDrawers();
        }
    }

    private ArrayList<DrawerItem> drawerItems() {
        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        for (int i = 0; i < DRAWER_COUNT; i++) {
            DrawerItem item = new DrawerItem();
            switch (i) {
                case IMPORTANT_TO_ME:
                    item.drawable = getResources().getDrawable(
                            R.drawable.ic_arrow_back_white);
                    item.text = getResources().getString(
                            R.string.action_important_to_me);
                    break;
                case MY_GOALS:
                    item.drawable = getResources().getDrawable(
                            R.drawable.ic_arrow_back_white);
                    item.text = getResources().getString(R.string.action_my_goals);
                    break;
                case MY_PRIVACY:
                    item.drawable = getResources().getDrawable(
                            R.drawable.ic_arrow_back_white);
                    item.text = getResources()
                            .getString(R.string.action_my_privacy);
                    break;
                case SETTINGS:
                    item.text = getResources().getString(R.string.action_settings);
                    break;
            }
            items.add(item);
        }

        return items;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            if (mDrawerIsOpen) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void categoriesLoaded(ArrayList<Category> categories) {
        ((GrowApplication) getApplication()).setCategories(categories);
        showCategories();
    }

    @Override
    public void goalsLoaded(ArrayList<Goal> goals) {
        ((GrowApplication) getApplication()).setGoals(goals);
        assignGoalsToCategories(false);
        showGoals();
    }

    @Override
    public void assignGoalsToCategories(boolean shouldSendBroadcast) {
        //this is messy...
        //add each goal to the correct category
        for (Category category : ((GrowApplication) getApplication()).getCategories()) {
            ArrayList<Goal> categoryGoals = new ArrayList<Goal>();
            for (Goal goal : ((GrowApplication) getApplication()).getGoals()) {
                for (Category goalCategory : goal.getCategories()) {
                    if (goalCategory.getId() == category.getId()) {
                        categoryGoals.add(goal);
                        break;
                    }
                }
            }
            category.setGoals(categoryGoals);
        }
        if(shouldSendBroadcast) {
            Intent intent = new Intent(Constants.GOAL_UPDATED_BROADCAST_ACTION);
            sendBroadcast(intent);
            Log.d("Main Activity", "send broadcast");
        }
    }
}
