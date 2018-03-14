package com.botscrew.messengercdk.model.outgoing.button;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostbackButton extends Button {

    private String payload;

    public PostbackButton(String title, String payload) {
        super("postback", title);
        this.payload = payload;
    }
}