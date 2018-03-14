package com.botscrew.messengercdk.model.outgoing.template.list;

import com.botscrew.messengercdk.model.outgoing.button.Button;
import com.botscrew.messengercdk.model.outgoing.template.Payload;
import com.botscrew.messengercdk.model.outgoing.template.TemplateElement;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListTemplatePayload extends Payload {
    private List<TemplateElement> elements;
    private List<Button> buttons;

    @JsonProperty("top_element_style")
    private TopElementStyle topElementStyle;

    public ListTemplatePayload(List<TemplateElement> elements, List<Button> buttons, TopElementStyle topElementStyle) {
        super("list");
        this.elements = elements;
        this.buttons = buttons;
        this.topElementStyle = topElementStyle;
    }
}