package com.gitee.hperfect.task.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.gitee.hperfect.task.windows.AddTaskPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/22 5:16 下午
 */
public class AddTaskDialogWrapper extends DialogWrapper {

    public AddTaskDialogWrapper() {

        super(true);
        setTitle("添加任务");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new AddTaskPanel();
    }


    @Override
    protected void doOKAction() {
        System.out.println("ok");

        super.doOKAction();
    }
}
