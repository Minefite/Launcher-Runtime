package com.zeydie.launcher.gui.scenes;

import com.zeydie.launcher.gui.Accounts;
import com.zeydie.launcher.gui.data.AccountData;
import com.zeydie.launcher.gui.scenes.components.AccountScroll;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.impl.ContextHelper;
import pro.gravit.launcher.gui.core.impl.FxScene;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.login.LoginScene;
import pro.gravit.utils.helper.LogHelper;

import java.util.Timer;
import java.util.TimerTask;

public class FastLoginScene extends FxScene {
    @Getter
    private final @NotNull String name = "fastlogin";

    private AccountScroll accountsScroll;
    private Button addAccountButton;
    private Button authButton;
    private Pane newyearPane;

    @Setter
    @Getter
    private @Nullable AccountData selectedAccount;

    private final @NotNull Timer timer = new Timer();
    private int scene = 1;

    public FastLoginScene(@NonNull final JavaFXApplication application) {
        super("minefite/scenes/login/fastlogin.fxml", application);
    }

    @Override
    protected void doInit() {
        this.accountsScroll = new AccountScroll(LookupHelper.lookup(super.layout, "#authPane", "#accountsScrollPane"));

        this.addAccountButton = LookupHelper.lookup(super.layout, "#authPane", "#addAccountButton");
        this.authButton = LookupHelper.lookup(super.layout, "#authPane", "#authButton");

        this.addAccountButton.setOnAction(event -> this.switchToLogging());
        this.authButton.setOnAction(event -> this.switchAuth());

        this.newyearPane = LookupHelper.lookup(super.layout, "#newyearPane");

        //TODO Fix
        /*if (Accounts.getAccountsConfig().getAccounts().isEmpty()) {
            LogHelper.debug("No accounts found, switching to logging");

            this.switchToLogging();
        }*/

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                newyearPane.getStyleClass().clear();
                newyearPane.getStyleClass().add("tm-" + scene);

                scene += 1;

                if (scene > 3) scene = 1;
            }
        }, 0, 500);


    }

    @Override
    public void reset() {
        this.selectedAccount = null;
        this.accountsScroll.updateGrid();
    }

    public void switchAuth() {
        if (this.selectedAccount == null) return;

        @NonNull val javaFXApplication = JavaFXApplication.getInstance();
        @NonNull val runtimeSettings = javaFXApplication.runtimeSettings;

        //TODO
        //runtimeSettings.oauthAccessToken = this.selectedAccount.getOauthAccessToken();
        //runtimeSettings.oauthRefreshToken = this.selectedAccount.getOauthRefreshToken();
        //runtimeSettings.oauthExpire = this.selectedAccount.getOauthExpire();

        ContextHelper.runInFxThreadStatic(
                () -> {
                    @NonNull val loginScene = JavaFXApplication.getInstance().gui.loginScene;

                    if (loginScene.auth == null)
                        super.switchScene(loginScene);
                    else {
                        super.switchScene(loginScene);
                        loginScene.getAuthFlow().tryOAuthLogin();
                    }
                }
        );
    }

    @SneakyThrows
    public void switchToLogging() {
        @NonNull val javaFXApplication = JavaFXApplication.getInstance();
        @NonNull val runtimeSettings = javaFXApplication.runtimeSettings;

        //TODO
        //runtimeSettings.oauthAccessToken = null;
        // runtimeSettings.oauthRefreshToken = null;
        //runtimeSettings.oauthExpire = 0;

        ContextHelper.runInFxThreadStatic(
                () -> {
                    @NonNull val fx = JavaFXApplication.getInstance();
                    @NonNull val gui = fx.gui;

                    @NonNull val background = gui.background;

                    if (!background.isInit()) {
                        background.init();
                        fx.getMainStage().pushBackground(background);
                    }

                    @NonNull val loginScene = gui.loginScene;

                    super.switchScene(loginScene);

                    loginScene.postInit();

                    /*if (loginScene.auth == null)
                        super.switchScene(loginScene);
                    else {
                        super.switchScene(loginScene);
                        loginScene.postInit();
                    }*/
                }
        );
    }
}
