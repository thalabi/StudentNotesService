package com.kerneldc.education.studentNotesService.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.kerneldc.education.studentNotesService.security.config.RestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	public WebSecurityConfig() {

	}
	
	@Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
        	.csrf().disable()
        	// return 401 Unauthorized when no credentials are supplied for a secured resource
        	.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
            // don't create session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers(/*"/", "/home",*/"/StudentNotesService/Security/authenticate").permitAll()
                .anyRequest().authenticated();
        // Custom JWT based security filter
//        httpSecurity
//                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

    }
	
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
}
