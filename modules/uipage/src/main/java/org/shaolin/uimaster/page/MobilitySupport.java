package org.shaolin.uimaster.page;

public class MobilitySupport {

	public static final String MOB_PAGE_SUFFIX = "_mob";
	
	public static boolean isMobileRequest(String userAgent) {
		if (userAgent.indexOf("Android") != -1
		 || userAgent.indexOf("webOS") != -1
		 || userAgent.indexOf("iPhone") != -1
		 || userAgent.indexOf("iPad") != -1
		 || userAgent.indexOf("iPod") != -1
		 || userAgent.indexOf("BlackBerry") != -1
		 || userAgent.indexOf("opera mobi") != -1
		 || userAgent.indexOf("opera mini") != -1
		 || userAgent.indexOf("symbian") != -1
		 || userAgent.indexOf("Windows Phone") != -1) {
			return true;
		}
		return false;
	}
	
}
