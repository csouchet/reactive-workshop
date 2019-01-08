package com.bonitasoft.reactiveworkshop.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {

	// extends MongoRepository<Artist, String> {

	/**
	 * Get all artists corresponding to the specified genre
	 *
	 * @param genre
	 *            The genre to filter
	 * @return The artists corresponding to the specified genre
	 */
	Collection<Artist> findAllByGenre(String genre);

}
