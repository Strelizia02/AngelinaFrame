package top.angelinaBot.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

@JsonAutoDetect
public class JsonResult<T> extends BaseVO implements Serializable {

    private static final long serialVersionUID = -5819736795237643687L;

    private Boolean success = true;

    private String code;

    private String message;

    private T data;

    private Integer count;

    private Long time = System.currentTimeMillis();

    public JsonResult(T data) {
        this.data = data;
    }

    public JsonResult() {
    }

    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> resp = new JsonResult<T>();
        resp.setSuccess(true);
        resp.setData(data);
        resp.setCode("200");
        return resp;
    }

    public static <T> JsonResult<T> success(T data, Integer count) {
        JsonResult<T> resp = new JsonResult<T>();
        resp.setSuccess(true);
        resp.setData(data);
        resp.setCode("200");
        resp.setCount(count);
        return resp;
    }

    public static <T> JsonResult<T> success() {
        JsonResult<T> resp = new JsonResult<T>();
        resp.setSuccess(true);
        return resp;
    }

    public static <T> JsonResult<T> failure() {
        JsonResult<T> resp = new JsonResult<T>();
        resp.setSuccess(false);
        return resp;
    }


    public static <T> JsonResult<T> failureWithCode(String code, String message) {
        JsonResult<T> resp = new JsonResult<T>();
        resp.setSuccess(false);
        resp.setCode(code);
        resp.setMessage(message);
        return resp;
    }

    public static <T> JsonResult<T> failureWithMessage(String message) {
        JsonResult<T> resp = new JsonResult<T>();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
