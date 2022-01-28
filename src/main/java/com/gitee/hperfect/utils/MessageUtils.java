package com.gitee.hperfect.utils;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.project.Project;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/10/30 10:14 上午
 */
public class MessageUtils {
    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("y-api-uploader", NotificationDisplayType.BALLOON, true);

    public static void info(Project project, String msg) {
        /*NotificationGroupManager.getInstance().getNotificationGroup("Custom Notification Group")
                .createNotification(msg, MessageType.INFO)
                .notify(project);*/
        Notifications.Bus.notify(NOTIFICATION_GROUP.createNotification(msg, MessageType.INFO));
    }

    public static void error(Project project,String msg) {
        Notifications.Bus.notify(NOTIFICATION_GROUP.createNotification(msg, MessageType.ERROR));
    }
}
