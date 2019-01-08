package com.bonitasoft.reactiveworkshop.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bonitasoft.reactiveworkshop.domain.artist.Artist;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;

@RestController
public class ArtistAPI {


    private ArtistRepository artistRepository;
    private RestTemplate restTemplate;

    public ArtistAPI(ArtistRepository artistRepository, RestTemplate restTemplate) {
        this.artistRepository = artistRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/artist/{id}")
    public Artist findById(@PathVariable String id) throws NotFoundException {
        return artistRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/artists")
    public List<Artist> findAll() throws NotFoundException {
        return artistRepository.findAll();
    }

}
