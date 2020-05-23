package com.example.newsfeedapp;

public class News {
    private String newsSection, newsDate, newsTitle, newsUrl;

    /**
     * Constructor of News class
     * @param newsSection is the section of the news
     * @param newsDate is the date of the news
     * @param newsTitle is the title of the news
     * @param newsUrl is the url of the news
     */
    public News(String newsSection, String newsDate, String newsTitle, String newsUrl){
        this.newsSection = newsSection;
        this.newsDate = newsDate;
        this.newsTitle = newsTitle;
        this.newsUrl = newsUrl;
    }

    public String getNewsSection(){
        return newsSection;
    }

    public String getNewsDate(){
        return newsDate;
    }

    public String getNewsTitle(){
        return newsTitle;
    }

    public String getNewsUrl(){
        return newsUrl;
    }
}
