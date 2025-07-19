package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController
@RequestMapping( value = "/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository repository;
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@CrossOrigin(maxAge = 10, origins = {"http://127.0.0.1:5500"})
	@GetMapping
	//Scopo do usuario logado e scopo do client(aplicacao que está chamando este serviço)
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
	public List<Categoria> listar(){
		return this.repository.findAll();
	}
	
	/**
	 * 1º versao
	 * @param categoria
	 * Criar Categoria
	 */
	/*@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public void criar(@RequestBody Categoria categoria) {
		this.repository.save(categoria);
	}*/
	
	/**
	 * 2º versao
	 * @param categoria
	 * Criar Categoria
	 */
	/*@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public void criar(@RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = this.repository.save(categoria);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}").buildAndExpand(categoriaSalva.getCodigo()).toUri();
		response.setHeader("Location", uri.toASCIIString());
	}*/
	
	/**
	 * 3º versao
	 * @param categoria
	 * Criar Categoria
	 */
	/*@PostMapping
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria) {
		Categoria categoriaSalva = this.repository.save(categoria);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}").buildAndExpand(categoriaSalva.getCodigo()).toUri();
		return ResponseEntity.created(uri).body(categoriaSalva);
	}*/
	
	/**
	 * 4º versao
	 * @param categoria
	 * Criar Categoria
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria,  HttpServletResponse response) {
		Categoria categoriaSalva = this.repository.save(categoria);
		//Implementado em um Event Listener
		//URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}").buildAndExpand(categoriaSalva.getCodigo()).toUri();
		//response.setHeader("Location", uri.toASCIIString());
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		
		//Devido não ter a URI - foi implementado de outra forma
		//return ResponseEntity.created(uri).body(categoriaSalva);
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	/**
	 * 1º versao
	 * @param codigo
	 * Buscar Categoria
	 */
	/*@GetMapping( value = "/{codigo}")
	public Categoria buscaPeloCodigo(@PathVariable Long codigo) {
		return this.repository.findById(codigo).orElse(null);
	}*/
	
	/**
	 * 2º versao
	 * @param codigo
	 * Buscar Categoria
	 */
	/*@GetMapping( value = "/{codigo}")
	public ResponseEntity<Categoria> buscaPeloCodigo(@PathVariable Long codigo) {
		Optional<Categoria> optCategoria =  this.repository.findById(codigo);
		return optCategoria.isPresent() ? ResponseEntity.ok(optCategoria.get()) : ResponseEntity.notFound().build() ; 
	}*/
	
	/**
	 * 3º versao
	 * @param codigo
	 * Buscar Categoria
	 */
	@GetMapping( value = "/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
	public ResponseEntity<Categoria> buscaPeloCodigo(@PathVariable Long codigo) {
		return this.repository.findById(codigo).map(categoria -> ResponseEntity.ok(categoria)).orElse(ResponseEntity.notFound().build());
	}
	
}
