package de.blafoo.growatt.feign;

import feign.InvocationContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.ResponseInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Collection;
import java.util.List;

@Configuration
public class GrowattFeignInterceptor implements RequestInterceptor, ResponseInterceptor {
	
	@Autowired
	private GrowattFeignCookieJar cookieJar;

	@Override
	public void apply(RequestTemplate template) {
		template.header(HttpHeaders.COOKIE, StringUtils.join(cookieJar.getCookies(), "; "));
	}
	
	@Override
	public Object intercept(InvocationContext invocationContext, Chain chain) throws Exception {
        try (feign.Response response = invocationContext.response()) {
			Collection<String> cookies = response.headers().get(HttpHeaders.SET_COOKIE);
			if (cookies != null)
				cookieJar.addCookies(cookies);

			return invocationContext.proceed();
        }
    }

    @Bean
    HttpMessageConverters customConverters() {
		var additionalMapper = new MappingJackson2HttpMessageConverter();
		additionalMapper.setSupportedMediaTypes(List.of(MediaType.TEXT_HTML));
	    return new HttpMessageConverters(additionalMapper);
	}

}
