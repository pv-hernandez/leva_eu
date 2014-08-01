package lp3.levaeu;

//import com.google.android.gms.maps.model.LatLng;

//import android.app.ActionBar;

import android.app.Activity;
import android.content.Intent;
//import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (findViewById(R.id.main_layout) != null) {
			if (savedInstanceState != null) {
				return;
			}
			
			NovasRotasFragment f = new NovasRotasFragment();
			
			getFragmentManager().beginTransaction().add(R.id.main_layout, f).commit();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		NovasRotasFragment f = new NovasRotasFragment();
		getFragmentManager().beginTransaction().replace(R.id.main_layout, f).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void optionsMenu() {
		Intent paraPrefs = new Intent(this, PreferenciasActivity.class);
		this.startActivity(paraPrefs);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opcoes:
			optionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
