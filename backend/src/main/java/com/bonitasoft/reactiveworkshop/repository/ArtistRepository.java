package com.bonitasoft.reactiveworkshop.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;

import reactor.core.publisher.Flux;

@Repository
public interface ArtistRepository extends ReactiveMongoRepository<Artist, String> {
	// extends JpaRepository<Artist, String> {

	/**
	 * Get all artists corresponding to the specified genre
	 *
	 * @param genre
	 *            The genre to filter
	 * @return The artists corresponding to the specified genre
	 */
	Flux<Artist> findAllByGenre(String genre);

}
