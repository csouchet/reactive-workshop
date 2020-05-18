package com.bonitasoft.reactiveworkshop.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.service.CommentService;
import com.bonitasoft.reactiveworkshop.util.extension.TestStatusLoggerExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.publisher.TestPublisher;

/**
 * @author SOUCHET Céline
 *
 */
@ExtendWith({TestStatusLoggerExtension.class, SpringExtension.class})
@WebFluxTest(ArtistAPI.class)
public class ArtistAPITest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ArtistRepository artistRepository;

	@MockBean
	private CommentService commentService;

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.ArtistAPI#findByIdWith10LastComments(java.lang.String)}.
	 */
	@Test
	@DisplayName("findByIdWith10LastComments() should return the artist linked to its 10 last comments when the External Service returns the comments")
	public void findByIdWith10LastComments_should_return_artist_linked_to_10_last_comments_when_external_service_returns_comments() {
		// Given
		final String id = "2";
		final Artist artist = Artist.builder()
				.id(id)
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findById(id)).willReturn(Mono.just(artist));

		final Comment comment = new Comment(new Artist(), "user name", "message");
		given(commentService.get10LastCommentsOfArtist(id)).willReturn(Flux.just(comment));

		// When
		final Artist result = webTestClient.get()
				.uri("/artist/{id}/comments", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(Artist.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).isEqualTo(artist);
		assertThat(result.getComments()).containsExactly(comment);
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.ArtistAPI#findByIdWith10LastComments(java.lang.String)}.
	 */
	@Test
	@DisplayName("findByIdWith10LastComments() should return the artist with a empty list of comments when the External Service returns a empty list of comments")
	public void findByIdWith10LastComments_should_return_artist_with_empty_comments_when_external_service_returns_NO_comments() {
		// Given
		final String id = "2";
		final Artist artist = Artist.builder()
				.id(id)
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findById(id)).willReturn(Mono.just(artist));

		given(commentService.get10LastCommentsOfArtist(id)).willReturn(Flux.empty());

		// When
		final Artist result = webTestClient.get()
				.uri("/artist/{id}/comments", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(Artist.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).isEqualTo(artist);
		assertThat(result.getComments()).isEmpty();
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.ArtistAPI#findByIdWith10LastComments(java.lang.String)}.
	 */
	@Test
	@DisplayName("findByIdWith10LastComments() should return a 404 response when the External Service returns 4xx or 5xx status code")
	public void findByIdWith10LastComments_should_return_404_status_when_bodyToFlux_returns_NotFoundException() {
		// Given
		final String id = "2";
		final Artist artist = Artist.builder()
				.id(id)
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findById(id)).willReturn(Mono.just(artist));

		final Flux<Comment> flux = TestPublisher.<Comment>create()
				.error(new NotFoundException())
				.flux();
		given(commentService.get10LastCommentsOfArtist(id)).willReturn(flux);

		// When // Then
		webTestClient.get()
				.uri("/artist/{id}/comments", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.ArtistAPI#findByIdWith10LastComments(java.lang.String)}.
	 */
	@Test
	@DisplayName("findByIdWith10LastComments() should return a 404 response when there is no artist corresponding to the id")
	public void findByIdWith10LastComments_should_return_404_status_when_artist_NOT_exist() {
		// Given
		final String id = "2";
		given(artistRepository.findById(id)).willReturn(Mono.empty());

		final Comment comment = new Comment(new Artist(), "user name", "message");

		given(commentService.get10LastCommentsOfArtist(id)).willReturn(Flux.just(comment));

		// When // Then
		webTestClient.get()
				.uri("/artist/{id}/comments", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

}
