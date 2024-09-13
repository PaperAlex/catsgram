package ru.yandex.practicum.catsgram.exception;

public class ImageFileException extends RuntimeException {

    public ImageFileException(String message) {
        super(message);
    }

    public ImageFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageFileException(Throwable cause) {
        super(cause);
    }

    public ImageFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
