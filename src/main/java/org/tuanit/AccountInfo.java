package org.tuanit;

import com.google.api.client.auth.oauth2.Credential;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccountInfo implements Serializable {
    private final Credential credential;

    public AccountInfo(Credential credential) {
        this.credential = credential;
    }
}
