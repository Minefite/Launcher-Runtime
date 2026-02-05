package pro.gravit.launcher.gui.components;

import javafx.scene.control.ButtonBase;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.core.api.LauncherAPIHolder;
import pro.gravit.launcher.core.backend.LauncherBackendAPIHolder;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.core.impl.FxComponent;
import pro.gravit.launcher.gui.core.impl.ContextHelper;

public class BasicUserControls extends FxComponent {

    public BasicUserControls(Pane layout, JavaFXApplication application) {
        super(layout, application);
    }

    @Override
    public String getName() {
        return "userControls";
    }

    @Override
    protected void doInit() {
        LookupHelper.<ButtonBase>lookupIfPossible(layout, "#controls", "#exit")
                    .ifPresent((b) -> b.setOnAction((e) -> currentStage.close()));
        LookupHelper.<ButtonBase>lookupIfPossible(layout, "#controls", "#minimize")
                    .ifPresent((b) -> b.setOnAction((e) -> currentStage.hide()));
        LookupHelper.<ButtonBase>lookupIfPossible(layout, "#controls", "#deauth").ifPresent(b -> b.setOnAction(
                (e) -> application.messageManager.showApplyDialog(
                        application.getTranslation("runtime.scenes.settings.exitDialog.header"),
                        application.getTranslation("runtime.scenes.settings.exitDialog.description"),
                        this::userExit, () -> {}, true)));
    }

    protected void userExit() {
        processRequest(application.getTranslation("runtime.scenes.settings.exitDialog.processing"),
                       LauncherBackendAPIHolder.getApi().userExit(),
                       (event) -> {
                           // Exit to main menu
                           ContextHelper.runInFxThreadStatic(() -> {
                               application.gui.loginScene.clearPassword();
                               application.gui.loginScene.reset();
                               try {
                                   application.authService.exit();
                                   //TODO ZeyCodeReplace loginScene on fastLoginScene
                                   this.switchScene(this.application.gui.fastLoginScene);
                                   //TODO ZeyCodeClear
                                   //switchScene(application.gui.loginScene);
                               } catch (Exception ex) {
                                   errorHandle(ex);
                               }
                           });
                       }, (event) -> {});
    }

    @Override
    public void reset() {

    }
}
