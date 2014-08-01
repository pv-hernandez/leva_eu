package lp3.levaeu;

import lp3.levaeu.application.ApplicationAdapter;
import lp3.levaeu.application.Rota;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class MapaActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final int idTransportadora = prefs.getInt("idTransportadora", 0);
		LatLng sede = ApplicationAdapter.taskGetSede(idTransportadora);
		
		final Rota rota = (Rota) getIntent().getParcelableExtra("rota");
		
		Button proposta = (Button) findViewById(R.id.button_proposta);
		proposta.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent paraVeiculos = new Intent(MapaActivity.this, VeiculosActivity.class);
				paraVeiculos.putExtra("rota", (Parcelable) rota);
				MapaActivity.this.startActivity(paraVeiculos);
			}
		});
		
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		GoogleMap map = mapFragment.getMap();
		
		try {
			rota.desenharInicio(map, sede);
			rota.desenharFim(map, sede);
		} catch (NullPointerException e) {
			// vlw flw
		}
		rota.desenharCaminho(map);
		rota.desenharSede(map, sede);
		rota.mostrar(map, sede);
	}
}
