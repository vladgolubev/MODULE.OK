package ua.samosfator.moduleok.fragment.modules;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ua.samosfator.moduleok.utils.App;
import ua.samosfator.moduleok.R;

class ModulesPagerAdapter extends FragmentPagerAdapter {

    private final Resources appResources = App.getContext().getResources();

    private final int TYPICAL_MODULES_COUNT = 4;
    private int maxModulesCount = TYPICAL_MODULES_COUNT;
    private List<ModuleFragment> moduleFragmentCacheList = new ArrayList<>(maxModulesCount);

    private final String[] titles = {
            appResources.getString(R.string.module_1_name), appResources.getString(R.string.module_2_name),
            appResources.getString(R.string.module_3_name), appResources.getString(R.string.module_4_name),
            appResources.getString(R.string.module_5_name), appResources.getString(R.string.module_6_name)
    };
    public ModulesPagerAdapter(FragmentManager fm, int maxModulesCount) {
        super(fm);
        this.maxModulesCount = maxModulesCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return maxModulesCount;
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= moduleFragmentCacheList.size()) {
            ModuleFragment moduleFragment = new ModuleFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("module", position);
            moduleFragment.setArguments(bundle);

            moduleFragmentCacheList.add(moduleFragment);

            return moduleFragment;
        } else {
            return moduleFragmentCacheList.get(position);
        }
    }
}
