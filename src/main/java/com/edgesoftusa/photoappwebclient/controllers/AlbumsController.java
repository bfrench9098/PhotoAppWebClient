package com.edgesoftusa.photoappwebclient.controllers;

import com.edgesoftusa.photoappwebclient.model.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate; //before Defined initialized

import java.util.Arrays;

import java.util.List;

@Controller
public class AlbumsController {
    @Autowired
    OAuth2AuthorizedClientService oauth2ClientService;
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/albums")
    public String getAlbums(Model model,
                            @AuthenticationPrincipal OidcUser principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient oauth2Client = oauth2ClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        String jwtAccessToken = oauth2Client.getAccessToken().getTokenValue();
        System.out.println("Access token = " + jwtAccessToken);

        System.out.println("Principal = " + principal);

        OidcIdToken idToken = principal.getIdToken();
        String idTokenValue = idToken.getTokenValue();
        System.out.println("Id token = " + idTokenValue);

        String url = "http://localhost:9091/albums";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtAccessToken);

        HttpEntity<List<Album>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Album>> responseEntity =  restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Album>>() {});

        List<Album> albums = responseEntity.getBody();

        model.addAttribute("albums", albums);

//        Album album1 = new Album();
//        album1.setAlbumId("albumOne");
//        album1.setAlbumTitle("Album one title");
//        album1.setAlbumUrl("http://localhost:8082/albums/1");
//
//        Album album2 = new Album();
//        album2.setAlbumId("albumTwo");
//        album2.setAlbumTitle("Album two title");
//        album2.setAlbumUrl("http://localhost:8082/albums/2");
//
//        model.addAttribute("albums", Arrays.asList(album1, album2));

        return "albums";
    }
}
