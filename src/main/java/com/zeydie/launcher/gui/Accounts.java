package com.zeydie.launcher.gui;

import com.zeydie.launcher.gui.configs.AccountsConfig;
import com.zeydie.launcher.gui.data.AccountData;
import com.zeydie.launcher.gui.http.HttpClientAPI;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.gravit.launcher.core.backend.LauncherBackendAPIHolder;
import pro.gravit.launcher.core.backend.UserSettings;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.config.StdSettingsManager;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.runtime.backend.BackendSettings;

import java.io.IOException;
import java.util.function.Function;

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
        @NonNull val auth = getAuthData();
        @NonNull val accountList = accountsConfig.getAccounts();
        @NonNull val filtered = accountList.stream()
                .filter(account -> account.isLogin(login))
                .findAny();

        @NonNull val accessToken = auth.accessToken;
        @NonNull val refreshToken = auth.refreshToken;
        val expire = auth.expireIn;
        val serverId = HttpClientAPI.getInstance().getServerIdForUUID(user.getUUID());

        if (filtered.isPresent()) {
            @NonNull val account = filtered.get();

            account.setLogin(login);
            account.setOauthAccessToken(accessToken);
            account.setOauthRefreshToken(refreshToken);
            account.setOauthExpire(expire);
            account.setServerId(serverId);
        } else {
            @NonNull val account = new AccountData();

            account.setLogin(login);
            account.setOauthAccessToken(accessToken);
            account.setOauthRefreshToken(refreshToken);
            account.setOauthExpire(expire);
            account.setServerId(serverId);

            accountList.add(account);
        }

        accountsConfig.save();
    }

    public static @NotNull BackendSettings getBackendSettings() {
        return (BackendSettings) (LauncherBackendAPIHolder.getApi().getUserSettings("backend", s -> new BackendSettings()));
    }

    public static @Nullable BackendSettings.AuthorizationData getAuthData() {
        return getBackendSettings().auth;
    }
}