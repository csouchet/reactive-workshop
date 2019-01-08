package com.bonitasoft.reactiveworkshop.domain.comment;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.domain.artist.ArtistViews;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author SOUCHET Céline
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder(value = {"artistId", "artistName", "userName", "comment"})
public class Comment {

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonView(CommentsViews.ByGenre.class)
	@JsonUnwrapped
	private Artist artist;

	@JsonView({ArtistViews.WithComments.class, CommentsViews.ByGenre.class})
	private String userName;

	@JsonView({ArtistViews.WithComments.class, CommentsViews.ByGenre.class})
	@JsonProperty("comment")
	private String message;

}
