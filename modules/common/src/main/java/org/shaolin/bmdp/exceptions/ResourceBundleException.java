package org.shaolin.bmdp.exceptions;

public class ResourceBundleException extends BaseException {
	private static final long serialVersionUID = 1L;

	public ResourceBundleException(String msg) {
		super(msg);
	}

	public ResourceBundleException(String msg, Throwable e) {
		super(msg, e);
	}

	public ResourceBundleException(String msg, Object[] args) {
		super(msg, args);
	}
}