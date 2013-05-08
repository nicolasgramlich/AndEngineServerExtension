package org.andengine.extension.server.util.constants;

/**
 * (c) 2013 Nicolas Gramlich
 *
 * @author Nicolas Gramlich
 * @since 19:51:34 - 03.04.2013
 */
public interface ServerConstants {

	// ===========================================================
	// Constants
	// ===========================================================

	/* Experiments: */
	public static final String SERVER_ENDPOINT_EXPERIMENTS = "experiments";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_UUID = "uuid";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_APP_ID = "app_id";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_APP_VERSION = "app_version";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_OS_VERSION = "os_version";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_MANUFACTURER = "device_manufacturer";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_NAME = "device_name";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_BRAND = "device_brand";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_PRODUCT = "device_product";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_MODEL = "device_model";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_BUILD = "device_build";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_LOCALE_COUNTRY = "device_locale_country";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_LOCALE_LANGUAGE = "device_locale_language";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_NETWORK_OPERATOR = "device_network_operator";
	public static final String SERVER_ENDPOINT_EXPERIMENTS_PARAMETER_DEVICE_SIM_OPERATOR = "device_sim_operator";

	/* GCM: */
	public static final String SERVER_ENDPOINT_GCM_REGISTER = "gcm/register";
	public static final String SERVER_ENDPOINT_GCM_REGISTER_PARAMETER_UUID = "uuid";
	public static final String SERVER_ENDPOINT_GCM_REGISTER_PARAMETER_APP_ID = "app_id";
	public static final String SERVER_ENDPOINT_GCM_REGISTER_PARAMETER_GCM_REGISTRATION_ID = "gcm_registration_id";

	public static final String SERVER_ENDPOINT_GCM_UNREGISTER = "gcm/unregister";
	public static final String SERVER_ENDPOINT_GCM_UNREGISTER_PARAMETER_UUID = "uuid";
	public static final String SERVER_ENDPOINT_GCM_UNREGISTER_PARAMETER_APP_ID = "app_id";
	public static final String SERVER_ENDPOINT_GCM_UNREGISTER_PARAMETER_GCM_REGISTRATION_ID = "gcm_registration_id";

	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
