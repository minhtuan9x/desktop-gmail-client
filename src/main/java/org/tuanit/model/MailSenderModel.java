package org.tuanit.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MailSenderModel {
    private String subject;
    private String content;
    private Integer sleep;
    private Boolean isHtml;
    private List<String> files = new ArrayList<>();
}
