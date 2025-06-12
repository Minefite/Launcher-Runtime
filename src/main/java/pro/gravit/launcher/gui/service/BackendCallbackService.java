package pro.gravit.launcher.gui.service;

import pro.gravit.launcher.core.backend.LauncherBackendAPI;

import java.util.concurrent.CompletableFuture;

public class BackendCallbackService extends LauncherBackendAPI.MainCallback {
    public CompletableFuture<LauncherBackendAPI.LauncherInitData> initDataCallback;
}
