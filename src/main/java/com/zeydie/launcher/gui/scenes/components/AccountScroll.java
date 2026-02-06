package com.zeydie.launcher.gui.scenes.components;

import com.zeydie.launcher.gui.Accounts;
import com.zeydie.launcher.gui.data.AccountData;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.utils.helper.LogHelper;

import java.io.IOException;
import java.net.URL;

public class AccountScroll {
    private final ScrollPane scrollPane;
    @Getter
    private final GridPane gridPane;

    public AccountScroll(
            @NonNull final ScrollPane scrollPane
    ) {
        this.scrollPane = scrollPane;
        this.gridPane = (GridPane) scrollPane.getContent();

        this.gridPane.setHgap(0);
        this.gridPane.setVgap(0);

        this.updateGrid();
    }

    public void updateGrid() {
        this.gridPane.getChildren().clear();

        @NonNull val accountsConfig = Accounts.getAccountsConfig();
        @NonNull val accountList = accountsConfig.getAccounts();

        for (int i = 0; i < accountList.size(); i++) {
            @NonNull val account = accountList.get(i);
            @NonNull val loginButton = this.getLoginButton(account);

            try {
                this.gridPane.add(loginButton, 0, i);
                this.gridPane.add(this.getServerIcon(account), 1, i);
                this.gridPane.add(this.getExitButton(account, loginButton), 2, i);
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private @NotNull Button getLoginButton(@NonNull final AccountData account) {
        @NonNull val loginButton = new AccountButton(account.getLogin());

        LogHelper.debug("Account button %s %s", loginButton.getText(), loginButton);

        loginButton.setOnAction(
                event -> {
                    this.gridPane.getChildren()
                                 .forEach(
                                         node -> {
                                             if (node instanceof AccountButton) {
                                                 @NonNull val styles = node.getStyleClass();

                                                 styles.remove("accountButton-hovered");

                                                 if (!styles.contains("accountButton"))
                                                     styles.add("accountButton");
                                             }
                                         }
                                 );

                    @NonNull val styles = loginButton.getStyleClass();

                    styles.remove("accountButton");
                    styles.add("accountButton-hovered");

                    JavaFXApplication.getInstance().gui.fastLoginScene.setSelectedAccount(account);
                }
        );

        return loginButton;
    }

    private @NotNull ImageView getServerIcon(@NonNull final AccountData account) throws IOException {
        val serverId = account.getServerId();

        @Nullable var inputStream = this.getClass().getResourceAsStream(
                String.format("/runtime/images/fastlogin/servers/%d.png", serverId));

        if (inputStream == null)
            inputStream = this.getClass().getResourceAsStream("/runtime/images/fastlogin/servers/null.png");
        if (inputStream == null)
            inputStream = new URL(
                    "https://img.favpng.com/19/6/24/check-mark-computer-icons-sign-clip-art-png-favpng-iFiVE36gqaa5HBVQ4AWZz1tHP.jpg").openStream();

        @NonNull val serverImage = new ImageView(new Image(inputStream));

        val width = 25;
        val height = 25;

        serverImage.setFitHeight(height);
        serverImage.setFitWidth(width);
        serverImage.maxHeight(height);
        serverImage.maxWidth(width);

        serverImage.getStyleClass().add("serverImage");

        return serverImage;
    }

    private @NotNull Button getExitButton(
            @NonNull final AccountData account,
            @NonNull final Button loginButton
    ) throws IOException {
        @NonNull val javaFXApplication = JavaFXApplication.getInstance();
        @NonNull val runtimeSettings = javaFXApplication.runtimeSettings;
        @NonNull val authService = javaFXApplication.authService;

        @NonNull val accountsConfig = Accounts.getAccountsConfig();
        @NonNull val accountList = accountsConfig.getAccounts();

        @Nullable var inputStream = this.getClass().getResourceAsStream("/runtime/images/fastlogin/trash.png");

        if (inputStream == null)
            inputStream = new URL(
                    "https://img.favpng.com/19/6/24/check-mark-computer-icons-sign-clip-art-png-favpng-iFiVE36gqaa5HBVQ4AWZz1tHP.jpg").openStream();

        @NonNull val exitImage = new ImageView(new Image(inputStream));

        val width = 18;
        val height = 21;

        exitImage.setFitHeight(height);
        exitImage.setFitWidth(width);
        exitImage.maxHeight(height);
        exitImage.maxWidth(width);

        @NonNull val exitButton = new Button("", exitImage);

        exitButton.setOnMouseClicked(
                event -> {
                    //TODO
                    //runtimeSettings.oauthAccessToken = null;
                    //runtimeSettings.oauthRefreshToken = null;
                    //runtimeSettings.oauthExpire = 0;

                    accountList.removeIf(entry -> entry.equals(account));
                    accountsConfig.save();

                    this.updateGrid();
                }
        );
        exitButton.setOnMouseEntered(event -> loginButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15);-fx-background-radius: 12px;"));
        exitButton.setOnMouseExited(event -> loginButton.setStyle(""));

        exitButton.getStyleClass().add("exitButton");

        return exitButton;
    }
}