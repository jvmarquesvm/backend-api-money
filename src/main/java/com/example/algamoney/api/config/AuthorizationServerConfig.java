package com.example.algamoney.api.config;

import java.util.Arrays;

import org.aspectj.weaver.ast.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


import com.example.algamoney.api.config.token.CustomTokenEnhancer;

/**
 * 
 * https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide
 * 
 * Aula 6.3 - Criando AccessToken
 *  A versão 2.5.1.RELEASE do spring-security-oauth2 traz a anotação @EnableAuthorizationServer
 *   como depreciada. Esta questão tem sido amplamente discutida na comunidade Spring desde 2019,
 *    quando foi anunciada a descontinuidade da parte do projeto que tratava do Authorization Server.
 *     Entretanto, devido à pressão de diversos desenvolvedores, em abril de 2020 a mantenedora do 
 *     Spring decidiu iniciar a remodelagem desta importante ferramenta. Como ainda não há uma 
 *     versão oficial adequada para produção, seguiremos utilizando a implementação já mostrada
 *      para o Authorization Server, com algumas alterações em relação ao que foi mostrado no vídeo. 
 * 
 */

@SuppressWarnings("deprecation")
@Configuration
@EnableAuthorizationServer
@Profile("oauth-security")
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	//Removido quando buscado o usuario do banco de dados
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	//configuração de aplicações que podem acessar os serviços e seus escopos
	// aplicação angular pode ler e escrever
	// aplicação mobile pode ler
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.         // é o componente angular
		    inMemory()   // Usuario está autorizando o cliente(aplicação) a acessar o Servidor de autorização e guardando em memória
			.withClient("angular") // nome do cliente ( aplicação ) 
				.secret(passwordEncoder.encode("@ngul@r0"))  // senha do cliente
				//.secret("$2a$10$tVK2HG2asFLcwQ.AFgBFA.P/s3mDglhN8V2Uh/OGt/D2gAZ6ON.FG")
				.scopes("read", "write") // escopo para limitar a acesso desse cliente ao recurso
				.authorizedGrantTypes("password", "refresh_token") // qual o tipo de grant type - password flow ( o cliente recebeu user/senha do usuário e 
				                                  // enviar para pegar access token
													// para conseguir configurar o refresh token, acrescentar ao grant_type refresh token
				.accessTokenValiditySeconds(20) // qual o tempo de validacão do access token
				.refreshTokenValiditySeconds(3600 * 24) //1 dia de duração
		//inserido para visualizar a utilização de scope do jwt token
		.and()
			.withClient("mobile") // nome do cliente ( aplicação ) 
			//.secret("wob$ile")  // senha do cliente
			//.secret( "$2a$10$l6vkOv3E9JVsIfJjfOqJ9OfsVpwkFvcMIc2QtoAW9Odh3o9y7FaZy")
			.secret(passwordEncoder.encode("wob$ile")) // Forma insegura
			.scopes("read") // escopo para limitar a acesso desse cliente ao recurso
			.authorizedGrantTypes("password", "refresh_token") // qual o tipo de grant type - password flow ( o cliente recebeu user/senha do usuário e 
		                                  // enviar para pegar access token
											// para conseguir configurar o refresh token, acrescentar ao grant_type refresh token
			.accessTokenValiditySeconds(20) // qual o tempo de validacão do access token
			.refreshTokenValiditySeconds(3600 * 24); //1 dia de duração - ligado com a instrução no authorization
		                                // reuseRefreshTokens se tiver pra true - essa duração será obedecida
	}
	
	//Armazenar o token na memória - tokenstore
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			 .authenticationManager(authenticationManager)  // para validar o token
			 .userDetailsService(userDetailsService)
			 .tokenEnhancer(tokenEnhancerChain)
		     //Removido por utilizar o TokenEnhancer
		     .accessTokenConverter(accessTokenConverter())  // inserido para converter o JWT Token
			 .tokenStore(tokenStore()) // local onde vai armazenar o token
		     .reuseRefreshTokens(false);  // configuração para que sempre que pedir, 
		                                    //  um novo refresh token será enviado(não reaproveitar)
		                            // Caso contrário, terá o valor da propriedade acima  refreshTokenValiditySeconds
	}

	// JWT Signature Verification
	// chave que valida o jwt token
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey("3032885ba9cd6621bcc4e7d6b6c35c2b");
        return accessTokenConverter;
    }

	//Versão com Access Token
	@Bean
	public TokenStore tokenStore() {
		//return new InMemoryTokenStore(); // local onde o access token será armazenado para o access token
		return new JwtTokenStore(accessTokenConverter()); // Versão com access token JWT
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}	
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
	    security.tokenKeyAccess("permitAll()")
        		.checkTokenAccess("isAuthenticated()");
	}
	
}
