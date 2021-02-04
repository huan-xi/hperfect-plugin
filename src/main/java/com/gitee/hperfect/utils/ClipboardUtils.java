package com.gitee.hperfect.utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/4 9:58 上午
 */
public class ClipboardUtils {
    public static void sendToClipboard(String content) {
        //获取系统剪切板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //构建String数据类型
        StringSelection selection = new StringSelection(content);
        //添加文本到系统剪切板
        clipboard.setContents(selection, null);
    }
}
