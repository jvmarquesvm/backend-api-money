package com.example.algamoney.api.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Arigo sobre OAuth2
 * https://www.digitalocean.com/community/tutorials/uma-introducao-ao-oauth-2-pt
 * Será implementado o Password Flow
 * 
 * Habilitar a segurança na aplicação @EnableWebSecurity
 * Habilitar segurança nos métodos @EnableGlobalMethodSecurity
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //habilitar seguranças nos métodos adicionando o escopo
@Profile("oauth-security")
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

	//Removido quando inserido o segurança de roles nos métodos - causou erro de referencia circular
    //private final AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
//	@Autowired
//	private PasswordEncoder passwordEncoder;

	//Removido quando inserido o segurança de roles nos métodos - causou erro de referencia circular
    //ResourceServerConfig(AuthenticationManager authenticationManager) {
    //    this.authenticationManager = authenticationManager;
    //}
	
	/**
	 * Fazer Autenticação(usuario/senha) e Autorização(roles)
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
	  
		//Removido para utilizar o banco de dados - Autenticacao em Memória
		//auth.inMemoryAuthentication()
	    //  .withUser("admin").password("admin").roles("ROLE");
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEnconder());
	}
	
	@Bean
	public PasswordEncoder passwordEnconder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Fazer a configuração autorização das requisições
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		     	//após a configuração de segurança por role nos métodos, essa deixou de funcionar, foi sobrescrita
				.antMatchers("/categorias").permitAll() //somente para categorias, qualquer um pode acessar
				.anyRequest().authenticated() // para qualquer outra requisição precisa estar autenticado
			.and()
				//.httpBasic() //tipo de autenticação será o basic
			//.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //desabilitar a criação de sessão da aplicação / não mantem estado 
			.and()
				.csrf().disable() // desabilitar o Cross-Site-Request-forgery
			//.oauth2ResourceServer().opaqueToken();// removido quando busca o usuario do banco
				.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
	}
	
	// O Authentication Manager deixou de ser provido automaticamente pelo Spring. 
	// Sendo assim, é necessário criar um bean de forma explícita 
	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {        
	    return super.authenticationManager();
	}
	
	/**
	 * É necessário configurar a codificação das senhas de cliente de forma explícita. 
	 * Para isso, configuraremos um bean que fará o gerenciamento deste processo. 
	 * Inicialmente será utilizada uma implementação com NoOpPasswordEncoder, 
	 * que encontra-se depreciado (devido a não codificar realmente a senha). 
	 * Em aulas posteriores isso será alterado. 
	 * @return
	 */
	//Removido quando passou o usuario para o banco de dados
	//@Bean
	//public PasswordEncoder passwordEncoder() {
	//    return NoOpPasswordEncoder.getInstance();
	//}
	
	/**
	 * A configuração do JWT no Spring Security 5.5 exige a criação de um bean para a decodificação 
	 * do mesmo, o que pode ser feito com o seguinte código. 
	 * @return
	 */
	//inserido quando buscado o usuário do banco de dados, mas nao utilizado
	//chave que valida o jwt token
	
	/**
	 *  Um ponto importante desta configuração é que a secretKeyString deve possuir no mínimo 
	 *  256 bits (32 caracteres) para que seja aceita. Caso seja utilizada uma chave de tamanho
	 *   menor será lançado um erro 401 (Não autorizado) para as requisições utilizando este token

	Observação: lembre-se de alterar a configuração da secretKey no método accessTokenConverter() 
	presente no Authorization Server para coincidir com a configurada no Resource Server 
	 * @return
	 */
	@Bean
	public JwtDecoder jwtDecoder() {
	    var secretKey = new SecretKeySpec("3032885ba9cd6621bcc4e7d6b6c35c2b".getBytes(), "HmacSHA256");
	    return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}
	
	// Método necessário para fazer funcionar o Refresh Token
//	@Bean
//	@Override
//	public UserDetailsService userDetailsServiceBean() throws Exception {
//	   return super.userDetailsServiceBean();
//	} 
	
	/**
	 * É necessário implementar um conversor de autenticação para tratar as permissões presentes no 
	 * token JWT. 
	 * Esta configuração será útil quando o sistema de permissões for implementado. 
	 * @return
	 */
	
	//inserido quando buscado o usuário do banco de dados
	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
			List<String> authorities = jwt.getClaimAsStringList("authorities");
			
			if (authorities == null) {
				authorities = Collections.emptyList();
			}
			
			JwtGrantedAuthoritiesConverter scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
			Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt);
			
			grantedAuthorities.addAll(authorities.stream()
					                       .map(SimpleGrantedAuthority::new)
							                     .collect(Collectors.toList()));
			return grantedAuthorities;
		});
		return jwtAuthenticationConverter;
	}
	
	//Configuração para autorização de segurança nos métodos
	// //habilitar seguranças nos métodos adicionando o escopo
	//@Bean
	public MethodSecurityExpressionHandler createExpressHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}

}
