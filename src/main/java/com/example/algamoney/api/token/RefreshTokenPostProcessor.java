package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/**
 * 
 * Controller Advice para a Classe OAuth2AccessToken(classe de retorno do /auth/oauth2
 * Ver em Spring Security OAuth2
 * 
 * OAuth2AccessToken - Tipo do dado que se quer interceptar
 * Toda vez que for criado um response body OAuth2AccessToken, o Advice irá interceptar
 * 
 * Pegar o refresh_token que vem no body e retorna no cookie
 */

@ControllerAdvice                                                  
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {
	
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;

	/**
	 * Só irá chamar o método beforeBodyWrite quando o método suporte retornar true
	 */
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// TODO Auto-generated method stub
		return returnType.getMethod().getName().equals("postAccessToken");
	}
	
	/**
	 * Só irá executar quando o supports returna true
	 */
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
											MediaType selectedContentType, 
											Class<? extends HttpMessageConverter<?>> selectedConverterType,
											ServerHttpRequest request, ServerHttpResponse response) {
		
		String valueRefreshToken = body.getRefreshToken().getValue();
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse(); 
		
		adicionarRefreshTokenCookie(valueRefreshToken, req, resp);
		
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		token.setRefreshToken(null);
		return body;
	}

	private void adicionarRefreshTokenCookie(String valueRefreshToken, 
			                                           HttpServletRequest req,
			                                                    HttpServletResponse resp) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", valueRefreshToken);
		refreshTokenCookie.setHttpOnly(true); //somente acessível via http
		refreshTokenCookie.setSecure(false); //em prd true.. em desenv false / funcionar somente https?
		//refreshTokenCookie.setSecure(algamoneyApiProperty.getSeguranca().isEnableHttps());
		System.out.println("https:" + algamoneyApiProperty.getSeguranca().isEnableHttps());
		
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		refreshTokenCookie.setMaxAge(259200); // 30 dias
		resp.addCookie(refreshTokenCookie);		
	}

}
