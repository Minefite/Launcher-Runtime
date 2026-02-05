package com.zeydie.launcher.gui.data;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Data
public class AccountData {
    private String login;
    private int serverId;
    private String oauthAccessToken;
    private String oauthRefreshToken;
    private long oauthExpire;

    public boolean isLogin(@Nullable final String login) {
        if (login == null) return false;

        return login.equals(this.login);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof final AccountData accountData)
            return this.login.equals(accountData.login) && this.serverId == accountData.serverId;

        return false;
    }
}