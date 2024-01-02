package com.app.file.constant;

public class RabbitQueueConstant {

    public static final String UPLOAD_QUEUE = "upload-queue";
    public static final String DELETE_QUEUE = "delete-queue";
    public static final String DELAYED_DELETE_QUEUE = "delayed-delete-queue";
    public static final String MOVE_TO_TRASH_QUEUE = "move-to_trash-queue";

    public static final String RESTORE_QUEUE = "restore-queue";

    public static final String HARD_DELETE_QUEUE = "hard-delete-queue";
    public static final String REAL_DELETE_QUEUE = "real-delete-queue";
    public static final long TTL_30_DAYS = 2592000000L;
}
