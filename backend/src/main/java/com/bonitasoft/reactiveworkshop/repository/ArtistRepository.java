package com.bonitasoft.reactiveworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {//extends MongoRepository<Artist, String> {
}
