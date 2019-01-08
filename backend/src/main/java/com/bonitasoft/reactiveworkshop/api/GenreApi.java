package com.bonitasoft.reactiveworkshop.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;

@RestController
public class GenreApi {



    private ArtistRepository artistRepository;
    private RestTemplate restTemplate;

    public GenreApi(ArtistRepository artistRepository, RestTemplate restTemplate) {
        this.artistRepository = artistRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/genres")
    public List<String> findAll() {
        return artistRepository.findAll().stream()
                .map(Artist::getGenre)
                .filter(g -> !g.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

}
