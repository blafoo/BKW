package de.blafoo.growatt.feign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GrowattFeignCookieJar {
	
	Map<String, String> cookies = new HashMap<>();

	public String getCookie(String cookie, String defaultValue) {
		return cookies.getOrDefault(cookie, defaultValue);
	}
	
	public Collection<String> getCookies() {
		List<String> result = new ArrayList<>();
		cookies.forEach((k,v) -> result.add(String.format("%s=%s", k, v)));
		return result;
	}
	
	// set-cookie: SERVERID=140e7034f8d2f6a146150a095a172bad|1687098527|1687098527;Path=/	
	// set-cookie: isloginValidCode=""; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/
	public void addCookies(Collection<String> cookies) {
		for (String cookie : cookies) {
			String[] parts = cookie.split(";");
			if (parts.length >= 1) {
				parts = parts[0].split("=");
				if (parts.length == 2 && StringUtils.isNotBlank(parts[1]) && !"\"\"".equals(parts[1])) {
					this.cookies.put(parts[0], parts[1]);
				}
			}
		}
	}

}
