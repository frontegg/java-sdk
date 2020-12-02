package com.frontegg.sdk.events.channels;

public class DefaultWebpushProperties implements WebpushProperties{

    /**
     * Webpush notification title.
     */
    private String title;

    /**
     * Webpush notification body.
     */
    private String body;

    /**
     * Send webpush to one user.
     */
    private String userId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
