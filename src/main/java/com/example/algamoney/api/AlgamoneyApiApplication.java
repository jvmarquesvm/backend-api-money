package com.example.algamoney.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(value = AlgamoneyApiProperty.class)
public class AlgamoneyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgamoneyApiApplication.class, args);
	}
	
	//Bean que trabalha junto com spring-boot-starter-validation
	/**
	 * A criação de um bean de configuração do MessageSource permite maior flexibilidade para configurá-lo. 
	 * É uma tarefa mais complexa, mas pode ser útil em casos específicos. 
	 * Esse bean pode ser implementado em uma classe avulsa ou diretamente na classe AlgamoneyApiApplication. 
	 * @return
	 */
    @Bean(name="messageSource")
    public ResourceBundleMessageSource bundleMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        
        return messageSource;
    }
    
    /*
     * Possibilidade de utilizar caracteres especiais
     * É possível adicionar caracteres especiais (acentos, cedilha) na mensagem armazenada no messages.properties. Não são necessárias configurações especiais para fazer isso.
	 * Caso você esteja recebendo no Postman as mensagens desconfiguradas, embora em seu arquivo mencionado acima você esteja conseguindo acrescentar caracteres especiais, você pode ajustar uma configuração específica para que seu arquivo
	 * No STS:
     * Clique em Window
     * Clique em Preferences
     * Digite na caixa de diálogo do lado superior esquerdo "content types"
     * Na lista que irá abrir na parte superior central da tela, abra a árvore chamada Text
     * Dentro da aba, clique em cima (não abra a árvore, apenas clique em cima) de Java properties Files
     * Na última caixa habilitada para escrever, chamada Default encoding, substitua o que estiver nela por UTF-8
     * Clique em Update
     * Clique em Apply and Close
     **/
    
}
