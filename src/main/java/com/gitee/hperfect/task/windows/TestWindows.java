package com.gitee.hperfect.task.windows;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/22 3:04 下午
 */

public class TestWindows {
    private final Project project;
    private final ToolWindow toolWindow;
    private JPanel testPanel;
    private JButton addBtn;
    private JButton delBtn;

    public TestWindows(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
    }


    public JPanel getTestPanel() {
        return testPanel;
    }
}
