package com.kerneldc.education.studentNotesService.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kerneldc.education.studentNotesService.security.config.AuthenticationFilter;
import com.kerneldc.education.studentNotesService.security.config.CrossOriginResourceSharingFilter;
import com.kerneldc.education.studentNotesService.security.config.RestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	@Autowired
	private AuthenticationFilter authenticationFilter;
    @Autowired
    private CrossOriginResourceSharingFilter crossOriginResourceSharingFilter;
    
	@Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
        	.csrf().disable()
        	// return 401 Unauthorized when no credentials are supplied for a secured resource
        	.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
            // don't create session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers(/*"/", "/home",*/"/StudentNotesService/getVersion", "/StudentNotesService/Security/authenticate").permitAll()
                //.antMatchers("/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(crossOriginResourceSharingFilter, ChannelProcessingFilter.class)
            .addFilterBefore(authenticationFilter,
            	UsernamePasswordAuthenticationFilter.class)
            ;
    }
}
