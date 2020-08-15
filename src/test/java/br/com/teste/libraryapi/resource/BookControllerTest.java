package br.com.teste.libraryapi.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.teste.libraryapi.api.dto.BookDTO;
import br.com.teste.libraryapi.exception.BusinessException;
import br.com.teste.libraryapi.model.entity.Book;
import br.com.teste.libraryapi.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService service;
	
	@Test
	@DisplayName ("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception{
		BookDTO dto = createNewBook();
		Book savedBook = Book.builder().id(1L).author("Artur").title("As aventuras")
				.isbn("001").build();
		
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);				
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);		
							
		mvc.perform(request)
				.andExpect( status().isCreated() )
				.andExpect( jsonPath("id").value(1L) )
				.andExpect( jsonPath("title").value(dto.getTitle()) )
				.andExpect( jsonPath("author").value(dto.getAuthor()) )
				.andExpect( jsonPath("isbn").value(dto.getIsbn()) );
		
	}

	
	@Test
	@DisplayName ("Deve lançar erro de validação quando não houver dados suficientes para criação do livro")
	public void createInvalidBookTest() throws Exception {
	
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);		
							
		mvc.perform(request)
				.andExpect( status().isBadRequest() )
				.andExpect( jsonPath("errors", hasSize(3)) );
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro")
	public void createBookWithDuplicatedIsbn() throws Exception {
		
		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		String mensagemErro = "Isbn Já cadastrado";
		BDDMockito.given(service.save(Mockito.any(Book.class)))
				.willThrow(new BusinessException(mensagemErro));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);		
							
		mvc.perform(request)
				.andExpect( status().isBadRequest() )
				.andExpect( jsonPath("errors", hasSize(1)) )
				.andExpect( jsonPath("errors[0]").value(mensagemErro) );						
	}
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
	}
}
