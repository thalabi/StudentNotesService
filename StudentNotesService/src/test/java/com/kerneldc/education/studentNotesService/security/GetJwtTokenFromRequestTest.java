package com.kerneldc.education.studentNotesService.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class GetJwtTokenFromRequestTest {

	@Test
	public void testGetJwtToken () {
		String authenticationHeader = " bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb2huRG9lIiwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6ImF1dGhvcml0eTEifSx7ImF1dGhvcml0eSI6ImF1dGhvcml0eTIifSx7ImF1dGhvcml0eSI6ImF1dGhvcml0eTMifV19.0n9X5iPk79ZZ9v3U3PgsF3RwW-AtfGVVrwJ6-DN0lY0AuIQvmSofiK8qeJz_Pa8X5-B3HZ3k8a3tzDPLfv3dqA";
    	Pattern p = Pattern.compile("^\\s*Bearer\\s+(.*)$", Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(authenticationHeader);
    	Assert.assertTrue("Group count is not 1", m.groupCount() == 1);
    	Assert.assertTrue("Does not match", m.matches());
    	if (m.matches() && m.groupCount() == 1) {
    		Assert.assertNotNull("m.group(1): "+m.group(1), m.group(1));
    		System.out.println(m.group(1));
    	} else {
    		Assert.fail();
    	}
	}
}
