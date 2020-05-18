/**
 *
 */
package com.bonitasoft.reactiveworkshop.domain.artist;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.domain.comment.CommentsViews;
import com.bonitasoft.reactiveworkshop.util.extension.TestStatusLoggerExtension;

/**
 * @author SOUCHET Céline
 *
 */
@ExtendWith({TestStatusLoggerExtension.class, SpringExtension.class})
@JsonTest
public class ArtistTest {

	@Autowired
	private JacksonTester<Artist> jacksonTester;

	@Test
	public void serialization_should_return_wrapped_json_with_comments_when_WithComments_is_used_as_json_view_and_content_comments() throws IOException {
		// Given
		final Comment comment = new Comment();
		comment.setMessage("message");
		comment.setUserName("user name");

		final Artist artist = new Artist();
		artist.setId("artist id");
		artist.setName("artist name");
		artist.setGenre("genre");
		artist.addComment(comment);

		// When
		final JsonContent<Artist> result = jacksonTester.forView(ArtistViews.WithComments.class)
				.write(artist);

		// Then
		assertThat(result).isEqualToJson("{\"artistId\": \"artist id\"," + "\"artistName\": \"artist name\"," + "\"genre\":\"genre\"}," + "\"comments\":["
				+ "\"comment\":\"message\"," + "\"userName\":\"user name\"" + "]}");
	}

	@Test
	public void serialization_should_return_wrapped_json_with_empty_comments_when_WithComments_is_used_as_json_view_and_not_content_comments()
			throws IOException {
		// Given
		final Artist artist = new Artist();
		artist.setId("artist id");
		artist.setName("artist name");
		artist.setGenre("genre");

		// When
		final JsonContent<Artist> result = jacksonTester.forView(ArtistViews.WithComments.class)
				.write(artist);

		// Then
		assertThat(result).isEqualToJson("{\"artistId\": \"artist id\"," + "\"artistName\": \"artist name\"," + "\"genre\":\"genre\"}," + "\"comments\":[]}");
	}

	@Test
	public void serialization_should_return_json_without_comments_when_WithoutComments_is_used_as_json_view() throws IOException {
		// Given
		final Comment comment = new Comment();
		comment.setMessage("message");
		comment.setUserName("user name");

		final Artist artist = new Artist();
		artist.setId("artist id");
		artist.setName("artist name");
		artist.setGenre("genre");
		artist.addComment(comment);

		// When
		final JsonContent<Artist> result = jacksonTester.forView(ArtistViews.WithoutComments.class)
				.write(artist);

		// Then
		assertThat(result).isEqualToJson("{\"artistId\": \"artist id\"," + "\"artistName\": \"artist name\"," + "\"genre\":\"genre\"}");
	}
	@Test
	public void serialization_should_return_json_with_id_and_name_when_ByGenre_is_used_as_json_view() throws IOException {
		// Given
		final Comment comment = new Comment();
		comment.setMessage("message");
		comment.setUserName("user name");

		final Artist artist = new Artist();
		artist.setId("artist id");
		artist.setName("artist name");
		artist.setGenre("genre");
		artist.addComment(comment);

		// When
		final JsonContent<Artist> result = jacksonTester.forView(CommentsViews.ByGenre.class)
				.write(artist);

		// Then
		assertThat(result).isEqualToJson("{\"artistId\": \"artist id\"," + "\"artistName\": \"artist name\"}");
	}

}
