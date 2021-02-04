// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.gitee.hperfect.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

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
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !mySettingsComponent.getYapiHostText().getText().equals(settings.getYapiHost());
        modified |= !mySettingsComponent.getYapiTokenText().getText().equals(settings.getYapiToken());
        modified |= !mySettingsComponent.getYapiProjectIdText().getText().equals(settings.getYapiProjectId());
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.setYapiHost(mySettingsComponent.getYapiHostText().getText());
        settings.setYapiToken(mySettingsComponent.getYapiTokenText().getText());
        settings.setYapiProjectId(mySettingsComponent.getYapiProjectIdText().getText());
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.getYapiProjectIdText().setText(settings.getYapiProjectId());
        mySettingsComponent.getYapiTokenText().setText(settings.getYapiToken());
        mySettingsComponent.getYapiHostText().setText(settings.getYapiHost());
    }


    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
