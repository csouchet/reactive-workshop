package com.bonitasoft.reactiveworkshop.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.domain.artist.ArtistViews;
import com.bonitasoft.reactiveworkshop.domain.artist.ArtistViews.WithComments;
import com.bonitasoft.reactiveworkshop.domain.artist.ArtistViews.WithoutComments;
import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.fasterxml.jackson.annotation.JsonView;

import reactor.core.publisher.Mono;

@RestController
public class ArtistAPI {

	private static final String ENDPOINT_10_LAST_COMMENTS_BY_ARTIST = "/comments/{artistId}/last10";

	private final ArtistRepository artistRepository;

	private final WebClient webClient;

	public ArtistAPI(final ArtistRepository artistRepository, final WebClient webClient) {
		this.artistRepository = artistRepository;
		this.webClient = webClient;
	}

	@JsonView(ArtistViews.WithoutComments.class)
	@GetMapping("/artists")
	public List<Artist> findAll() {
		return artistRepository.findAll();
	}

	@JsonView(ArtistViews.WithoutComments.class)
	@GetMapping("/artist/{id}")
	public Artist findById(@PathVariable final String id) throws NotFoundException {
		return artistRepository.findById(id)
				.orElseThrow(NotFoundException::new);
	}

	/**
	 * Find an artist with its 10 last comments
	 *
	 * @param id
	 *            The identifier of the artist to find
	 * @return The artist with its 10 last comments
	 * @throws NotFoundException
	 *             Throws when the artist is not found
	 */
	@JsonView(ArtistViews.WithComments.class)
	@GetMapping("/artist/{id}/comments")
	public Mono<Artist> findByIdWith10LastComments(@PathVariable final String id) throws NotFoundException {
		final Mono<Artist> artistFlux = Mono.just(findById(id));
		final Mono<List<Comment>> commentsFlux = webClient.get()
				.uri(ENDPOINT_10_LAST_COMMENTS_BY_ARTIST, id)
				.retrieve()
				.bodyToFlux(Comment.class)
				.log()
				.collectList();

		// The zip method allows to easily combine the results of several Mono
		// with the great benefit that the execution of your zip method will
		// last as much as the longest Mono , not the sum of all the executions.
		return artistFlux.zipWith(commentsFlux)
				.map(tuple -> {
					final Artist artist = tuple.getT1();
					final List<Comment> comments = tuple.getT2();
					artist.setComments(comments);
					return artist;
				});
	}

}
