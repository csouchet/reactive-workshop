/**
 *
 */
package com.bonitasoft.reactiveworkshop.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.util.extension.TestStatusLoggerExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

/**
 * @author SOUCHET Céline
 *
 */
@ExtendWith({TestStatusLoggerExtension.class, SpringExtension.class})
public class CommentServiceTest {

	@Mock
	private WebClient webClient;

	@InjectMocks
	private CommentService commentService;

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.service.CommentService#get10LastCommentsOfArtist(java.lang.String)}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	@DisplayName("get10LastCommentsOfArtist() should return the 10 last comments of an artist when the External Service returns the comments")
	public void get10LastCommentsOfArtist_should_return_10_last_comments_of_artist_when_external_service_returns_comments() {
		// Given
		final String artistId = "2";
		final Comment comment = new Comment(new Artist(), "user name", "message");
		final Flux<Comment> commentFlux = Flux.just(comment);

		final RequestHeadersUriSpec headersUriSpec = mock(RequestHeadersUriSpec.class);
		final RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
		final ResponseSpec responseSpec = mock(ResponseSpec.class);
		given(webClient.get()).willReturn(headersUriSpec);
		given(headersUriSpec.uri(anyString(), anyString())).willReturn(headersSpec);
		given(headersSpec.retrieve()).willReturn(responseSpec);
		given(responseSpec.onStatus(Mockito.<Predicate<HttpStatus>>any(), Mockito.<Function<ClientResponse, Mono<? extends Throwable>>>any())).willReturn(
				responseSpec);
		given(responseSpec.bodyToFlux(Comment.class)).willReturn(commentFlux);

		// When
		final Flux<Comment> result = commentService.get10LastCommentsOfArtist(artistId);

		// Then
		StepVerifier.create(result)
				.expectNext(comment)
				.verifyComplete();
		verify(headersUriSpec).uri("/comments/{artistId}/last10", artistId);
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.service.CommentService#get10LastCommentsOfArtist(java.lang.String)}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	@DisplayName("get10LastCommentsOfArtist() should return a 404 response when the External Service returns 4xx or 5xx status code")
	public void get10LastCommentsOfArtist_should_return_404_status_when_external_service_returns_error() {
		// Given
		final String artistId = "2";

		final Flux<Comment> flux = TestPublisher.<Comment>create()
				.error(new NotFoundException())
				.flux();

		final RequestHeadersUriSpec headersUriSpec = mock(RequestHeadersUriSpec.class);
		final RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
		final ResponseSpec responseSpec = mock(ResponseSpec.class);
		given(webClient.get()).willReturn(headersUriSpec);
		given(headersUriSpec.uri(anyString(), anyString())).willReturn(headersSpec);
		given(headersSpec.retrieve()).willReturn(responseSpec);
		given(responseSpec.onStatus(Mockito.<Predicate<HttpStatus>>any(), Mockito.<Function<ClientResponse, Mono<? extends Throwable>>>any())).willReturn(
				responseSpec);
		given(responseSpec.bodyToFlux(Comment.class)).willReturn(flux);

		// When
		final Flux<Comment> result = commentService.get10LastCommentsOfArtist(artistId);

		// Then
		StepVerifier.create(result)
				.expectError(NotFoundException.class)
				.verify();
		verify(headersUriSpec).uri("/comments/{artistId}/last10", artistId);
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.service.CommentService#getCommentsStream()}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	@DisplayName("getCommentsStream() should return a stream of comments when the External Service returns a stream of comments")
	public void getCommentsStream_should_return_stream_of_comments_when_external_service_returns_stream_of_comments() {
		// Given
		final Comment comment = new Comment(new Artist(), "user name", "message");
		final Flux<Comment> commentFlux = Flux.just(comment);

		final RequestHeadersUriSpec headersUriSpec = mock(RequestHeadersUriSpec.class);
		final RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
		final ResponseSpec responseSpec = mock(ResponseSpec.class);
		given(webClient.get()).willReturn(headersUriSpec);
		given(headersUriSpec.uri(anyString())).willReturn(headersSpec);
		given(headersSpec.retrieve()).willReturn(responseSpec);
		given(responseSpec.onStatus(Mockito.<Predicate<HttpStatus>>any(), Mockito.<Function<ClientResponse, Mono<? extends Throwable>>>any())).willReturn(
				responseSpec);
		given(responseSpec.bodyToFlux(Comment.class)).willReturn(commentFlux);

		// When
		final Flux<Comment> result = commentService.getCommentsStream();

		// Then
		StepVerifier.create(result)
				.expectNext(comment)
				.verifyComplete();
		verify(headersUriSpec).uri("/comments/stream");
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.service.CommentService#getCommentsStream()}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	@DisplayName("getCommentsStream() should return a 404 response when the External Service returns 4xx or 5xx status code")
	public void getCommentsStream_should_return_404_status_when_external_service_returns_error() {
		// Given
		final Flux<Comment> flux = TestPublisher.<Comment>create()
				.error(new NotFoundException())
				.flux();

		final RequestHeadersUriSpec headersUriSpec = mock(RequestHeadersUriSpec.class);
		final RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
		final ResponseSpec responseSpec = mock(ResponseSpec.class);
		given(webClient.get()).willReturn(headersUriSpec);
		given(headersUriSpec.uri(anyString())).willReturn(headersSpec);
		given(headersSpec.retrieve()).willReturn(responseSpec);
		given(responseSpec.onStatus(Mockito.<Predicate<HttpStatus>>any(), Mockito.<Function<ClientResponse, Mono<? extends Throwable>>>any())).willReturn(
				responseSpec);
		given(responseSpec.bodyToFlux(Comment.class)).willReturn(flux);

		// When
		final Flux<Comment> result = commentService.getCommentsStream();

		// Then
		StepVerifier.create(result)
				.expectError(NotFoundException.class)
				.verify();
		verify(headersUriSpec).uri("/comments/stream");
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.service.CommentService#get10LastComments()}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	@DisplayName("get10LastComments() should return 10 last comments when the External Service returns 10 last comments")
	public void get10LastComments_should_return_10_last_comments_when_external_service_returns_stream_of_comments() {
		// Given
		final Comment comment = new Comment(new Artist(), "user name", "message");
		final Flux<Comment> commentFlux = Flux.just(comment);

		final RequestHeadersUriSpec headersUriSpec = mock(RequestHeadersUriSpec.class);
		final RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
		final ResponseSpec responseSpec = mock(ResponseSpec.class);
		given(webClient.get()).willReturn(headersUriSpec);
		given(headersUriSpec.uri(anyString())).willReturn(headersSpec);
		given(headersSpec.retrieve()).willReturn(responseSpec);
		given(responseSpec.onStatus(Mockito.<Predicate<HttpStatus>>any(), Mockito.<Function<ClientResponse, Mono<? extends Throwable>>>any())).willReturn(
				responseSpec);
		given(responseSpec.bodyToFlux(Comment.class)).willReturn(commentFlux);

		// When
		final Flux<Comment> result = commentService.get10LastComments();

		// Then
		StepVerifier.create(result)
				.expectNext(comment)
				.verifyComplete();
		verify(headersUriSpec).uri("/comments/last10");
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.service.CommentService#get10LastComments()}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	@DisplayName("get10LastComments() should return a 404 response when the External Service returns 4xx or 5xx status code")
	public void get10LastComments_should_return_404_status_when_external_service_returns_error() {
		// Given
		final Flux<Comment> flux = TestPublisher.<Comment>create()
				.error(new NotFoundException())
				.flux();

		final RequestHeadersUriSpec headersUriSpec = mock(RequestHeadersUriSpec.class);
		final RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
		final ResponseSpec responseSpec = mock(ResponseSpec.class);
		given(webClient.get()).willReturn(headersUriSpec);
		given(headersUriSpec.uri(anyString())).willReturn(headersSpec);
		given(headersSpec.retrieve()).willReturn(responseSpec);
		given(responseSpec.onStatus(Mockito.<Predicate<HttpStatus>>any(), Mockito.<Function<ClientResponse, Mono<? extends Throwable>>>any())).willReturn(
				responseSpec);
		given(responseSpec.bodyToFlux(Comment.class)).willReturn(flux);

		// When
		final Flux<Comment> result = commentService.get10LastComments();

		// Then
		StepVerifier.create(result)
				.expectError(NotFoundException.class)
				.verify();
		verify(headersUriSpec).uri("/comments/last10");
	}

}
