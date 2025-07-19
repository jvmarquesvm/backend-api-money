package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Categoria_;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;
	
	//Utilizar o Criteria do JPA
	@Override
	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteriaQuery = criteriaBuilder.createQuery(Lancamento.class);
		Root<Lancamento> from = criteriaQuery.from(Lancamento.class);
		
		Predicate[] pedicates = criarRestricoes(lancamentoFilter, criteriaBuilder, from);
		criteriaQuery.where(pedicates);
		
		TypedQuery<Lancamento> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder criteriaBuilder,
			Root<Lancamento> from) {
		List<Predicate> predicates = new ArrayList<>();
		
		if(lancamentoFilter.getDescricao() != null) {
			//predicates.add(criteriaBuilder.like(criteriaBuilder.lower(from.get("descricao")), "%" + lancamentoFilter.getDescricao() + "%"));
			predicates.add(criteriaBuilder.like(criteriaBuilder.lower(from.get(Lancamento_.descricao)), "%" + lancamentoFilter.getDescricao() + "%"));
		}
		
		if(!StringUtils.isEmpty( lancamentoFilter.getDataVencimentoDe() )) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo( from.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()) );	
		}
		
		if(!ObjectUtils.isEmpty( lancamentoFilter.getDataVencimentoAte()) ) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo( from.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()) );
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = this.manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteriaQuery = criteriaBuilder.createQuery(Lancamento.class);
		Root<Lancamento> from = criteriaQuery.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(lancamentoFilter, criteriaBuilder, from);
		criteriaQuery.where(predicates);
		
		TypedQuery<Lancamento> query = this.manager.createQuery(criteriaQuery);
		adicionarRestricoesDePaginacao(query, pageable);
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<Lancamento> from = criteriaQuery.from(Lancamento.class);
		
		Predicate[] criarRestricoesPredicates = criarRestricoes(lancamentoFilter, builder, from);
		criteriaQuery.where(criarRestricoesPredicates);
		criteriaQuery.select(builder.count( from ));
		return this.manager.createQuery(criteriaQuery).getSingleResult();
	}

	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteriaQuery = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> from = criteriaQuery.from(Lancamento.class);
		
		criteriaQuery.select(builder.construct(ResumoLancamento.class
				, from.get(Lancamento_.codigo), from.get(Lancamento_.descricao)
				, from.get(Lancamento_.dataVencimento), from.get(Lancamento_.dataPagamento)
				, from.get(Lancamento_.valor), from.get(Lancamento_.tipo)
				, from.get(Lancamento_.categoria).get(Categoria_.nome)
				, from.get(Lancamento_.pessoa).get(Pessoa_.nome)));
		
		Predicate[] criarRestricoesPredicates = criarRestricoes(lancamentoFilter, builder, from);
		criteriaQuery.where(criarRestricoesPredicates);
		
		TypedQuery<ResumoLancamento> query = this.manager.createQuery(criteriaQuery);
		adicionarRestricoesDePaginacao(query, pageable);
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

}
