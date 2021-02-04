package com.gitee.hperfect.task.windows;

import com.intellij.ui.EditorTextField;

import javax.swing.*;
import java.awt.*;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/22 5:33 下午
 */
public class AddTaskPanel extends JPanel {

    public AddTaskPanel() {
        this.setSize(400, 400);
        EditorTextField title = new EditorTextField();
        title.setPlaceholder("新建代办事项");
        title.setOneLineMode(false);
        EditorTextField detail = new EditorTextField();
        Dimension miniSized = new Dimension(100, 200);
        detail.setOneLineMode(false);
        detail.setMinimumSize(miniSized);
        detail.setPlaceholder("备注");
        this.setLayout(new BorderLayout());
        this.add(title, BorderLayout.NORTH);
        this.add(detail, BorderLayout.CENTER);
    }

}
