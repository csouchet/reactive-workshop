/**
 *
 */
package com.bonitasoft.reactiveworkshop.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
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
import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.external.ExternalApplication;

/**
 * @author SOUCHET Céline
 *
 */
@ExtendWith({TestStatusLoggerExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {ReactiveWorkshopApplication.class})
public class GenreAPIIT {

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
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#getStreamOfCommentByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("getStreamOfCommentByGenre() should return a list of comments with their artist when the External Service returns the comments")
	public void getStreamOfCommentByGenre_should_return_comments_with_artist_when_external_service_returns_comments() {
		// Given
		final String genre = "Rock";

		// When
		final List<Comment> result = webTestClient.get()
				.uri("/genre/{genre}/comments/stream", genre)
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(Comment.class)
				.returnResult()
				.getResponseBody();

		// Then
		assertThat(result).hasSize(1);
		// final Comment resultComment = result.get(0);
		// assertThat(resultComment).isEqualToIgnoringGivenFields(comment,
		// "artist");
		// final Artist resultArtist = resultComment.getArtist();
		// assertThat(resultArtist).isEqualToIgnoringGivenFields(artist,
		// "genre", "comments");
		//
		// assertThat(result).hasFieldOrPropertyWithValue("id",
		// "c7174dc75237a0361780548a1af6872b")
		// .hasFieldOrPropertyWithValue("name", "Bush")
		// .hasFieldOrPropertyWithValue("genre", "Rock");
		// assertThat(result.getComments()).hasSize(10);
	}

	/**
	 * Test method for
	 * {@link com.bonitasoft.reactiveworkshop.api.GenreAPI#find10LastCommentsByGenre(java.lang.String)}.
	 */
	@Test
	@DisplayName("find10LastCommentsByGenre() should return 10 last comments with their artist when the External Service returns the comments")
	public void find10LastCommentsByGenre_should_return_10_last_comments_with_artist_when_external_service_returns_comments() {
		// Given
		final String genre = "Rock";

		// When
		final List<Comment> result = webTestClient.get()
				.uri("/genre/{genre}/comments", genre)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBodyList(Comment.class)
				.returnResult()
				.getResponseBody();

		// Then
		// assertThat(result).hasSize(10)
		// .containsOnlyOnce(comment)
		// .doesNotContain(commentWithWrongArtist);
		// final Artist resultArtist = result.get(0)
		// .getArtist();
		// assertThat(resultArtist).isEqualToIgnoringGivenFields(artist,
		// "genre", "comments");
	}

}
