package com.dev.safwan.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailMessageDTO {

    private String From;
    private String to;
    private String Subject;
    private String body;

}
