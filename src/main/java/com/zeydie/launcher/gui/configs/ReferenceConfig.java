package com.zeydie.launcher.gui.configs;

import com.zeydie.launcher.gui.Accounts;
import com.zeydie.launcher.gui.data.AccountData;
import com.zeydie.launcher.gui.http.HttpClientAPI;
import javafx.scene.image.ImageView;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.utils.JavaFxUtils;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.utils.helper.LogHelper;

import java.nio.file.Path;

public class ReferenceConfig {
    private static final @NotNull HttpClientAPI httpClient = HttpClientAPI.getInstance();

    public static final @NotNull Path launcherDirectory = DirBridge.dirUpdates;
    public static final @NotNull Path defaultLauncherDirectory = DirBridge.defaultUpdatesDir;

    public static final @NotNull Path accountConfig = launcherDirectory.resolve("accounts.cfg");
    public static final @NotNull Path defaultAccountConfig = defaultLauncherDirectory.resolve("accounts.cfg");

    public static @NotNull ImageView getAvatar(@NonNull final String login) {
        @NonNull val imageView = new ImageView();

        imageView.setFitHeight(35);
        imageView.setFitWidth(35);
        imageView.maxHeight(35);
        imageView.maxWidth(35);

        httpClient.cacheSkin(login);

        if (JavaFXApplication.getInstance().skinManager.getSkin(login) != null)
            JavaFxUtils.putAvatarToImageView(JavaFXApplication.getInstance(), login, imageView);
        else {
            httpClient.cacheSkin("default");

            JavaFxUtils.putAvatarToImageView(JavaFXApplication.getInstance(), "default", imageView);
        }

        return imageView;
    }

    public static boolean isPlayerServer(final int sortIndex) {
        @Nullable val account = getAuthedAccount();

        if (account == null) return false;

        val serverId = account.getServerId();

        LogHelper.debug("ServerId %d, account %s", serverId, account);

        return serverId == -1 || serverId == sortIndex;
    }

    public static @Nullable AccountData getAuthedAccount() {
        @Nullable val oauthRefreshToken = "";//TODO JavaFXApplication.getInstance().runtimeSettings.oauthRefreshToken;

        LogHelper.debug("OauthRefreshToken %s", oauthRefreshToken);

        if (oauthRefreshToken == null) return null;

        return Accounts.getAccountsConfig()
                       .getAccounts()
                       .stream()
                       .filter(account -> account.getLogin().equals(oauthRefreshToken.split("\\.")[0]))
                       .findFirst()
                       .orElse(null);
    }
}