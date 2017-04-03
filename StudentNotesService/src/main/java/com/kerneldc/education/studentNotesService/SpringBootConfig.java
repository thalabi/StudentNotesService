package com.kerneldc.education.studentNotesService;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	    bean.setFilter(new ServletContainer(new JerseyResourceConfig()));
	    bean.setOrder(1);
	    //bean.setUrlPatterns(Lists.newArrayList("/api/*"));
	    //bean.addInitParameter(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "/admin/.*");

	    return bean;
	}
}
