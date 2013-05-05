package org.andengine.extension.server.gcm.util;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gcm.GCMRegistrar;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 20:41:56 - 20.08.2010
 */
public class GCMRegisterAsyncTask extends AsyncTask<Void, Void, Void> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final String mGCMRegistrationID;
	private final Context mContext;
	private final String mServerURL;

	// ===========================================================
	// Constructors
	// ===========================================================

	public GCMRegisterAsyncTask(final Context pContext, final String pServerURL, final String pGCMRegistrationID) {
		this.mContext = pContext;
		this.mServerURL = pServerURL;
		this.mGCMRegistrationID = pGCMRegistrationID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected Void doInBackground(final Void ... params) {
		/* Try to register with our server: */
		final boolean registered = GCMServerUtils.register(this.mContext, this.mServerURL, this.mGCMRegistrationID);

		/* If it failed, unregister from GCM itself, so it will be retried next startup: */
		if (!registered) {
			GCMRegistrar.unregister(this.mContext);
		}
		return null;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
