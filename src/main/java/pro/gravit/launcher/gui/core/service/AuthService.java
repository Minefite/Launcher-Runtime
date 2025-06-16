package pro.gravit.launcher.gui.core.service;

import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.request.auth.password.AuthAESPassword;
import pro.gravit.launcher.base.request.auth.password.AuthPlainPassword;
import pro.gravit.launcher.core.api.method.AuthMethod;
import pro.gravit.launcher.core.api.method.AuthMethodPassword;
import pro.gravit.launcher.core.api.model.SelfUser;
import pro.gravit.launcher.core.api.model.User;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.utils.helper.SecurityHelper;

public class AuthService {
    private final LauncherConfig config = Launcher.getConfig();
    private final JavaFXApplication application;
    private SelfUser user;
    private AuthMethod authAvailability;

    public AuthService(JavaFXApplication application) {
        this.application = application;
    }

    public AuthMethodPassword makePassword(String plainPassword) {
        if (config.passwordEncryptKey != null) {
            try {
                return new AuthAESPassword(encryptAESPassword(plainPassword));
            } catch (Exception ignored) {
            }
        }
        return new AuthPlainPassword(plainPassword);
    }

    private byte[] encryptAESPassword(String password) throws Exception {
        return SecurityHelper.encrypt(Launcher.getConfig().passwordEncryptKey, password);
    }

    public void setUser(SelfUser user) {
        this.user = user;
    }

    public void setAuthAvailability(AuthMethod info) {
        this.authAvailability = info;
    }

    public AuthMethod getAuthAvailability() {
        return authAvailability;
    }

    public boolean isFeatureAvailable(String name) {
        return authAvailability.getFeatures() != null && authAvailability.getFeatures().contains(name);
    }

    public String getUsername() {
        if (user == null) return "Player";
        return user.getUsername();
    }

    public String getMainRole() {
        return "";
    }

    public boolean checkPermission(String name) {
        if (user == null || user.getPermissions() == null) {
            return false;
        }
        return user.getPermissions().hasPerm(name);
    }

    public boolean checkDebugPermission(String name) {
        return application.isDebugMode() || (!application.guiModuleConfig.disableDebugPermissions &&
                checkPermission("launcher.debug."+name));
    }

    public User getPlayerProfile() {
        if (user == null) return null;
        return user;
    }

    public void exit() {
        user = null;
        //.profile = null;
    }
}
