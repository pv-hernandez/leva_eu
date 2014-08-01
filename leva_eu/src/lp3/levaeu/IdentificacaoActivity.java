package lp3.levaeu;

import lp3.levaeu.application.ApplicationAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IdentificacaoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identificacao);
		
		final EditText editText = (EditText) findViewById(R.id.ident_id_transp);
		final EditText urlText = (EditText) findViewById(R.id.ident_app_url);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editText.setText(String.valueOf(prefs.getInt(PreferenciasActivity.ID_TRANSP, 0)));
		urlText.setText(prefs.getString(PreferenciasActivity.URL_BASE, "http://"));
		
		Button button = (Button) findViewById(R.id.ident_identifica);
		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ApplicationAdapter.setUrlBase(urlText.getText().toString());
				Intent paraMain = new Intent(IdentificacaoActivity.this, MainActivity.class);
				int idT = Integer.valueOf(editText.getText().toString());
				String urlBase = urlText.getText().toString();
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(IdentificacaoActivity.this);
				prefs.edit()
					.putString(PreferenciasActivity.URL_BASE, urlBase)
					.putInt(PreferenciasActivity.ID_TRANSP, idT)
					.commit();
				
				PreferenciasActivity.agendar(IdentificacaoActivity.this);
				
				IdentificacaoActivity.this.startActivity(paraMain);
				IdentificacaoActivity.this.finish();
			}
		});
	}
}
