package com.example.backend.dto.account;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardDTO {
	@JsonProperty("Ranking")
    private String ranking;
    
    @JsonProperty("Card Name")
    private String cardName;
    
    @JsonProperty("Corporate Name")
    private String corporateName;
    
    @JsonProperty("Benefits")
    private List<String> benefits;
    
    @JsonProperty("Image URLs")
    private String imageUrl;

    // Getters and Setters
    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
