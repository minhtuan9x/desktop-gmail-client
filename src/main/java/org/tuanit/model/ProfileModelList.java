package org.tuanit.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProfileModelList {
    private List<ProfileModel> profiles = new ArrayList<>();
}
