package com.zeydie.launcher.gui.scenes.components;

import com.zeydie.launcher.gui.configs.ReferenceConfig;
import javafx.scene.control.Button;
import lombok.NonNull;

public class AccountButton extends Button {
    public AccountButton(@NonNull final String text) {
        super(text, ReferenceConfig.getAvatar(text));

        super.setMnemonicParsing(false);

        super.setMinWidth(330);
        super.setMinHeight(60);

        super.getStyleClass().add("accountButton");
    }
}
