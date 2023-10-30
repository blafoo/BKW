package de.blafoo.bkw.growatt.feign;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import feign.InvocationContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.ResponseInterceptor;

@Configuration
public class GrowattFeignInterceptor implements RequestInterceptor, ResponseInterceptor {
	
	@Autowired
	private GrowattFeignCookieJar cookieJar;

	@Override
	public void apply(RequestTemplate template) {
		template.header(HttpHeaders.COOKIE, StringUtils.join(cookieJar.getCookies(), "; "));
	}
	
	@Override
	public Object aroundDecode(InvocationContext invocationContext) throws IOException {
		feign.Response response = invocationContext.response();
		var cookies = response.headers().get(HttpHeaders.SET_COOKIE);
		cookieJar.addCookies(cookies);

        return invocationContext.proceed();
	}

    @Bean
    HttpMessageConverters customConverters() {
		var additionalMapper = new MappingJackson2HttpMessageConverter();
		additionalMapper.setSupportedMediaTypes(List.of(MediaType.TEXT_HTML));
	    return new HttpMessageConverters(additionalMapper);
	}

}
