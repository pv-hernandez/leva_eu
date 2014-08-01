package lp3.levaeu;

import java.util.List;

import lp3.levaeu.application.ApplicationAdapter;
import lp3.levaeu.application.Rota;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;

public class Notificacoes extends Service {

	public static final int id = 1234567874;
	private WakeLock lock;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleIntent(intent);
		return START_NOT_STICKY;
	}
	
	private void handleIntent(Intent intent) {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "leva_eu");
		lock.acquire();
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (!cm.getBackgroundDataSetting()) {
			stopSelf();
			return;
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String urlBase = prefs.getString("urlBase", null);
		int idTransportadora = prefs.getInt("idTransportadora", 0);
		
		if (prefs.getBoolean(PreferenciasActivity.NOTIFICAR, true)) {
			ApplicationAdapter.setUrlBase(urlBase);
			new Worker().execute(idTransportadora);
		}
	}
	
	private class Worker extends AsyncTask<Integer, Void, List<Rota>> {

		@Override
		protected List<Rota> doInBackground(Integer... params) {
			int idTransportadora = params[0];
			return ApplicationAdapter.getRotas(idTransportadora);
		}
		
		@Override
		protected void onPostExecute(List<Rota> result) {
			if (result.size() > 0) {
				Notification.Builder builder = new Notification.Builder(Notificacoes.this);
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setContentTitle("Leva Eu");
				builder.setContentText("Você tem novas rotas disponíveis!");
				builder.setAutoCancel(true);
				
				Intent notClick = new Intent(Notificacoes.this, MainActivity.class);
				
				TaskStackBuilder stack = TaskStackBuilder.create(Notificacoes.this);
				stack.addParentStack(MainActivity.class);
				stack.addNextIntent(notClick);
				
				PendingIntent pi = stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(pi);
				
				NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				nm.notify(id, builder.getNotification());
			}
			stopSelf();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		lock.release();
	}
}
