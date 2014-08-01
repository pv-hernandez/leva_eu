package lp3.levaeu;

import java.util.List;

import lp3.levaeu.application.Veiculo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class VeiculosArrayAdapter extends ArrayAdapter<Veiculo> {
	
	public VeiculosArrayAdapter(Context context) {
		super(context, R.layout.item_rota);
	}

	public VeiculosArrayAdapter(Context context, List<Veiculo> objects) {
		super(context, R.layout.item_rota, objects);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Veiculo veiculo = this.getItem(position);
		
		if (view == null) {
			view = LayoutInflater.from(this.getContext()).inflate(R.layout.item_veiculo, parent, false);
		}
		
		TextView placa = (TextView) view.findViewById(R.id.veiculo_placa_text);
		TextView renavam = (TextView) view.findViewById(R.id.veiculo_renavam_text);
		TextView capacidade = (TextView) view.findViewById(R.id.veiculo_capacidade_text);
		
		placa.setText(veiculo.getPlaca());
		renavam.setText(String.valueOf(veiculo.getRenavam()));
		capacidade.setText(String.valueOf(veiculo.getCapacidade()));
		
		return view;
	}
}
