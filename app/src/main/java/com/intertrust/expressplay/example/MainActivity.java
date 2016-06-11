package com.intertrust.expressplay.example;

import java.util.EnumSet;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.VideoView;

import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.Runtime;
import com.intertrust.wasabi.media.PlaylistProxy;
import com.intertrust.wasabi.media.PlaylistProxy.MediaSourceParams;
import com.intertrust.wasabi.media.PlaylistProxy.MediaSourceType;
import com.intertrust.wasabi.media.PlaylistProxyListener;

import orion.express.R;

/*
 * this enum simply maps the media types to the mimetypes required for the playlist proxy
 */
enum ContentTypes
{
	DASH("application/dash+xml"), HLS("application/vnd.apple.mpegurl"), PDCF(
	        "video/mp4"), M4F("video/mp4"), DCF("application/vnd.oma.drm.dcf"), BBTS(
	        "video/mp2t");

	String mediaSourceParamsContentType = null;

	private ContentTypes(String mediaSourceParamsContentType)
	{
		this.mediaSourceParamsContentType = mediaSourceParamsContentType;
	}

	public String getMediaSourceParamsContentType()
	{
		return mediaSourceParamsContentType;
	}
}

public class MainActivity extends Activity implements PlaylistProxyListener
{
	PlaylistProxy playerProxy;
	static final String TAG = "SamplePlayer";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create a MediaController for the video view
		VideoView videoView = (VideoView) findViewById(R.id.videoView1);
		MediaController mediaController = new MediaController(this, false);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);

		try {
			/*
			 * Initialize the Wasabi Runtime (necessary only once for each
			 * instantiation of the application)
			 * 
			 * ** Note: Set Runtime Properties as needed for your environment
			 */
			Runtime.initialize(getDir("wasabi", MODE_PRIVATE).getAbsolutePath());
			/*
			 * Personalize the application (acquire DRM keys). This is only
			 * necessary once each time the application is freshly installed
			 * 
			 * ** Note: personalize() is a blocking call and may take long
			 * enough to complete to trigger ANR (Application Not Responding)
			 * errors. In a production application this should be called in a
			 * background thread.
			 */

			if (!Runtime.isPersonalized())
				Runtime.personalize();

		}
		catch (NullPointerException e)
		{

			Log.d("NULL EXCEPTION EE","EXCEPTIOM"+e.getMessage());
			return;
		} catch (ErrorCodeException e)
		{
			// Consult WasabiErrors.txt for resolution of the error codes
			Log.e(TAG,
			        "runtime initialization or personalization error: "
			                + e.getLocalizedMessage());
			return;
		}
		/*
		 * create a playlist proxy and start it
		 */

		try {
//			Janki nagar rahimatpur bilhaur near jankai guest house
			EnumSet<PlaylistProxy.Flags> flags = EnumSet.noneOf(PlaylistProxy.Flags.class);
			playerProxy = new PlaylistProxy(flags, this, new Handler());
			playerProxy.start();
		} catch (ErrorCodeException e) {
			// Consult WasabiErrors.txt for resolution of the error codes
			Log.e(TAG, "playlist proxy error: " + e.getLocalizedMessage());
			return;
		}

		/*
		 * create a playlist proxy url and pass it to the native player
		 */
		try {
			/*
			 * Note that the MediaSourceType must be adapted to the stream type
			 * (DASH or HLS). Similarly, 
			 * the MediaSourceParams need to be set according to the media type
			 * if MediaSourceType is SINGLE_FILE
			 */
		
			String ms3_url = "https://ms3.test.expressplay.com:8443/hms/ms3/rights/?b=AAwAAwAAAxkAAlRXPGAAECBR0l7K6dxdkxUSrLNplXUAcDb87DeJyouvcWEmW_-VnmUCGj3vNRJP5f3R_455AWnHcflhQYwJ-O1XUvJYJGK_xtVeEXdJ6DFnwSyo5sRo0ewSCcp-S3MDZGtH2KvXzPBawG45q-VUIc9h2qBpjlnbmqzDkSXpNDm8SwKE0v2MzY0AAAAU6O8AXt97WK0NMFGHgfcAidReoRE#http%3A%2F%2Fd31lwto7rjo21l.cloudfront.net%2Fvideo%2FExpressPlayerDASH-BBB%2Fstream.mpd";
//			ContentTypes contentType = ContentTypes.HLS;
//
//			MediaSourceParams params = new MediaSourceParams();
//			params.sourceContentType = contentType
//			        .getMediaSourceParamsContentType();
//			/*
//			 * if the content has separate audio tracks (eg languages) you may
//			 * select one using MediaSourceParams, eg params.language="es";
//			 */
//			String contentTypeValue = contentType.toString();

			String url = playerProxy.makeUrl(ms3_url,
					MediaSourceType.DASH,
					new MediaSourceParams());
			//String url = playerProxy.makeUrl(ms3_url, MediaSourceType.valueOf((contentTypeValue=="HLS"||contentTypeValue=="DASH")?contentTypeValue:"SINGLE_FILE"), params);
			videoView.setVideoURI(Uri.parse(url));
			videoView.start();

		} catch (ErrorCodeException e) {
			// Consult WasabiErrors.txt for resolution of the error codes
			Log.e(TAG, "playback error: " + e.getLocalizedMessage());
			return;
		}
	}



	public void onErrorNotification(int errorCode, String errorString) {
		Log.e(TAG, "PlaylistProxy Event: Error Notification, error code = " +
			Integer.toString(errorCode) + ", error string = " +
			errorString);
	}

}
