package com.example.algamoney.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {
	@Autowired
	private LancamentoRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;


	public Lancamento salvar(@Valid Lancamento lancamento) {
		Optional<Pessoa> optionalPessoa = this.pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if(!optionalPessoa.isPresent() || optionalPessoa.get().isAtivo() ) {
			throw new PessoaInexistenteOuInativaException();
		}
		return this.repository.save(lancamento);
	}
	
}
