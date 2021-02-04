// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.gitee.hperfect.settings;

import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 *
 * @author huanxi
 */
@Getter
@Setter
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final EditorTextField yapiHostText;
    private final EditorTextField yapiTokenText;
    private final EditorTextField yapiProjectIdText;

    public AppSettingsComponent() {
        yapiHostText = new EditorTextField();
        yapiHostText.setPlaceholder("http://doc.domain.com");
        yapiTokenText = new EditorTextField();
        yapiProjectIdText = new EditorTextField();

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("yapi host:"), yapiHostText, 1, false)
                .addLabeledComponent(new JBLabel("yapi token:"), yapiTokenText, 1, false)
                .addLabeledComponent(new JBLabel("yapi project id:"), yapiProjectIdText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

}
