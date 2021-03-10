// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.gitee.hperfect.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;
    AppSettingsState settings;

    public AppSettingsConfigurable(Project project) {
        settings = project.getService(AppSettingsState.class);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "yapi项目配置";
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getMyMainPanel();
    }

    @Override
    public boolean isModified() {
        boolean modified = !mySettingsComponent.getYapiHostText().getText().equals(settings.getYapiHost());
        modified |= !mySettingsComponent.getYapiTokenText().getText().equals(settings.getYapiToken());
        modified |= !mySettingsComponent.getYapiProjectIdText().getText().equals(settings.getYapiProjectId());
        modified |= !mySettingsComponent.getExcludeFieldText().getText().equals(settings.getExcludeFields());

        return modified;
    }

    @Override
    public void apply() {
        settings.setYapiHost(mySettingsComponent.getYapiHostText().getText());
        settings.setYapiToken(mySettingsComponent.getYapiTokenText().getText());
        settings.setYapiProjectId(mySettingsComponent.getYapiProjectIdText().getText());
        settings.setExcludeFields(mySettingsComponent.getExcludeFieldText().getText());
    }

    @Override
    public void reset() {
        mySettingsComponent.getYapiProjectIdText().setText(settings.getYapiProjectId());
        mySettingsComponent.getYapiTokenText().setText(settings.getYapiToken());
        mySettingsComponent.getYapiHostText().setText(settings.getYapiHost());
        mySettingsComponent.getExcludeFieldText().setText(settings.getExcludeFields());
    }


    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
