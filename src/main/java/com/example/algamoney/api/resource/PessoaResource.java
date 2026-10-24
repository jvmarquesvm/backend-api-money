package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.PessoaService;

@RestController
@RequestMapping(value = "/pessoas")
public class PessoaResource {
	
	@Autowired
	private PessoaRepository repository;
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private PessoaService service;
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@PostMapping(produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		 Pessoa pessoaSave = this.repository.save(pessoa);
		 
		 //Implementado em um Event Listener
		 //URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}").buildAndExpand(pessoaSave.getCodigo()).toUri();
		 //response.setHeader("Location", uri.toASCIIString());
		 //this é o objeto que gerou o evento
		 publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSave.getCodigo()));
		 
		 //Devido não ter a URI - foi implementado de outra forma
		 //return ResponseEntity.created(uri).body(pessoaSave);
		 return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSave);
	}
	
	@GetMapping( value = "/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Pessoa> buscar(@PathVariable Long codigo) {
		Optional<Pessoa> optionalPessoa = this.repository.findById(codigo);
		return optionalPessoa.isPresent() ? ResponseEntity.ok(optionalPessoa.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping( value = "/{codigo}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and hasAuthority('SCOPE_write')")
	public void remover(@PathVariable Long codigo) {
		this.repository.deleteById(codigo);
	}
	
	@PutMapping( value = "/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody(required = true) Pessoa pessoa) {
		Pessoa pessoaSalva = this.service.atualizar(codigo, pessoa);
		return ResponseEntity.ok(pessoaSalva);
	}
	
	@PutMapping( value = "/{codigo}/ativo")
	@ResponseStatus( code = HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and hasAuthority('SCOPE_write')")
	public void atualizarPessoaAtivo(@PathVariable Long codigo, @RequestBody(required = true) Boolean ativo) {
		this.service.atualizarPessoaAtivo(codigo, ativo);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
	public Page<Pessoa> pesquisar(@RequestParam(required = false, defaultValue = "") String nome, Pageable pageable) {
		return pessoaRepository.findByNomeContaining(nome, pageable);
	}

}
