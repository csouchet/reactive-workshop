package com.bonitasoft.reactiveworkshop.api;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.domain.comment.CommentsViews;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class GenreAPI {

	private static final String ENDPOINT_COMMENTS_STREAM = "/comments/stream";
	private static final String ENDPOINT_COMMENTS_LAST10 = "/comments/last10";

	private final ArtistRepository artistRepository;

	private final WebClient webClient;

	public GenreAPI(final ArtistRepository artistRepository, final WebClient webClient) {
		this.artistRepository = artistRepository;
		this.webClient = webClient;
	}

	@GetMapping("/genres")
	public List<String> findAll() {
		return artistRepository.findAll()
				.stream()
				.map(Artist::getGenre)
				.filter(g -> !g.isEmpty())
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	/**
	 * Get a stream of comments and associated artists filtered by a genre
	 *
	 * @param genre
	 *            The genre to filter the artists
	 * @return The stream of comments and associated artists
	 */
	@JsonView(CommentsViews.ByGenre.class)
	@GetMapping(path = "/genre/{genre}/comments/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Comment> getStreamOfCommentByGenre(@PathVariable final String genre) {
		final Map<String, Artist> filteredArtistsById = getFilteredArtistsById(genre);

		return webClient.get()
				.uri(ENDPOINT_COMMENTS_STREAM)
				.retrieve()
				.bodyToFlux(Comment.class)
				.filter(comment -> getArtist(filteredArtistsById, comment) != null)
				.map(comment -> addCommentToArtist(filteredArtistsById, comment))
				.log();
	}

	/**
	 * Find the 10 last comments and associated artists filtered by a genre
	 *
	 * @param genre
	 *            The genre to filter the artists
	 * @return The 10 last comments and associated artists
	 */
	@JsonView(CommentsViews.ByGenre.class)
	@GetMapping("/genre/{genre}/comments")
	public Flux<Comment> find10LastCommentsByGenre(@PathVariable final String genre) {
		final Map<String, Artist> filteredArtistsById = getFilteredArtistsById(genre);

		final Mono<Comment> fallback = Mono.error(NotFoundException::new);

		return webClient.get()
				.uri(ENDPOINT_COMMENTS_LAST10)
				.retrieve()
				.bodyToFlux(Comment.class)
				.filter(comment -> {
					final Artist artist = getArtist(filteredArtistsById, comment);
					return artist != null && !artist.getComments()
							.contains(comment);
				})
				.map(comment -> {
					return addCommentToArtist(filteredArtistsById, comment);
				})
				.log()
				.repeat()
				.take(10)
				.timeout(Duration.ofMinutes(2), fallback);
	}

	private Map<String, Artist> getFilteredArtistsById(final String genre) {
		final Map<String, Artist> mappingFilteredArtistById = new HashMap<>();

		@Cleanup
		final Stream<Artist> stream = artistRepository.findAllByGenre(genre)
				.stream();
		stream.forEach(artist -> mappingFilteredArtistById.put(artist.getId(), artist));

		return mappingFilteredArtistById;
	}

	private static Artist getArtist(final Map<String, Artist> filteredArtistsById, final Comment comment) {
		final String artistId = comment.getArtist()
				.getId();
		log.debug("comment = " + comment + ", artistId = " + artistId);
		return filteredArtistsById.get(artistId);
	}

	private static Comment addCommentToArtist(final Map<String, Artist> filteredArtistsById, final Comment comment) {
		final Artist artist = getArtist(filteredArtistsById, comment);
		artist.addComment(comment);
		return comment;
	}

}
