package com.kerneldc.education.studentNotesService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SpringBootConfig {

	/**
	 * Configure Jersey as a filter 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean jerseyFilterRegistration() {
	    FilterRegistrationBean bean = new FilterRegistrationBean();
	    bean.setName("jerseyFilter");
	    ResourceConfig rc = new JerseyResourceConfig();
	    bean.setFilter(new ServletContainer(rc));
	    bean.setOrder(Ordered.LOWEST_PRECEDENCE);
	    //bean.setUrlPatterns(Lists.newArrayList("/api/*"));
	    //bean.addInitParameter(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "/admin/.*");

	    return bean;
	}
	/**
	 * Allow cross origin requests
	 * @return
	 */
	@Bean
	public FilterRegistrationBean corsFilter() {
		
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		
		bean.setOrder(Ordered.LOWEST_PRECEDENCE);
		return bean;
	}
		
}
