package com.example.algamoney.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Aula 7.2
 * 2. Configuração da classe AlgamoneyApiProperty
 * É necessário adicionar a anotação @Component na classe AlgamoneyApiProperty. O bean injetado nas classes Token Resource, 
 * RefreshTokenPostProcessor e CorsFilter depende dessa configuração, que não é mais realizada automaticamente 
 * apenas com a anotação @ConfigurationProperties
 * 3. Alteração em AlgamoneyApiApplication
 * Não é mais necessário colocar a anotação EnableConfigurationProperties em AlgamoneyApiApplication, logo, 
 * a seguinte linha de código pode ser removida:
 * @EnableConfigurationProperties(value = AlgamoneyApiProperty.class)
 */

@Primary
@ConfigurationProperties("algamoney")
@Component
public class AlgamoneyApiProperty {
	
	private String originPermitida = "http://localhost:8080";
	
	public String getOriginPermitida() {
		return originPermitida;
	}
	
	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}
	
	//Pode-se gerar propriedades relacionando com temas
	private final Seguranca seguranca = new Seguranca();
	
	public Seguranca getSeguranca() {
		return seguranca;
	}

	public static class Seguranca {
		
		private boolean enableHttps = false;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}
		
	}

}
