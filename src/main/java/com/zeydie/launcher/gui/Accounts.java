package com.zeydie.launcher.gui;

import com.zeydie.launcher.gui.configs.AccountsConfig;
import com.zeydie.launcher.gui.data.AccountData;
import com.zeydie.launcher.gui.http.HttpClientAPI;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;

import java.io.IOException;

public final class Accounts {
    @Getter
    private static final @NotNull AccountsConfig accountsConfig = new AccountsConfig();

    public static void load() {
        try {
            accountsConfig.load();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void authed(@NonNull final AuthFlow.SuccessAuth successAuth) {
        @NonNull val user = successAuth.user();

        @NonNull val login = user.getUsername();

        @NonNull val javaFXApplication = JavaFXApplication.getInstance();
        @NonNull val runtimeSettings = javaFXApplication.runtimeSettings;
        @NonNull val accountList = accountsConfig.getAccounts();
        @NonNull val filtered = accountList.stream()
                                           .filter(account -> account.isLogin(login))
                                           .findAny();

        @NonNull val accessToken = user.getAccessToken();
        //@NonNull val refreshToken = runtimeSettings.oauthRefreshToken;
        //val expire = runtimeSettings.oauthExpire;
        val serverId = HttpClientAPI.getInstance().getServerIdForUUID(user.getUUID());

        if (filtered.isPresent()) {
            @NonNull val account = filtered.get();

            account.setLogin(login);
            account.setOauthAccessToken(accessToken);
          //  account.setOauthRefreshToken(refreshToken);
          //  account.setOauthExpire(expire);
            account.setServerId(serverId);
        } else {
            @NonNull val account = new AccountData();

            account.setLogin(login);
            account.setOauthAccessToken(accessToken);
           // account.setOauthRefreshToken(refreshToken);
          //  account.setOauthExpire(expire);
            account.setServerId(serverId);

            accountList.add(account);
        }

        accountsConfig.save();
    }
}