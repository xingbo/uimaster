package org.shaolin.bmdp.i18n;

import junit.framework.Assert;

import org.junit.Test;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;

public class ResourceUtilTest {

	@Test
	public void testGetResourceStringString() {
		
		Assert.assertNotNull(ResourceUtil.getResource("Common", "OKbtn"));
		Assert.assertNotNull(ResourceUtil.getResource("Common", "Cancelbtn"));
		
		Assert.assertNotNull(ResourceUtil.getResource("org_shaolin_bmdp_common_i18n", "a"));
		
		try {
			throw new I18NRuntimeException(ExceptionConstants.EBOS_ODMAPPER_056,
					new Object[]{"hello."});
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

}
