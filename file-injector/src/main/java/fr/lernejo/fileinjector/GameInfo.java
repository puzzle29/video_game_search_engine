package fr.lernejo.fileinjector;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class GameInfo {

    private String id;

    private String title;
    private String thumbnail;
    @JsonProperty("short_description")
    private String shortDescription;
    private String genre;
    private String platform;
    private String publisher;
    private String developer;
    @JsonProperty("release_date")
    private LocalDate releaseDate;

    private String freetogame_profile_url;

    private String game_url;

    // Constructeurs, getters et setters...

    public GameInfo() {
        // Constructeur par défaut nécessaire pour la désérialisation JSON
    }

    public GameInfo(String title, String thumbnail, String shortDescription, String genre, String platform,
                    String publisher, String developer, LocalDate releaseDate) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.shortDescription = shortDescription;
        this.genre = genre;
        this.platform = platform;
        this.publisher = publisher;
        this.developer = developer;
        this.releaseDate = releaseDate;
    }

    // Getters et Setters (assurez-vous de générer ces méthodes ou de les implémenter manuellement)

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFreetogame_profile_url() {
        return freetogame_profile_url;
    }

    public String setFreetogame_profile_url(String freetogame_profile_url) {
        return this.freetogame_profile_url = freetogame_profile_url;
    }
}
