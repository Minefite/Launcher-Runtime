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
        @NonNull val source = ReferenceConfig.defaultAccountConfig;
        @NonNull val destination = ReferenceConfig.accountConfig;

        if (Files.exists(source)) {
            if (!Files.exists(destination)) {
                Files.createDirectories(destination.getParent());

                Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE);

                source.toFile().delete();
            }
        }

        this.accounts = new SGsonFile(destination).fromJsonToObject(this).getAccounts();
    }

    public void save() {
        @NonNull val file = new SGsonFile(ReferenceConfig.accountConfig);

        file.getFile().mkdirs();
        file.writeJsonFile(this);
    }
}