package org.andengine.extension.server.gcm.util;

import org.andengine.extension.server.gcm.util.constants.GCMConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * (c) 2013 Nicolas Gramlich
 *
 * @author Nicolas Gramlich
 * @since 20:07:34 - 01.04.2013
 */
public final class GCMUtils implements GCMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final IntentFilter INTENTFILTER_GCM_MESSAGES = new IntentFilter(GCMConstants.ACTION_GCM_MESSAGE);

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	private GCMUtils() {

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

	public static void registerGCMMessageBroadcastReceiver(final Context pContext, final BroadcastReceiver pBroadcastReceiver) {
		pContext.registerReceiver(pBroadcastReceiver, GCMUtils.INTENTFILTER_GCM_MESSAGES);
	}

	public static void unregisterGCMMessageBroadcastReceiver(final Context pContext, final BroadcastReceiver pBroadcastReceiver) {
		pContext.unregisterReceiver(pBroadcastReceiver);
	}

	public static void broadcastGCMMessage(final Context pContext, final Bundle pExtras) {
		final Intent intent = new Intent(ACTION_GCM_MESSAGE);
		intent.putExtras(pExtras);
		pContext.sendBroadcast(intent);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
