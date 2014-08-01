package lp3.levaeu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class PreferenciasActivity extends Activity {

	private SharedPreferences preferences;
	
	public static final String ID_TRANSP = "idTransportadora";
	public static final String URL_BASE = "urlBase";
	public static final String NOTIFICAR = "notificar";
	public static final String INTERVALO = "intervalo";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferencias_activity);
		
		final TextView idTransp = (TextView) findViewById(R.id.prefs_text_id_transp);
		final TextView urlBase = (TextView) findViewById(R.id.prefs_text_url);
		final CheckBox notify = (CheckBox) findViewById(R.id.prefs_cbx_notify);
		final TextView interval = (TextView) findViewById(R.id.prefs_text_interval);
		Button apply = (Button) findViewById(R.id.prefs_button_apply);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		idTransp.setText(String.valueOf(preferences.getInt(ID_TRANSP, 0)));
		urlBase.setText(preferences.getString(URL_BASE, "http://"));
		notify.setChecked(preferences.getBoolean(NOTIFICAR, true));
		interval.setText(String.valueOf(preferences.getInt(INTERVALO, 1)));
		
		apply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor edit = preferences.edit();
				edit.putInt(ID_TRANSP, Integer.valueOf(idTransp.getText().toString()));
				edit.putString(URL_BASE, urlBase.getText().toString());
				edit.putBoolean(NOTIFICAR, notify.isChecked());
				edit.putInt(INTERVALO, Integer.valueOf(interval.getText().toString()));
				edit.commit();
				
				if (preferences.getBoolean(NOTIFICAR, true)) {
					agendar(PreferenciasActivity.this);
				}
				
				PreferenciasActivity.this.finish();
			}
		});
	}

	public static void agendar(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int intervalo = prefs.getInt(INTERVALO, 1) * 60 * 1000;
		boolean nots = prefs.getBoolean(NOTIFICAR, true);
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent iniciaNots = new Intent(context, Notificacoes.class);
		PendingIntent pi = PendingIntent.getService(context, 0, iniciaNots, 0);
		am.cancel(pi);
		if (nots && intervalo > 0) {
			am.setInexactRepeating(
					AlarmManager.ELAPSED_REALTIME_WAKEUP, 
					SystemClock.elapsedRealtime() + intervalo, 
					intervalo, 
					pi);
		}
	}
	
}
