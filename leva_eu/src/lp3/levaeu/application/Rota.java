package lp3.levaeu.application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.os.Parcel;
import android.os.Parcelable;

import com.directions.route.Routing;
import com.directions.route.Routing.TravelMode;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Rota implements Parcelable {

	private int id;
	private double distancia;
	private List<LatLng> caminho;
	private List<LatLng> pontos;
	
	private LatLng sede;
	private List<LatLng> inicio;
	private List<LatLng> fim;
	private Routing taskInicio;
	private Routing taskFim;
	
	public static final int AZUL = 0xff0000ff;
	public static final int VERDE = 0xff00ff00;
	public static final int VERMELHO = 0xffff0000;
		
	public Rota(int id, double distancia, List<LatLng> rota, List<LatLng> pontos) {
		this.id = id;
		this.distancia = distancia;
		this.caminho = rota;
		this.pontos = pontos;
		
		this.inicio = null;
		this.fim = null;
		this.sede = null;
		this.taskInicio = new Routing(TravelMode.DRIVING);
		this.taskFim = new Routing(TravelMode.DRIVING);
	}
	
	private Rota(Parcel in) {
		this.id = in.readInt();
		this.distancia = in.readDouble();
		this.caminho = in.createTypedArrayList(LatLng.CREATOR);
		this.pontos = in.createTypedArrayList(LatLng.CREATOR);
		
		this.inicio = null;
		this.fim = null;
		this.sede = null;
		this.taskInicio = new Routing(TravelMode.DRIVING);
		this.taskFim = new Routing(TravelMode.DRIVING);
	}
	
	public void addCoord(LatLng coord) {
		this.caminho.add(coord);
	}
	
	public void addPonto(LatLng ponto) {
		this.pontos.add(ponto);
	}
	
	public int getId() {
		return this.id;
	}
	
	public double getDistancia() {
		return this.distancia;
	}
	
	private static double dist(LatLng a, LatLng b) {
		final double earth_radius_km = 6371;
		double lat1 = a.latitude;
	    double lat2 = b.latitude;
	    double lon1 = a.longitude;
	    double lon2 = b.longitude;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLon = Math.toRadians(lon2-lon1);
	    double x = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	    Math.sin(dLon/2) * Math.sin(dLon/2);
	    double c = 2 * Math.asin(Math.sqrt(x));
	    return earth_radius_km * c;
	}
	
	public double getPercurso() {
		double percurso = 0;
		for (int i = 1; i < caminho.size(); i++) {
			percurso += dist(caminho.get(i - 1), caminho.get(i));
		}
		return percurso;
	}
	
	@SuppressWarnings("unchecked")
	public Polyline desenharInicio(GoogleMap map, LatLng sede) {
		if (this.sede != sede) {
			this.sede = sede;
			this.inicio = null;
		}
		if (this.inicio == null) {
			inicio = new ArrayList<LatLng>();
			inicio.add(this.sede);
			inicio.add(caminho.get(0));
			taskInicio.execute(inicio);
			try {
				inicio = taskInicio.get().getPoints();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		PolylineOptions options = new PolylineOptions();
		options.geodesic(true);
		options.color(VERDE);
		options.addAll(inicio);
		
		return map.addPolyline(options);
	}
	
	@SuppressWarnings("unchecked")
	public Polyline desenharFim(GoogleMap map, LatLng sede) {
		if (this.sede != sede) {
			this.sede = sede;
			this.fim = null;
		}
		if (this.fim == null) {
			fim = new ArrayList<LatLng>();
			fim.add(caminho.get(caminho.size() - 1));
			fim.add(this.sede);
			taskFim.execute(fim);
			try {
				fim = taskFim.get().getPoints();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		PolylineOptions options = new PolylineOptions();
		options.geodesic(true);
		options.color(VERMELHO);
		options.addAll(fim);
		
		return map.addPolyline(options);
	}
	
	public Polyline desenharCaminho(GoogleMap map) {
		PolylineOptions options = new PolylineOptions();
		options.geodesic(true);
		options.addAll(caminho);
		options.color(AZUL);
		
		for (int i = 0; i < pontos.size(); i++) {
			LatLng marca = pontos.get(i);
			MarkerOptions mo = new MarkerOptions().position(marca);
			
			if (i == 0) {
				mo.title("Início");
			} else if (i == pontos.size() - 1) {
				mo.title("Fim");
			} else {
				mo.icon(BitmapDescriptorFactory.defaultMarker(
						BitmapDescriptorFactory.HUE_GREEN));
			}
			
			map.addMarker(mo);
		}
		
		return map.addPolyline(options);
	}
	
	public Marker desenharSede(GoogleMap map, LatLng sede) {
		MarkerOptions mo = new MarkerOptions().position(sede);
		mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		mo.title("Transportadora");
		return map.addMarker(mo);
	}
	
	public void mostrar(GoogleMap map, LatLng sede) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng ponto : caminho) {
			builder.include(ponto);
		}
		if (inicio != null) {
			for (LatLng ponto : inicio) {
				builder.include(ponto);
			}
		}
		if (fim != null) {
			for (LatLng ponto : fim) {
				builder.include(ponto);
			}
		}
		
		CameraUpdate update = 
				CameraUpdateFactory.newLatLngBounds(
						builder.build(), 1000, 1000, 100);
		map.moveCamera(update);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeDouble(distancia);
		out.writeTypedList(caminho);
		out.writeTypedList(pontos);
	}
	
	public static final Parcelable.Creator<Rota> CREATOR = new Parcelable.Creator<Rota>() {
        public Rota createFromParcel(Parcel in) {
            return new Rota(in); 
        }

        public Rota[] newArray(int size) {
            return new Rota[size];
        }
    };
}
