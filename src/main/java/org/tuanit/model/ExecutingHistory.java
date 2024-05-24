package org.tuanit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutingHistory {
    private Integer stt;
    private String date;
    private String profileName;
    private String name;
    private String status;
    private String title;
    private List<String> logs = new ArrayList<>();
}
