package lp3.levaeu.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@SuppressLint("DefaultLocale")
public class ApplicationAdapter {
	
	private static final String getRotasUrlFormat 		= "%s/api/mobile/transportadora/%d/rotasDisponiveis";
	private static final String getSedeUrlFormat 		= "%s/api/mobile/transportadora/%d/sede";
	private static final String getVeiculosUrlFormat 	= "%s/api/mobile/transportadora/%d/veiculos";
	private static final String euQueroUrlFormat 		= "%s/api/mobile/veiculo/%d/rota/%d/euQuero";
	
    private static final String rota_id_tag             = "id";
    private static final String rota_distancia_tag      = "distancia_sede_km";
    private static final String rota_caminho_tag        = "caminho";
    private static final String geojson_coordenadas_tag = "coordinates";
    private static final String rota_pontos_tag         = "pontos";
    private static final String ponto_lat_tag           = "y";
    private static final String ponto_lng_tag           = "x";
    private static final String veiculo_id_tag          = "id";
    private static final String veiculo_placa_tag       = "placa";
    private static final String veiculo_renavam_tag     = "renavam";
    private static final String veiculo_capacidade_tag  = "capacidade";
    private static final String euQuero_mensagem_tag    = "mensagem";
	
	private static OkHttpClient client = new OkHttpClient();
	private static String urlBase;

	public static String getUrlBase() {
		return urlBase;
	}
	
	public static void setUrlBase(String url) {
		urlBase = url; 
	}
	
	private static List<LatLng> decodeRoute(JSONObject geoJson) throws JSONException {
		List<LatLng> r = new ArrayList<LatLng>();
		
		JSONArray linestring = geoJson.getJSONArray(geojson_coordenadas_tag);
		int numCoords = linestring.length();
		for (int j = 0; j < numCoords; j++) {
			JSONArray coord = linestring.getJSONArray(j);
			r.add(new LatLng(coord.getDouble(0), coord.getDouble(1)));
		}
		
		return r;
	}
	
	private static List<LatLng> decodePoints(JSONArray points) throws JSONException {
		List<LatLng> r = new ArrayList<LatLng>();
		
		int numPontos = points.length();
		for (int j = 0; j < numPontos; j++) {
			JSONObject ponto = points.getJSONObject(j);
			r.add(new LatLng(ponto.getDouble(ponto_lat_tag), ponto.getDouble(ponto_lng_tag)));
		}
		
		return r;
	}
	
	public static List<Rota> getRotas(int idTransportadora) {
		String getRotasUrl = String.format(getRotasUrlFormat, urlBase, idTransportadora);
		
		Request request = new Request.Builder().url(getRotasUrl).build();
		
		List<Rota> listaRotas = new ArrayList<Rota>();
		try {
			Response response = client.newCall(request).execute();
			
			JSONArray json = new JSONArray(response.body().string());
			int numRotas = json.length();
			for (int i = 0; i < numRotas; i++) {
				JSONObject jRota = json.getJSONObject(i);
				int id = jRota.getInt(rota_id_tag);
				double distSede = jRota.getDouble(rota_distancia_tag);
				List<LatLng> caminho = decodeRoute(jRota.getJSONObject(rota_caminho_tag));
				List<LatLng> pontos = decodePoints(jRota.getJSONArray(rota_pontos_tag));
				
				Rota rota = new Rota(id, distSede, caminho, pontos);
				listaRotas.add(rota);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return listaRotas;
	}
	
	public static LatLng getSede(int idTransportadora) {
		String getSedeUrl = String.format(getSedeUrlFormat, urlBase, idTransportadora);
		
		Request request = new Request.Builder().url(getSedeUrl).build();
		
		LatLng sede = null;
		try {
			Response response = client.newCall(request).execute();
			JSONObject json = new JSONObject(response.body().string());
			sede = new LatLng(json.getDouble(ponto_lat_tag), json.getDouble(ponto_lng_tag));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return sede;
	}
	
	public static List<Veiculo> getVeiculos(int idTransportadora) {
		String getVeiculosUrl = String.format(getVeiculosUrlFormat, urlBase, idTransportadora);
		
		Request request = new Request.Builder().url(getVeiculosUrl).build();
		
		List<Veiculo> listaVeiculos = new ArrayList<Veiculo>();
				
		try {
			Response response = client.newCall(request).execute();
			JSONArray json = new JSONArray(response.body().string());
			
			int numVeiculos = json.length();
			for (int i = 0; i < numVeiculos; i++) {
				JSONObject jVeiculo = json.getJSONObject(i);
				int id = jVeiculo.getInt(veiculo_id_tag);
				String placa = jVeiculo.getString(veiculo_placa_tag);
				int renavam = jVeiculo.getInt(veiculo_renavam_tag);
				int capacidade = jVeiculo.getInt(veiculo_capacidade_tag);
				
				Veiculo veiculo = new Veiculo(id, placa, renavam, capacidade);
				listaVeiculos.add(veiculo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return listaVeiculos;
	}
	
	public static String euQuero(int idVeiculo, int idRota) {
		String euQueroUrl = String.format(euQueroUrlFormat, urlBase, idVeiculo, idRota);
		
		Request request = new Request.Builder().url(euQueroUrl).build();
		String mensagem = null;
		try {
			Response response = client.newCall(request).execute();
			JSONObject json = new JSONObject(response.body().string());
			mensagem = json.getString(euQuero_mensagem_tag);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mensagem;
	}

	public static List<Rota> taskGetRotas(int idTransportadora) {
		AsyncTask<Integer, Void, List<Rota>> task = new AsyncTask<Integer, Void, List<Rota>>() {
			@Override
			protected List<Rota> doInBackground(Integer... params) {
				int idTransportadora = params[0];
				return ApplicationAdapter.getRotas(idTransportadora);
			}
		};
		task.execute(idTransportadora);
		
		List<Rota> rotas = null;
		try {
			rotas = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return rotas;
	}
	
	public static LatLng taskGetSede(int idTransportadora) {
		AsyncTask<Integer, Void, LatLng> task = new AsyncTask<Integer, Void, LatLng>() {
			
			@Override
			protected LatLng doInBackground(Integer... params) {
				int idTransportadora = params[0];
				return ApplicationAdapter.getSede(idTransportadora);
			}
		};
		
		task.execute(idTransportadora);
		LatLng sede = null;
		try {
			sede = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return sede;
	}
	
	public static List<Veiculo> taskGetVeiculos(int idTransportadora) {
		AsyncTask<Integer, Void, List<Veiculo>> task = new AsyncTask<Integer, Void, List<Veiculo>>() {
			@Override
			protected List<Veiculo> doInBackground(Integer... params) {
				int idTransportadora = params[0];
				return ApplicationAdapter.getVeiculos(idTransportadora);
			}
		};
		task.execute(idTransportadora);
		
		List<Veiculo> veiculos = null;
		try {
			veiculos = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return veiculos;
	}
	
	public static String taskEuQuero(Veiculo veiculo, Rota rota) {
		AsyncTask<Integer, Void, String> task = new AsyncTask<Integer, Void, String>() {
			@Override
			protected String doInBackground(Integer... params) {
				int idVeiculo = params[0];
				int idRota = params[1];
				return ApplicationAdapter.euQuero(idVeiculo, idRota);
			}
		};
		task.execute(veiculo.getId(), rota.getId());
		
		String mensagem = null;
		try {
			mensagem = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return mensagem;
	}
}
