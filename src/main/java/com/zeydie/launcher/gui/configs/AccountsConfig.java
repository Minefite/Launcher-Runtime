package com.zeydie.launcher.gui.configs;

import com.zeydie.launcher.gui.data.AccountData;
import com.zeydie.sgson.SGsonFile;
import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Data
public class AccountsConfig {
    private @NotNull List<AccountData> accounts = new ArrayList<>();

    public void load() throws IOException {
        if (ReferenceConfig.defaultAccountConfig.toFile().exists()) {
            Files.move(ReferenceConfig.defaultLauncherDirectory, ReferenceConfig.accountConfig, StandardCopyOption.REPLACE_EXISTING);

            ReferenceConfig.defaultLauncherDirectory.toFile().delete();
        }

        this.accounts = new SGsonFile(ReferenceConfig.accountConfig).fromJsonToObject(this).getAccounts();
    }

    public void save() {
        @NonNull val file = new SGsonFile(ReferenceConfig.accountConfig);

        file.getFile().mkdirs();
        file.writeJsonFile(this);
    }
}