package br.com.teste.libraryapi.service.impl;

import org.springframework.stereotype.Service;

import br.com.teste.libraryapi.exception.BusinessException;
import br.com.teste.libraryapi.model.entity.Book;
import br.com.teste.libraryapi.model.repository.BookRepository;
import br.com.teste.libraryapi.service.BookService;

@Service
public class BookServiceImpl implements BookService{

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		if ( repository.existsByIsbn(book.getIsbn()) ) {
			throw new BusinessException ("Isbn já cadastrado");
		}
		return repository.save(book);
	}

}
