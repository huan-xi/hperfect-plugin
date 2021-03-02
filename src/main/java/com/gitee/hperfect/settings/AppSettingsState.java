// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.gitee.hperfect.settings;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@Getter
@Setter
@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)})
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    private String yapiToken;
    private String yapiProjectId;
    private String yapiHost;

    public static AppSettingsState getInstance(Project project) {
        return ServiceManager.getService(project,AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }


    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }


    public boolean validateYapi() {
        return StrUtil.isNotBlank(yapiToken) && StrUtil.isNotBlank(yapiProjectId) && StrUtil.isNotBlank(yapiHost);
    }
}
