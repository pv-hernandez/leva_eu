package lp3.levaeu;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class ActionTabListener implements TabListener {

	private Fragment fragment;
	
	public ActionTabListener(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.main_layout, fragment);
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }
 
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
 
    }
	
}
