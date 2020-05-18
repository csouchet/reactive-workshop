/**
 *
 */
package com.bonitasoft.reactiveworkshop.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
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
@WebFluxTest(GenreAPI.class)
public class GenreAPITest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ArtistRepository artistRepository;

	@MockBean
	private CommentService commentService;

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#getStreamOfCommentByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("getStreamOfCommentByGenre() should return a list of comments with their artist when the External Service returns the comments")
	public void getStreamOfCommentByGenre_should_return_comments_with_artist_when_external_service_returns_comments() {
		// Given
		final String genre = "genre";
		final Artist artist = Artist.builder()
				.id("id")
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findAllByGenre(genre)).willReturn(Flux.just(artist));

		final Comment comment = generateComment();
		given(commentService.getCommentsStream()).willReturn(Flux.just(comment));

		// When
		final List<Comment> result = webTestClient.get()
				.uri("/genre/{genre}/comments/stream", genre)
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(Comment.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).hasSize(1);
		final Comment resultComment = result.get(0);
		assertThat(resultComment).isEqualToIgnoringGivenFields(comment, "artist");
		final Artist resultArtist = resultComment.getArtist();
		assertThat(resultArtist).isEqualToIgnoringGivenFields(artist, "genre", "comments");
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#getStreamOfCommentByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("getStreamOfCommentByGenre() should return an empty list of comments when the External Service returns a comment with an artist with wrong genre")
	public void getStreamOfCommentByGenre_should_return_NO_comments_when_artist_of_comment_has_wrong_genre() {
		// Given
		final String genre = "genre";
		given(artistRepository.findAllByGenre(genre)).willReturn(Flux.empty());

		given(commentService.getCommentsStream()).willReturn(Flux.just(generateComment()));

		// When
		final List<Comment> result = webTestClient.get()
				.uri("/genre/{genre}/comments/stream", genre)
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(Comment.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).isEmpty();
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#getStreamOfCommentByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("getStreamOfCommentByGenre() should return an empty list of comments when the External Service returns a empty list of comments")
	public void getStreamOfCommentByGenre_should_return_NO_comments_when_external_service_returns_NO_comments() {
		// Given
		final String genre = "genre";
		final Artist artist = Artist.builder()
				.id("id")
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findAllByGenre(genre)).willReturn(Flux.just(artist));

		given(commentService.getCommentsStream()).willReturn(Flux.empty());

		// When
		final List<Comment> result = webTestClient.get()
				.uri("/genre/{genre}/comments/stream", genre)
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(Comment.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).isEmpty();
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#getStreamOfCommentByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("getStreamOfCommentByGenre() should return a 404 response when the External Service returns 4xx or 5xx status code")
	public void getStreamOfCommentByGenre_should_return_404_status_when_bodyToFlux_returns_NotFoundException() {
		// Given
		final String genre = "genre";
		final Artist artist = Artist.builder()
				.id("id")
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findAllByGenre(genre)).willReturn(Flux.just(artist));

		final Flux<Comment> flux = TestPublisher.<Comment>create()
				.error(new NotFoundException())
				.flux();
		given(commentService.getCommentsStream()).willReturn(flux);

		// When // Then
		webTestClient.get()
				.uri("/genre/{genre}/comments/stream", genre)
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange()
				.expectStatus()
				.is4xxClientError();
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#find10LastCommentsByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("find10LastCommentsByGenre() should return 10 last comments with their artist when the External Service returns the comments")
	public void find10LastCommentsByGenre_should_return_10_last_comments_with_artist_when_external_service_returns_comments() {
		// Given
		final String genre = "genre";
		final Artist artist = Artist.builder()
				.id("id")
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findAllByGenre(genre)).willReturn(Flux.just(artist));

		final Comment comment = generateComment();
		final Comment commentWithWrongArtist = generateComment();
		commentWithWrongArtist.getArtist()
				.setId("8487");
		final Mono<Comment> randomComment = Mono.fromSupplier(() -> {
			return generateComment();
		});
		final Flux<Comment> comments = Flux.just(comment, comment, commentWithWrongArtist)
				.mergeWith(randomComment.repeat(7));

		given(commentService.get10LastComments()).willReturn(comments)
				.willReturn(randomComment.repeat(10));

		// When
		final List<Comment> result = webTestClient.get()
				.uri("/genre/{genre}/comments", genre)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(Comment.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).hasSize(10)
				.containsOnlyOnce(comment)
				.doesNotContain(commentWithWrongArtist);
		final Artist resultArtist = result.get(0)
				.getArtist();
		assertThat(resultArtist).isEqualToIgnoringGivenFields(artist, "genre", "comments");
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#find10LastCommentsByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("find10LastCommentsByGenre() should return a 404 response when the External Service returns 4xx or 5xx status code")
	public void find10LastCommentsByGenre_should_return_404_status_when_bodyToFlux_returns_NotFoundException() {
		// Given
		final String genre = "genre";
		final Artist artist = Artist.builder()
				.id("id")
				.name("name")
				.genre("genre")
				.build();
		given(artistRepository.findAllByGenre(genre)).willReturn(Flux.just(artist));

		final Flux<Comment> flux = TestPublisher.<Comment>create()
				.error(new NotFoundException())
				.flux();
		given(commentService.get10LastComments()).willReturn(flux);

		// When // Then
		webTestClient.get()
				.uri("/genre/{genre}/comments", genre)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.is4xxClientError();
	}

	private static Comment generateComment() {
		final Artist deserializedArtist = Artist.builder()
				.id("id")
				.build();

		final String username = "user name";
		final String message = "plop " + RandomStringUtils.random(10, true, true);
		return new Comment(deserializedArtist, username, message);
	}

}
