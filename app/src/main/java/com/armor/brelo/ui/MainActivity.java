package com.armor.brelo.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.armor.brelo.DeviceScanActivity;
import com.armor.brelo.R;
import com.armor.brelo.SettingsActivity;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    static final int ITEMS = 2;
    ViewPager mPager;
    Toolbar toolbar;
    private DrawerLayout dLayout;
    private ListView mListView;
    private FloatingActionButton mAddLockFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new LocksAdapter(getSupportFragmentManager()));
        setNavigationDrawer();
        mAddLockFAB = (FloatingActionButton) findViewById(R.id.btn_fab_add_lock);
        mAddLockFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
//                Intent intent = new Intent(MainActivity.this, AddLockActivity.class);
                startActivity(intent);
            }
        });
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setTextColor(Color.BLACK);
        titleIndicator.setFooterLineHeight(0);
        titleIndicator.setSelectedColor(Color.BLACK);
        titleIndicator.setFooterIndicatorHeight(5);
        titleIndicator.setViewPager(mPager);
        mAddLockFAB.setColorFilter(Color.WHITE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dLayout.isDrawerOpen(Gravity.LEFT))
                    dLayout.closeDrawer(Gravity.LEFT);
                else
                    dLayout.openDrawer(Gravity.LEFT);
            }
        });
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new NavigationMenuAdapter());
        mListView.setOnItemClickListener(this);
        mListView.setDivider(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.menu_item_notification:
                Toast.makeText(this, "Notification selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                break;
            case 1:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    private class NavigationMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.navigation_menu_row, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            if(position == 0) {
                icon.setImageResource(R.drawable.locks);
                title.setText("LOCKS");
            } else {
                icon.setImageResource(R.drawable.settings);
                title.setText("SETTINGS");
            }
            return convertView;
        }
    }
    public static class LocksAdapter extends FragmentPagerAdapter {
        public LocksAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "MY LOCKS";
                case 1:
                    return "SHARED LOCKS";
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return MyLockListFragment.newInstance(0);
                case 1:
                    return SharedLockListFragment.newInstance(1);
                default:
                    return null;
            }
        }
    }

    private void setNavigationDrawer() {
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
    }
}
