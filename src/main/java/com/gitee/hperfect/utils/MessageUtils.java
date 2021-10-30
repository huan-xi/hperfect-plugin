package com.gitee.hperfect.utils;

import com.gitee.hperfect.yapi.action.UploadToYapiAction;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/10/30 10:14 上午
 */
public class MessageUtils {
    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("h-api-uploader", NotificationDisplayType.BALLOON, true);

    public static void info(String msg) {
        Notifications.Bus.notify(NOTIFICATION_GROUP.createNotification(msg, MessageType.INFO));
    }

    public static void error(String msg) {
        Notifications.Bus.notify(NOTIFICATION_GROUP.createNotification(msg, MessageType.ERROR));
    }
}
