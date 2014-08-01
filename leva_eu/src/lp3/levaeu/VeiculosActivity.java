package lp3.levaeu;

import lp3.levaeu.application.ApplicationAdapter;
import lp3.levaeu.application.Rota;
import lp3.levaeu.application.Veiculo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class VeiculosActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_veiculos);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int idTransportadora = prefs.getInt("idTransportadora", 0);
		
		ListView listaVeiculos = (ListView) findViewById(R.id.veiculos_listview);
		final VeiculosArrayAdapter adapter = new VeiculosArrayAdapter(this, ApplicationAdapter.taskGetVeiculos(idTransportadora));
		listaVeiculos.setAdapter(adapter);
		
		listaVeiculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				final Veiculo v = adapter.getItem(position);
				final Rota r = VeiculosActivity.this.getIntent().getParcelableExtra("rota");
				
				AlertDialog.Builder builder = new AlertDialog.Builder(VeiculosActivity.this);
				builder.setTitle("Deseja realizar a proposta?");
				builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						String mensagem = ApplicationAdapter.taskEuQuero(v, r);
						Toast resposta = Toast.makeText(VeiculosActivity.this, mensagem, Toast.LENGTH_LONG);
						resposta.show();
					}
				});
				builder.setNegativeButton("Não", null);
				builder.show();
			}
		});
	}
}
