package br.com.teste.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.teste.libraryapi.exception.BusinessException;
import br.com.teste.libraryapi.model.entity.Book;
import br.com.teste.libraryapi.model.repository.BookRepository;
import br.com.teste.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void SetUp() {
		this.service = new BookServiceImpl(repository);
	}
	
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		//cenário
		Book book = createValidBook();
		Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);
		Mockito.when(repository.save(book)).thenReturn(Book.builder().id(1L).isbn("123")
				.author("Fulano").title("As Aventuras").build());
		
		//execução
		Book savedBook = service.save(book);
		
		//verificação
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getTitle()).isEqualTo("As Aventuras");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
	}
	
	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
	public void shoulNotSaveABookWithDuplicatedISBN() {
		//cenário
		Book book = createValidBook();
		Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);
		
		//execução
		Throwable exception = Assertions.catchThrowable( () -> service.save(book) );
		
		//verificação
		assertThat(exception)
				.isInstanceOfAny(BusinessException.class)
				.hasMessage("Isbn já cadastrado");	
		Mockito.verify(repository, Mockito.never()).save(book);
	}
	
	public Book createValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("As Aventuras").build();
	}
	
}

