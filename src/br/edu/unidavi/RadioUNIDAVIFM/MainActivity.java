package br.edu.unidavi.RadioUNIDAVIFM;

import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements
		MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener
		, OnClickListener  {

	private String TAG = getClass().getSimpleName();
	private static final MediaPlayer mp = new MediaPlayer();

	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private static final int HELLO_ID = 1;
	
	Intent playbackServiceIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		playButton = (Button) this.findViewById(R.id.btn_play);
	    stopButton = (Button) this.findViewById(R.id.btn_stop);

	    playButton.setOnClickListener(this);
	    stopButton.setOnClickListener(this);

	    playbackServiceIntent = new Intent(this, BackgroundRadioService.class);
	    
//		play = (Button) findViewById(R.id.btn_play);
//		play.setOnClickListener(this);
//		
//		pause = (Button) findViewById(R.id.btn_pause);
//		pause.setOnClickListener(this);
//		
//		stop = (Button) findViewById(R.id.btn_stop);
//		stop.setOnClickListener(this);
	}

	public void onClick(View v) {
		Log.e("teste", v.toString());
		switch (v.getId()) {
		case R.id.btn_play:
			Log.e("Play", "Button Play");
			try {
				play();
			} catch (Exception e) {
				Log.e("Play", "ERRO no Play");
			}
			break;
		case R.id.btn_pause:
			Log.e("Pause", "Button Pause");
			try {
				pause();
			} catch (Exception e) {
				Log.e("Pause", "ERRO no Pause");
			}
			break;
		case R.id.btn_stop:
			Log.e("Stop", "Button Stop");
			try {
				stop();
			} catch (Exception e) {
				Log.e("Stop", "ERRO no Stop");
			}
			break;
		default:
			break;
		}
	}
	
	private void play() {
		Uri myUri = Uri.parse("http://200.135.228.21:8000/");
		try {
			if (mp == null) {
				//this.mp;
			} else {
				mp.stop();
				mp.reset();
			}
			mp.setDataSource(this, myUri); // Go to Initialized state
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setOnPreparedListener(this);
			mp.setOnBufferingUpdateListener(this);

			mp.setOnErrorListener(this);
			mp.prepareAsync();
			
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);		
			int icon = R.drawable.icon_radio;
			CharSequence tickerText = "Bem Vindo! Rádio UNIDAVI";
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, tickerText, when);
			
			Context context = getApplicationContext();
			CharSequence contentTitle = "Rádio UNIDAVI";
			CharSequence contentText = "Seja Bem Vindo!";
			Intent notificationIntent = new Intent(this, MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			
			mNotificationManager.notify(HELLO_ID, notification);
			
			Log.d(TAG, "LoadClip Done");
		} catch (Throwable t) {
			Log.d(TAG, t.toString());
		}
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "Stream is prepared");
		mp.start();
	}

	private void pause() {
		mp.pause();
		mp.stop();
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);		
		int icon = R.drawable.icon_radio;
		CharSequence tickerText = "Pausa Rádio UNIDAVI.";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "Rádio UNIDAVI";
		CharSequence contentText = "Rádio em pausa!";
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		mNotificationManager.notify(HELLO_ID, notification);
	}

	private void stop() {
		mp.stop();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(HELLO_ID);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//stop();
	}

	public void onCompletion(MediaPlayer mp) {
		stop();
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		StringBuilder sb = new StringBuilder();
		sb.append("Media Player Error: ");
		switch (what) {
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
			sb.append("Not Valid for Progressive Playback");
			break;
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
			sb.append("Server Died");
			break;
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
			sb.append("Unknown");
			break;
		default:
			sb.append(" Non standard (");
			sb.append(what);
			sb.append(")");
		}
		sb.append(" (" + what + ") ");
		sb.append(extra);
		Log.e(TAG, sb.toString());
		return true;
	}

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Log.d(TAG, "PlayerService onBufferingUpdate : " + percent + "%");
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}

}
