package com.kerneldc.education.studentNotesService;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootConfig {

    @Value("${jersey.logging}")
    private boolean jerseyLogging;

    /**
	 * Configure Jersey as a filter 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean jerseyFilterRegistration() {
		System.out.println("jerseyLogging: "+jerseyLogging);
	    FilterRegistrationBean bean = new FilterRegistrationBean();
	    bean.setName("jerseyFilter");
	    bean.setFilter(new ServletContainer(new JerseyResourceConfig(jerseyLogging)));
	    bean.setOrder(1);
	    //bean.setUrlPatterns(Lists.newArrayList("/api/*"));
	    //bean.addInitParameter(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "/admin/.*");

	    return bean;
	}
}
