package com.example.fingersynthesis;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {

	AudioSynthesisTask audioSynth;
	static final float BASE_FREQUENCY = 440;
	float synth_frequency = BASE_FREQUENCY;
	boolean play = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.activity_main);

		View mainView = this.findViewById(R.id.MainView);
		mainView.setOnTouchListener(this);

		audioSynth = new AudioSynthesisTask();
		audioSynth.execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		play = false;
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			play = true;
			synth_frequency = event.getX() + BASE_FREQUENCY;
			Log.v("FREQUENCY", "" + synth_frequency);
			break;
		case MotionEvent.ACTION_MOVE:
			play = true;
			synth_frequency = event.getX() + BASE_FREQUENCY;
			Log.v("FREQUENCY", "" + synth_frequency);
			break;
		case MotionEvent.ACTION_UP:
			play = false;
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}
		
		return true;
	}

	private class AudioSynthesisTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			final int SAMPLE_RATE = 11025;

			int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minSize,
					AudioTrack.MODE_STREAM);

			audioTrack.play();

			short[] buffer = new short[minSize];
			float angle = 0;

			while (true) {

				if (play) {

					for (int i = 0; i < buffer.length; i++) {
						float angular_frequency = (float) (2 * Math.PI)
								* synth_frequency / SAMPLE_RATE;
						buffer[i] = (short) (Short.MAX_VALUE * ((float) Math
								.sin(angle)));
						angle += angular_frequency;
					}
					audioTrack.write(buffer, 0, buffer.length);
				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
