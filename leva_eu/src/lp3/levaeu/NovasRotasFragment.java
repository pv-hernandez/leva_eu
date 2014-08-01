package lp3.levaeu;

import lp3.levaeu.application.ApplicationAdapter;
import lp3.levaeu.application.Rota;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class NovasRotasFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		final View rootView = inflater.inflate(R.layout.fragment_novas_rotas, container, false);
        final int idTransportadora = prefs.getInt("idTransportadora", 0);
		
        ListView listRotas = (ListView) rootView.findViewById(R.id.novas_rotas_listview);

        final RotasArrayAdapter adapter = new RotasArrayAdapter(rootView.getContext(), ApplicationAdapter.taskGetRotas(idTransportadora));
		listRotas.setAdapter(adapter);
		
		listRotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent paraMapa = new Intent(rootView.getContext(), MapaActivity.class);
				
				Rota rota = adapter.getItem(pos);

				paraMapa.putExtra("rota", (Parcelable) rota);
				
				rootView.getContext().startActivity(paraMapa);
			}
		});
        
        return rootView;
    }
}
