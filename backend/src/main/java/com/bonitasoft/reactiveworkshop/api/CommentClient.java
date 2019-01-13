/**
 *
 */
package com.bonitasoft.reactiveworkshop.api;

import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author SOUCHET Céline
 *
 */
@Component
public class CommentClient {

	private static final String ENDPOINT_10_LAST_COMMENTS_BY_ARTIST = "/comments/{artistId}/last10";
	private static final String ENDPOINT_COMMENTS_STREAM = "/comments/stream";
	private static final String ENDPOINT_COMMENTS_LAST10 = "/comments/last10";

	private final WebClient webClient;

	/**
	 * Default constructor
	 *
	 */
	public CommentClient() {
		super();
		webClient = WebClient.create("http://localhost:3004");
	}

	/**
	 * For unit tests
	 *
	 * @param webClient
	 *            To communicate with the external service
	 */
	CommentClient(final WebClient webClient) {
		super();
		this.webClient = webClient;
	}

	/**
	 * Find the 10 last comments of an artist
	 *
	 * @param artistId
	 *            The identifier of the artist
	 * @return The 10 last comments
	 */
	Flux<Comment> get10LastCommentsOfArtist(final String artistId) {
		return getComments(ENDPOINT_10_LAST_COMMENTS_BY_ARTIST, artistId);
	}

	/**
	 * Get a stream of comments
	 */
	Flux<Comment> getCommentsStream() {
		return getComments(ENDPOINT_COMMENTS_STREAM);
	}

	Flux<Comment> get10LastComments() {
		return getComments(ENDPOINT_COMMENTS_LAST10);
	}

	private Flux<Comment> getComments(final String uri, final Object... uriVariables) {
		final Function<ClientResponse, Mono<? extends Throwable>> fallbackFunction = clientResponse -> Mono.error(new NotFoundException(
				"Not possible to get comments from the external service" + clientResponse.statusCode()));
		return webClient.get()
				.uri(uri, uriVariables)
				.retrieve()
				.onStatus(HttpStatus::isError, fallbackFunction)
				.bodyToFlux(Comment.class)
				.log();
	}
}
