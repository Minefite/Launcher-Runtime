package pro.gravit.launcher.gui.scenes.serverinfo;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import lombok.NonNull;
import lombok.val;
import pro.gravit.launcher.core.api.features.ProfileFeatureAPI;
import pro.gravit.launcher.core.backend.LauncherBackendAPIHolder;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.components.ServerButton;
import pro.gravit.launcher.gui.components.UserBlock;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.core.impl.FxScene;
import pro.gravit.launcher.gui.scenes.interfaces.SceneSupportUserBlock;

public class ServerInfoScene extends FxScene implements SceneSupportUserBlock {
    private ServerButton serverButton;
    private UserBlock userBlock;

    public ServerInfoScene(JavaFXApplication application) {
        super("scenes/serverinfo/serverinfo.fxml", application);
    }

    @Override
    protected void doInit() {
        this.userBlock = use(layout, UserBlock::new);
        LookupHelper.<Button>lookup(layout, "#back").setOnAction((e) -> {
            try {
                switchToBackScene();
            } catch (Exception exception) {
                errorHandle(exception);
            }
        });

        //TODO ZeyCodeStart
        LookupHelper.<ButtonBase>lookupIfPossible(layout, "#settings")
                .ifPresent(
                        (buttonBase) -> buttonBase.setOnAction(
                                (e) -> {
                                    try {
                                        @NonNull val settings = application.gui.settingsScene;

                                        super.switchScene(settings);

                                        settings.reset();

                                    } catch (final Exception exception) {
                                        errorHandle(exception);
                                    }
                                }
                        )
                );

        LookupHelper.<ButtonBase>lookupIfPossible(layout, "#startProfile")
                .ifPresent(
                        buttonBase -> {
                            //button.setText(this.application.getTranslation("runtime.scenes.serverinfo.serverButton.game"));
                            buttonBase.setOnAction(e -> runClient());
                        }
                );

        LookupHelper.<ButtonBase>lookupIfPossible(layout, "#clientSettings")
                .ifPresent(
                        buttonBase -> {
                            buttonBase.setOnAction(
                                    e -> {
                                        if (this.application.profileService.getCurrentProfile() == null) return;

                                        @NonNull val options = application.gui.optionsScene;

                                        try {
                                            super.switchScene(options);
                                        } catch (final Exception exception) {
                                            exception.printStackTrace();
                                        }

                                        options.reset();
                                    }
                            );
                        }
                );
        //TODO ZeyCodeEnd
        //TODO ZeyCodeClear
        /*
        LookupHelper.<ButtonBase>lookup(header, "#controls", "#clientSettings").setOnAction((e) -> {
            try {
                if (application.profileService.getCurrentProfile() == null) return;
                switchScene(application.gui.optionsScene);
                application.gui.optionsScene.reset();
            } catch (Exception ex) {
                errorHandle(ex);
            }
        });

        LookupHelper.<ButtonBase>lookup(header, "#controls", "#settings").setOnAction((e) -> {
            try {
                switchScene(application.gui.settingsScene);
                application.gui.settingsScene.reset();
            } catch (Exception exception) {
                errorHandle(exception);
            }
        });*/
        reset();
    }

    @Override
    public void reset() {
        ProfileFeatureAPI.ClientProfile profile = application.profileService.getCurrentProfile();
        LookupHelper.<Label>lookupIfPossible(layout, "#serverName").ifPresent((e) -> e.setText(profile.getName()));
        LookupHelper.<ScrollPane>lookupIfPossible(layout, "#serverDescriptionPane").ifPresent((e) -> {
            var label = (Label) e.getContent();
            label.setText(profile.getDescription());
        });


        Pane serverButtonContainer = LookupHelper.lookup(layout, "#serverButton");
        serverButtonContainer.getChildren().clear();
        serverButton = ServerButton.createServerButton(application, profile);
        serverButton.addTo(serverButtonContainer);
        //TODO ZeyCodeClear
        /*serverButton.enableSaveButton(application.getTranslation("runtime.scenes.serverinfo.serverButton.game"),
                (e) -> runClient());*/
        //TODO ZeyCodeStart
        LookupHelper.lookupIfPossible(layout, "#startProfile")
                .ifPresent(
                        node -> {
                            if (node instanceof final Button button) {
                                //button.setText(this.application.getTranslation("runtime.scenes.serverinfo.serverButton.game"));
                                button.setOnAction(e -> runClient());
                            }
                        }
                );
        //TODO ZeyCodeEnd

        this.userBlock.reset();
    }

    private void runClient() {
        var profile = application.profileService.getCurrentProfile();
        contextHelper.runInFxThread(() -> {
            switchScene(application.gui.updateScene);
            var downloadProfile = LauncherBackendAPIHolder.getApi().downloadProfile(profile,
                    LauncherBackendAPIHolder.getApi().makeClientProfileSettings(profile),
                    application.gui.updateScene.makeDownloadCallback());
            downloadProfile.thenAccept((readyProfile) -> {
                contextHelper.runInFxThread(() -> {
                    switchScene(application.gui.debugScene);
                    application.gui.debugScene.run(readyProfile);
                }).handle((success, error) -> {
                    if (error != null) {
                        errorHandle(error);
                    }
                    return null;
                });
            }).exceptionally(e -> {
                contextHelper.runInFxThread(() -> {
                    errorHandle(e);
                });
                return null;
            });

        });
        /*application.launchService.launchClient().thenAccept((clientInstance -> {
            if (application.runtimeSettings.globalSettings.debugAllClients || clientInstance.getSettings().debug) {
                contextHelper.runInFxThread(() -> {
                    try {
                        switchScene(application.gui.debugScene);
                        application.gui.debugScene.onClientInstance(clientInstance);
                    } catch (Exception ex) {
                        errorHandle(ex);
                    }
                });
            } else {
                clientInstance.start();
                clientInstance.getOnWriteParamsFuture().thenAccept((ok) -> {
                    LogHelper.info("Params write successful. Exit...");
                    Platform.exit();
                }).exceptionally((ex) -> {
                    contextHelper.runInFxThread(() -> errorHandle(ex));
                    return null;
                });
            }
        })).exceptionally((ex) -> {
            contextHelper.runInFxThread(() -> errorHandle(ex));
            return null;
        });*/
    }

    @Override
    public String getName() {
        return "serverinfo";
    }

    @Override
    public UserBlock getUserBlock() {
        return userBlock;
    }
}
