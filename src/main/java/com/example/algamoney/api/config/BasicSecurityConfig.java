package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *  1. Sobre a aula

Para a nossa classe de configuração, pouca coisa mudará. Mas vamos tentar rodar a aplicação, usando o mesmo código e chamar nossa API.

Se chamarmos a API, mesmo que passando a autenticação mostrada na aula, através do Postman, podemos notar o seguinte erro:
java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"

Isso é porque, antes da versão 5 do Spring, não era necessário explicitar qual PasswordEncoder utilizar para “decodificar” a senha de usuário. Por padrão o Spring nos fornecia uma classe chama NoOpPasswordEncoder, que na verdade não realizaria nenhuma “decodificação”.

Como estamos declarando a senha de usuário como texto puro (admin), não precisamos de decodificá-la, então precisamos declarar o NoOpPasswordEncoder, e temos duas formas de fazer isso:
Declarando o encoder direto na senha

Para este caso, podemos passar entre chaves o ID do Encoder que desejamos utilizar, como a senha não está criptografada, vamos utilizar o {noop}.

Caso nossa senha estivesse criptografa com BCrypt (por exemplo), poderíamos utilizar {bcrypt}

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth.inMemoryAuthentication()
      .withUser("admin").password("{noop}admin").roles("ROLE");
}
 * 
 * 
 * 
 */

@SuppressWarnings("deprecation")
@Profile("basic-security")
@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	//auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ROLE");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			.httpBasic()
			.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.csrf().disable();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
