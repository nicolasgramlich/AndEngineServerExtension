package org.andengine.extension.server.gcm.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.extension.server.gcm.util.constants.GCMConstants;
import org.andengine.util.math.MathUtils;
import org.andengine.util.net.HttpClientUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

import com.google.android.gcm.GCMRegistrar;

/**
 * (c) 2013 Nicolas Gramlich
 *
 * @author Nicolas Gramlich
 * @since 20:07:34 - 01.04.2013
 */
public final class GCMServerUtils implements GCMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int RETRY_COUNT = 5;
	private static final int RERTY_BACKOFF_MILLISECONDS = 2000;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	private GCMServerUtils() {

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static boolean register(final Context pContext, final String pServerURL, final String pGCMRegistrationID) {
		final String serverUrl = pServerURL + SERVER_ENDPOINT_REGISTER;

		final Map<String, String> params = new HashMap<String, String>();
		params.put(GCMServerUtils.SERVER_ENDPOINT_REGISTER_PARAMETER_REGISTRATIONID, pGCMRegistrationID);

		long backoff = GCMServerUtils.RERTY_BACKOFF_MILLISECONDS + MathUtils.random(1000);

		for (int i = 0; i < GCMServerUtils.RETRY_COUNT; i++) {
			try {
				GCMServerUtils.post(serverUrl, params);
				GCMRegistrar.setRegisteredOnServer(pContext, true);
				return true;
			} catch (final IOException e) {
				if (i < GCMServerUtils.RETRY_COUNT) {
					try {
						Thread.sleep(backoff);
					} catch (final InterruptedException e1) {
						Thread.currentThread().interrupt();
						return false;
					}
					backoff *= 2;
				}
			}
		}
		return false;
	}

	public static boolean unregister(final Context pContext, final String pServerURL, final String pGCMRegistrationID) {
		final String serverUrl = pServerURL + SERVER_ENDPOINT_UNREGISTER;

		final Map<String, String> params = new HashMap<String, String>();
		params.put(GCMServerUtils.SERVER_ENDPOINT_UNREGISTER_PARAMETER_REGISTRATIONID, pGCMRegistrationID);

		try {
			GCMServerUtils.post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(pContext, false);
			return true;
		} catch (final IOException e) {
			return false;
		}
	}

	private static int post(final String pServerURL, final Map<String, String> pParameters) throws IOException {
		final HttpClient httpClient = new DefaultHttpClient();

		try {
			final URI uri = new URI(pServerURL);
			final HttpPost httpPost = new HttpPost(uri);

			final List<NameValuePair> parameters = HttpClientUtils.convertParametersToNameValuePairs(pParameters);
			httpPost.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));

			final HttpResponse response = httpClient.execute(httpPost);

			return response.getStatusLine().getStatusCode();
		} catch (final URISyntaxException e) {
			throw new IOException(e.getMessage());
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
