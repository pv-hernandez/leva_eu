package lp3.levaeu;

import java.util.List;

import lp3.levaeu.application.Rota;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class RotasArrayAdapter extends ArrayAdapter<Rota> {

	public RotasArrayAdapter(Context context) {
		super(context, R.layout.item_rota);
	}

	public RotasArrayAdapter(Context context, List<Rota> objects) {
		super(context, R.layout.item_rota, objects);
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		Rota rota = this.getItem(position);
		
		if(view == null)
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.item_rota, parent, false);
 
        final TextView distancia = (TextView) view.findViewById(R.id.item_text_distancia);
        final TextView percurso = (TextView) view.findViewById(R.id.item_text_percurso);
        final ImageButton opcoes = (ImageButton) view.findViewById(R.id.item_rota_opcoes);
        
        distancia.setText(String.format("%1.1f Km", rota.getDistancia()));
        percurso.setText(String.format("%1.1f Km", rota.getPercurso()));
        opcoes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu menu = new PopupMenu(RotasArrayAdapter.this.getContext(), opcoes);
				menu.getMenuInflater().inflate(R.menu.item_rota_menu, menu.getMenu());
				
				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					
					private void verNoMapa(Rota r) {
						Intent paraMapa = new Intent(RotasArrayAdapter.this.getContext(), MapaActivity.class);
						paraMapa.putExtra("rota", r);
						RotasArrayAdapter.this.getContext().startActivity(paraMapa);
					}
					
					private void proposta(Rota r) {
						Intent paraProposta = new Intent(RotasArrayAdapter.this.getContext(), VeiculosActivity.class);
						paraProposta.putExtra("rota", r);
						RotasArrayAdapter.this.getContext().startActivity(paraProposta);
					}
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
						case R.id.ver_no_mapa:
							verNoMapa(RotasArrayAdapter.this.getItem(position));
							return true;
						case R.id.proposta:
							proposta(RotasArrayAdapter.this.getItem(position));
							return true;
							default:
								return false;
						}
					}
				});
				
				menu.show();
			}
		});
        
        return view;
	}

}
