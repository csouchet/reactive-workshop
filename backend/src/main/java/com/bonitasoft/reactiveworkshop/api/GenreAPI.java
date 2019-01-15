package com.bonitasoft.reactiveworkshop.api;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
	public Flux<String> findAll() {
		return artistRepository.findAll()
				.map(Artist::getGenre)
				.filter(g -> !g.isEmpty())
				.distinct()
				.sort();
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
	    final Flux<Artist> artistsWithGenreById = artistRepository.findAllByGenre(genre);
        final Flux<Comment> commentsFlux =   commentService.getCommentsStream();
		return commentsFlux.zipWith(artistsWithGenreById).transform(filterAndLinkToArtist());
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
		final Flux<Artist> artistsWithGenreById = artistRepository.findAllByGenre(genre);
		final Flux<Comment> commentsFlux =  commentService.get10LastComments();
		final Mono<Comment> fallback = Mono.error(NotFoundException::new);
		
		
		// The zip method allows to easily combine the results of several Mono
        // with the great benefit that the execution of your zip method will
        // last as much as the longest Mono, not the sum of all the executions.
        return commentsFlux.zipWith(artistsWithGenreById).transform(filterAndLinkToArtist()).repeat().take(10)
                .timeout(Duration.ofMinutes(2), fallback);
	}

	private static Function<Flux<Tuple2< Comment, Artist>>, Flux<Comment>> filterAndLinkToArtist() {
		return f -> f.filter(tuple -> {
            final Artist artist = tuple.getT2();
            final Comment comment = tuple.getT1();
            
            String artistIdOfComment = comment.getArtist().getId(); 
            if (log.isDebugEnabled()) {
                log.debug("comment = " + comment + ", artistId = " + artistIdOfComment);
            }
            
            return artist.getId().equals(artistIdOfComment) && !artist.getComments()
                    .contains(comment);
        })
                .map(tuple -> updateComment(tuple.getT1(), tuple.getT2()));
	}

    private static Comment updateComment(final Comment comment, final Artist artist) {
        artist.addComment(comment);
        return comment;
    }


	private static Artist getArtist(final Map<String, Artist> artistsById, final Comment comment) {
		final String artistId = comment.getArtist()
				.getId();
		if (log.isDebugEnabled()) {
			log.debug("comment = {}, artistId = " + artistId, comment);
		}
		return artistsById.get(artistId);
	}

}
