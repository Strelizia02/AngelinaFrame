package top.angelinaBot.Exception;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * Angelina运行时异常类，遇到Angelina框架相关的错误请抛出这个异常
 **/
public class AngelinaException extends RuntimeException {

    public AngelinaException() {}

    public AngelinaException(String message){
        super(message);
    }
}
