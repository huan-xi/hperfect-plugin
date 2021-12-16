// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.gitee.hperfect.settings;

import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/**
 * 设置面板
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
    private final EditorTextField excludeFieldText;

    public AppSettingsComponent() {
        yapiHostText = new EditorTextField();
        excludeFieldText = new EditorTextField();
        yapiHostText.setPlaceholder("http://doc.domain.com");
        excludeFieldText.setPlaceholder("以逗号连接");
        yapiTokenText = new EditorTextField();
        yapiProjectIdText = new EditorTextField();

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("yapi地址(不要加/结尾):"), yapiHostText, 1, false)
                .addLabeledComponent(new JBLabel("yapi token:"), yapiTokenText, 1, false)
                .addLabeledComponent(new JBLabel("yapi项目id:"), yapiProjectIdText, 1, false)
                .addLabeledComponent(new JBLabel("全局排除字段(逗号连接):"), excludeFieldText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

}
