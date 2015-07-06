package org.shaolin.bmdp.runtime.cache;

public class UnlimitedCache<K, V> extends AbstractCache<K, V> {
	
	private static final long serialVersionUID = -8456115678990044172L;
	
	public UnlimitedCache(String name, boolean needSynchronize) {
		super(name, needSynchronize);
	}

	public int getMaxSize() {
		return -1;
	}

	public void setMaxSize(int maxSize) {
	}

	protected V getValue(V value) {
		return value;
	}

}
