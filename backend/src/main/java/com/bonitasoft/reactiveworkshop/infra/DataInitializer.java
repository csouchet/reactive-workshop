package com.bonitasoft.reactiveworkshop.infra;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.opencsv.CSVReader;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

	private final ArtistRepository repository;

	public DataInitializer(final ArtistRepository repository) {
		this.repository = repository;
	}

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		repository.deleteAll();

		final List<Artist> allArtists = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(DataInitializer.class.getResourceAsStream("/artists_genre.csv")))) {
			String[] line;
			while ((line = reader.readNext()) != null) {
				// name,facebook,twitter,website,genre,mtv
				final String name = line[0].trim();
				final Artist artist = Artist.builder()
						.id(md5(name))
						.name(name)
						.genre(line[4].trim())
						.build();
				allArtists.add(artist);

			}
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		final Set<String> artistIds = new HashSet<>();

		allArtists.stream()
				.filter(a -> artistIds.add(a.getId()))
				.forEach(repository::save);
		// Flux.fromIterable(allArtists)
		// .distinct(Artist::getId)
		// .subscribe(repository::save);
		// .subscribe(repository::insert);
		// .deleteAll()
		// .thenMany(
		// Flux
		// .just("A", "B", "C", "D")
		// .map(name -> new Profile(UUID.randomUUID().toString(), name +
		// "@email.com"))
		// .flatMap(repository::save)
		// )
		// .thenMany(repository.findAll())
		// .subscribe(profile -> log.info("saving " + profile.toString()));
	}

	private static String md5(final String name) {
		try {
			return md5DigestAsHex(name.getBytes("UTF-8"));
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to parse artists file", e);
		}
	}
}
