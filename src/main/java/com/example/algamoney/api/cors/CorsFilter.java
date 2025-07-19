package com.example.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
	
	//Removida para ser pego atraves de profile properties
	//private String originPermitida = "http://127.0.0.1:5500"; //configurar para diferentes ambientes
	
	@Autowired
	private AlgamoneyApiProperty algamoneyApiPropery;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		System.out.println("Origem:" + algamoneyApiPropery.getOriginPermitida());
		
		resp.setHeader("Access-Control-Allow-Origin", algamoneyApiPropery.getOriginPermitida());
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		
		if( "OPTIONS".equals(req.getMethod()) && algamoneyApiPropery.getOriginPermitida().equals(req.getHeader("Origin"))){
			resp.setHeader("Access-control-Allow-Methods", "post, get, put, delete, options");
			resp.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			resp.setHeader("Access-Control-Max-Age", "3600"); //1h
			
			resp.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, resp);
		}
		
	}

}
