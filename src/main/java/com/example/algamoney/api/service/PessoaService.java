package com.example.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository repository;
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		Optional<Pessoa> pessoaSalva = this.repository.findById(codigo);
		if(pessoaSalva.isPresent()) {
			BeanUtils.copyProperties(pessoa, pessoaSalva.get(), "codigo");
			return this.repository.save(pessoaSalva.get());
		} else {
			throw new EmptyResultDataAccessException(1);
		}
	}

	public void atualizarPessoaAtivo(Long codigo, Boolean ativo) {
		Optional<Pessoa> pessoaSalva = this.repository.findById(codigo);
		if(pessoaSalva.isPresent()) {
			pessoaSalva.get().setAtivo(ativo);
			this.repository.save(pessoaSalva.get());
		} else {
			throw new EmptyResultDataAccessException(1);
		}
	}
}
