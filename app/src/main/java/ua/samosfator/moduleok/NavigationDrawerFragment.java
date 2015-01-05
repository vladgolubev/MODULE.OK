package ua.samosfator.moduleok;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ua.samosfator.moduleok.recyclerview.DrawerSection;
import ua.samosfator.moduleok.recyclerview.adapter.SectionAdapter;

public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "NavigationDrawerFragment";
    public static final String KEY_USER_SAW_DRAWER = "user_saw_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private List<DrawerSection> mSections;

    private RecyclerView mRecyclerView;
    private SectionAdapter mSectionAdapter;

    private boolean mUserSawDrawer;
    private boolean mFromSavedInstanceState;

    private View containerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserSawDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_SAW_DRAWER, "false"));
        if (savedInstanceState == null) {
            mFromSavedInstanceState = true;
        }
        initSections();
    }

    private void initSections() {
        mSections = new ArrayList<>();
        mSections.add(new DrawerSection("Subjects", R.drawable.ic_format_list_numbers_grey600_24dp));
        mSections.add(new DrawerSection("Modules", R.drawable.ic_file_document_box_grey600_24dp));
        mSections.add(new DrawerSection("Stats", R.drawable.ic_poll_grey600_24dp));
        mSections.add(new DrawerSection("Log in", R.drawable.ic_login_grey600_24dp));//TODO: Log out in case of logged-in
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.drawer_list);
        mSectionAdapter = new SectionAdapter(getActivity(), mSections);
        mRecyclerView.setAdapter(mSectionAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserSawDrawer) {
                    mUserSawDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_SAW_DRAWER, String.valueOf(mUserSawDrawer));
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserSawDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
}
