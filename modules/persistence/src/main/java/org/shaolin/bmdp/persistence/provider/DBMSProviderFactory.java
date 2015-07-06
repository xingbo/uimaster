package org.shaolin.bmdp.persistence.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBMSProviderFactory {

	public static final String ORACLE = "ORACLE";
	
	public static final String DB2 = "DB2";
	
	public static final String MYSQL = "MYSQL";
	
	private static final Map<String, IDBMSProvider> map 
		= new HashMap<String, IDBMSProvider>(3);

	static {
		map.put(ORACLE, new OracleProvider());
		map.put(DB2, new DB2Provider());
		map.put(MYSQL, new MySQLProvider());
	}

	private static String providerId = MYSQL;
	
	private DBMSProviderFactory() {
	}
	
	public static void setProviderId(String providerId_) {
		providerId = providerId_;
	}
	
	public static String getProviderId() {
		return providerId;
	}

	public static IDBMSProvider getProvider() {
		return getProvider(providerId);
	}
	
	public static IDBMSProvider getProvider(String id) {
		IDBMSProvider provider = (IDBMSProvider) map.get(id.toUpperCase());
		if (provider == null) {
			throw new IllegalArgumentException("Unsupported database:" + id);
		}
		return provider;
	}

	public static List<IDBMSProvider> getAllProviders() {
		return new ArrayList<IDBMSProvider>(map.values());
	}

}
