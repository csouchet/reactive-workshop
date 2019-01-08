package com.bonitasoft.reactiveworkshop.domain.artist;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

//import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Transient;

import com.bonitasoft.reactiveworkshop.domain.comment.Comment;
import com.bonitasoft.reactiveworkshop.domain.comment.CommentsViews;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//@Document
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder(value = {"artistId", "artistName", "genre", "comments"})
public class Artist {

	@JsonView({ArtistViews.WithoutComments.class, CommentsViews.ByGenre.class})
	@JsonProperty("artistId")
	@JsonAlias({"artist"})
	@Id
	private String id;

	@JsonView({ArtistViews.WithoutComments.class, CommentsViews.ByGenre.class})
	@JsonProperty("artistName")
	private String name;

	@JsonView(ArtistViews.WithoutComments.class)
	private String genre;

	@JsonView(ArtistViews.WithComments.class)
	@Transient
	@Builder.Default
	@EqualsAndHashCode.Exclude
	private List<Comment> comments = new ArrayList<>();

	/**
	 * Add a new comment to the current artist
	 *
	 * @param comment
	 *            The comment to add
	 */
	public void addComment(final Comment comment) {
		comments.add(comment);
		comment.setArtist(this);
	}

	/**
	 * Remove a comment to the current artist
	 *
	 * @param comment
	 *            The comment to remove
	 */
	public void removeComment(final Comment comment) {
		comment.setArtist(null);
		comments.remove(comment);
	}

}
