package com.bonitasoft.reactiveworkshop.api;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.domain.comment.CommentsViews;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.service.CommentService;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class GenreAPI {

	private final ArtistRepository artistRepository;

	private final CommentService commentService;

	public GenreAPI(final ArtistRepository artistRepository, final CommentService commentService) {
		this.artistRepository = artistRepository;
		this.commentService = commentService;
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
		return commentService.getCommentsStream()
				.transform(filterByGenreAndLinkCommentToArtist(genre));
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
		final Mono<Comment> fallback = Mono.error(NotFoundException::new);
		return commentService.get10LastComments()
				.transform(filterByGenreAndLinkCommentToArtist(genre))
				.repeat()
				.take(10)
				.timeout(Duration.ofMinutes(2), fallback);
	}

	private Function<Flux<Comment>, Flux<Comment>> filterByGenreAndLinkCommentToArtist(final String genre) {
	    final Map<String, Artist> artistsWithGenreById = getArtistsWithGenreById(genre);
		return f -> f.filter(comment ->  commentHasArtistOfGenreAndArtistDoesNotContainComment(artistsWithGenreById, comment))
				.map(comment -> updateComment(artistsWithGenreById, comment));
	}

	private static boolean commentHasArtistOfGenreAndArtistDoesNotContainComment(final Map<String, Artist> artistsById, Comment comment) {
        final Artist artist = getArtist(artistsById, comment);
        return artist != null && !artist.getComments().contains(comment);
    }

    private static Comment updateComment(final Map<String, Artist> artistsById, Comment comment) {
        final Artist artist = getArtist(artistsById, comment);
        artist.addComment(comment);
        return comment;
    }

	private Map<String, Artist> getArtistsWithGenreById(final String genre) {
		final Map<String, Artist> mappingFilteredArtistById = new HashMap<>();

		@Cleanup
		final Stream<Artist> stream = artistRepository.findAllByGenre(genre)
				.stream();
		stream.forEach(artist -> mappingFilteredArtistById.put(artist.getId(), artist));

		return mappingFilteredArtistById;
	}

	private static Artist getArtist(final Map<String, Artist> artistsById, final Comment comment) {
		final String artistId = comment.getArtist()
				.getId();
		if (log.isDebugEnabled()) {
			log.debug("comment = " + comment + ", artistId = " + artistId);
		}
		return artistsById.get(artistId);
	}

}
