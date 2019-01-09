/**
 *
 */
package com.bonitasoft.reactiveworkshop.api;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bonitasoft.reactiveworkshop.domain.comment.Comment;

import reactor.core.publisher.Flux;

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
	 * @param webClient
	 *            To communicate with the external service
	 */
	public CommentClient(final WebClient webClient) {
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
		return webClient.get()
				.uri(uri, uriVariables)
				.retrieve()
				.bodyToFlux(Comment.class)
				.log();
	}

}
