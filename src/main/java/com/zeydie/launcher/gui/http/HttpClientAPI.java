package com.zeydie.launcher.gui.http;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.gravit.launcher.base.Downloader;
import pro.gravit.launcher.gui.core.JavaFXApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class HttpClientAPI {
    @Getter
    public static @NotNull HttpClientAPI instance = new HttpClientAPI();

    @Getter
    private final @NotNull String skinUrl = "https://minefite.net/ExtraModules/sac/skins/%s.png";
    private final @NotNull String serverIdUrl = "https://admin.minefite.net/fs/launcher-serverId.php?uuid=%s";
    private final @NotNull String uuidUrl = "https://admin.minefite.net/fs/launcher-uuid.php?player=%s";
    private final @NotNull String serverNameUrl = "https://admin.minefite.net/fs/launcher-serverName.php?serverId=%d";
    private final @NotNull String twoFACheckUrl = "https://admin.minefite.net/api/Google2FA.php?login=%s&action=activated";
    private final @NotNull String twoFAValidationUrl = "https://admin.minefite.net/api/Google2FA.php?login=%s&code=%s";

    @SneakyThrows
    public void cacheSkin(@NonNull final String login) {
        @NonNull val skinManager = JavaFXApplication.getInstance().skinManager;

        skinManager.addSkin(login, new URL(String.format(this.skinUrl, login)).toURI());
        skinManager.getSkin(login);
    }

    public int getServerIdForUUID(@NonNull final UUID uuid) {
        return this.get(this.serverIdUrl, uuid).toInteger();
    }

    public @NotNull UUID getUUIDForLogin(@NonNull final String login) {
        @Nullable val uuid = this.get(this.uuidUrl, login).content();

        return (uuid == null || uuid.isEmpty()) ? UUID.randomUUID() : UUID.fromString(uuid);
    }

    public @NotNull String getServerOfServerId(final int serverId) {
        return this.get(this.serverNameUrl, serverId).content();
    }

    public boolean has2FA(@NonNull final String login) {
        return this.get(this.twoFACheckUrl, login).toBoolean();
    }

    public boolean isValid2FA(
            @NonNull final String login,
            @NonNull final String code
    ) {
        return this.get(this.twoFAValidationUrl, login, code).toBoolean();
    }

    private @NotNull Result get(@NonNull final String url, @NonNull final Object... params) {
        try {
            return execute(
                    HttpRequest.newBuilder()
                               .GET()
                               .uri(new URI(String.format(url, params)))
                               .build()
            );
        } catch (final Exception exception) {
            exception.printStackTrace();

            return new Result(500, null);
        }
    }

    private @NotNull Result execute(@NonNull final HttpRequest httpRequest) {
        @NonNull val httpClient = Downloader.newHttpClientBuilder()
                                            .build();

        try {
            @NonNull val response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            @NonNull val result = new Result(response.statusCode(), response.body());
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        return new Result(500, null);
    }

    public static record Result(
            int status,
            @Nullable String content
    ) {
        public boolean isOk() {
            return this.status == 200;
        }

        public boolean toBoolean() {
            if (this.content != null) {
                if (this.content.equalsIgnoreCase("yes"))
                    return true;
                if (this.content.equalsIgnoreCase("no"))
                    return false;

                try {
                    return Boolean.parseBoolean(this.content);
                } catch (final Exception exception) {
                    exception.printStackTrace();
                    return false;
                }
            }

            return false;
        }

        public int toInteger() {
            if (this.content != null) {
                try {
                    return Integer.parseInt(this.content);
                } catch (final Exception exception) {
                    exception.printStackTrace();
                }
            }

            return 0;
        }
    }
}