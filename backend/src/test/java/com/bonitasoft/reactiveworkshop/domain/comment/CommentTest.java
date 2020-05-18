package com.bonitasoft.reactiveworkshop.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.domain.artist.ArtistViews;
import com.bonitasoft.reactiveworkshop.util.extension.TestStatusLoggerExtension;

/**
 * @author SOUCHET Céline
 *
 */
@ExtendWith({TestStatusLoggerExtension.class, SpringExtension.class})
@JsonTest
public class CommentTest {

	@Autowired
	private JacksonTester<Comment> jacksonTester;

	@Test
	public void serialization_should_return_unwrapped_json_without_artist_fields_when_WithComments_is_used_as_json_view() throws IOException {
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
		final JsonContent<Comment> result = jacksonTester.forView(ArtistViews.WithComments.class)
				.write(comment);

		// Then
		assertThat(result).isEqualToJson("{\"comment\":\"message\"," + "\"userName\":\"user name\"}");
	}

	@Test
	public void serialization_should_return_unwrapped_json_with_artist_fields_when_ByGenre_is_used_as_json_view() throws IOException {
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
		final JsonContent<Comment> result = jacksonTester.forView(CommentsViews.ByGenre.class)
				.write(comment);

		// Then
		assertThat(result).isEqualToJson("{\"artistId\": \"artist id\"," + "\"artistName\": \"artist name\"," + "\"comment\":\"message\","
				+ "\"userName\":\"user name\"}");
	}

	@Test
	public void deserialization_should_return_Comment_when_json_is_unwrapped_with_artist() throws IOException {
		// Given
		final Artist artist = new Artist();
		artist.setId("artist id");

		final Comment comment = new Comment();
		comment.setMessage("message");
		comment.setUserName("user name");
		comment.setArtist(artist);

		final String json = "{\"artist\": \"artist id\"," + "\"comment\":\"message\"," + "\"userName\":\"user name\"}";

		// When
		final Comment result = jacksonTester.parseObject(json);

		// Then
		assertThat(result).isEqualTo(comment);
	}
}
