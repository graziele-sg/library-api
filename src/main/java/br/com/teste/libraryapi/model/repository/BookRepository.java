package br.com.teste.libraryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.teste.libraryapi.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

	boolean existsByIsbn(String isbn);
}
