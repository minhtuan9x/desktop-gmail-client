package org.tuanit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListExecutingStatus implements Serializable {
    private List<ExecutingHistory> executingHistories = new ArrayList<>();
}
