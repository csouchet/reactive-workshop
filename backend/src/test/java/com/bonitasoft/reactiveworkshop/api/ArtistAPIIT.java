package com.bonitasoft.reactiveworkshop.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.csouchet.test.extension.TestStatusLoggerExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bonitasoft.reactiveworkshop.ReactiveWorkshopApplication;
import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.external.ExternalApplication;

/**
 * @author SOUCHET Céline
 *
 */
@ExtendWith({TestStatusLoggerExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {ReactiveWorkshopApplication.class})
public class ArtistAPIIT {

	@LocalServerPort
	private int port;

	@Autowired
	private WebTestClient webTestClient;

	@BeforeAll
	public static void beforeAll() {
		final Map<String, Object> properties = new HashMap<>();
		properties.put("server.port", "3004");

		final SpringApplication externalApplication = new SpringApplicationBuilder(ExternalApplication.class).properties(properties)
				.build();
		externalApplication.run();
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.ArtistAPI#findByIdWith10LastComments(java.lang.String)}.
	 */
	@Test
	@DisplayName("findByIdWith10LastComments() should return the artist linked to its 10 last comments when the External Service returns the comments")
	public void findByIdWith10LastComments_should_return_artist_linked_to_10_last_comments_when_external_service_returns_comments() {
		// Given
		final String id = "c7174dc75237a0361780548a1af6872b";

		// When
		final Artist result = webTestClient.get()
				.uri("/artist/{id}/comments", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(Artist.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).hasFieldOrPropertyWithValue("id", "c7174dc75237a0361780548a1af6872b")
				.hasFieldOrPropertyWithValue("name", "Bush")
				.hasFieldOrPropertyWithValue("genre", "Rock");
		assertThat(result.getComments()).hasSize(10);
	}

}
