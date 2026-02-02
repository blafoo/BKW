package de.blafoo.growatt.feign;

import feign.InvocationContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.ResponseInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;

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


	/**
	 * The responses from Growatt have the Content-type 'text/html;' instead of 'application/json'. As a consequence we have to register a converter for 'text' to 'json'.
	 * Without converter feign would fail with:
	 * org.springframework.web.client.UnknownContentTypeException: Could not extract response: no suitable HttpMessageConverter found for response type [class de.blafoo.growatt.entity.ResultResponse] and content type [text/html;charset=UTF-8]'
	 */
    @Bean
	public JacksonJsonHttpMessageConverter customConverters() {
		JacksonJsonHttpMessageConverter additionalMapper = new JacksonJsonHttpMessageConverter();
		additionalMapper.setSupportedMediaTypes(List.of(MediaType.TEXT_HTML));
	    return additionalMapper;
	}

}
