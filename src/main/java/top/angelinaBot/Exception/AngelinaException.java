package top.angelinaBot.Exception;

/**
 * 自定义运行时异常类，遇到框架相关的错误请抛出这个异常
 */
public class AngelinaException extends RuntimeException {

    public AngelinaException() {}

    public AngelinaException(String message){
        super(message);
    }
}
