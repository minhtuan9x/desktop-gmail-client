package org.tuanit.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tuanit.model.EmailProfile;
import org.tuanit.model.ProfileModel;
import org.tuanit.model.ProfileModelList;
import org.tuanit.util.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfileService {

    @Autowired
    AccountService accountService;


    public void addProfile(String profile) {
        ProfileModelList profileModelList = Helper.getObjectOptionally(ProfileModelList.class).orElse(new ProfileModelList());
        if (!profileModelList.getProfiles().stream().map(ProfileModel::getProfile).collect(Collectors.toList()).contains(profile)) {
            profileModelList.getProfiles().add(new ProfileModel(profile, null));
        }
        Helper.writeFile(profileModelList);
    }

    public void changeNickname(String profile, String nickname) {
        ProfileModelList profileModelList = Helper.getObjectOptionally(ProfileModelList.class).orElse(new ProfileModelList());
        ProfileModel profileModel = profileModelList.getProfiles().stream().filter(profileModel1 -> profileModel1.getProfile().equals(profile))
                .findFirst().orElseThrow(() -> new RuntimeException("not found"));
        profileModelList.getProfiles().remove(profileModel);
        profileModel.setNickname(nickname);
        profileModelList.getProfiles().add(profileModel);
        Helper.writeFile(profileModelList);
    }

    public List<ProfileModel> getAllProfile() {
        ProfileModelList profileModelList = Helper.getObjectOptionally(ProfileModelList.class).orElse(new ProfileModelList());
        return profileModelList.getProfiles();
    }

    public List<EmailProfile> getAllEmailProfile() {
        List<EmailProfile> result = new ArrayList<>();
        List<ProfileModel> profiles = getAllProfile();
        for (ProfileModel profile : profiles) {
            Credential credential = accountService.getCredentials(profile.getProfile());
            Profile person = accountService.getGmailProfile(credential);
            EmailProfile emailProfile = new EmailProfile();
            emailProfile.setEmail(person.getEmailAddress());
            emailProfile.setProfile(profile.getProfile());
            emailProfile.setNickname(profile.getNickname());
            result.add(emailProfile);
        }
        return result;
    }
}
