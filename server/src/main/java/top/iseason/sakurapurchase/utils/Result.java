package top.iseason.sakurapurchase.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * API结果集
 */
@ApiModel(value = "响应结果", description = "响应封装类")
@Data
@Accessors(chain = true)
public class Result<T> {
    private final static ObjectMapper objectMapper;

    static {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper = mapper;
    }

    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码", example = "200")
    private Integer state;
    @ApiModelProperty(value = "状态信息", example = "请求成功")
    private String message;
    /**
     * 数据 (如果有的话)
     */
    @ApiModelProperty("返回数据(可能不存在)")
    private T data;


    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setState(200)
                .setMessage("请求成功")
                .setData(data);
    }

    public static Result<Object> success() {
        return new Result<Object>().setState(200)
                .setMessage("请求成功");
    }


    public static <T> Result<T> failure(T data) {
        return new Result<T>()
                .setState(999)
                .setMessage("请求失败")
                .setData(data);
    }

    public static <T> Result<T> failure() {
        return new Result<T>()
                .setState(999)
                .setMessage("请求失败");
    }

    public static <T> Result<T> failure(String message) {
        return new Result<T>()
                .setState(999)
                .setMessage(message);
    }

    public static <T> Result<T> of(Integer code, String message, T data) {
        return new Result<T>()
                .setState(code)
                .setMessage(message)
                .setData(data);
    }

    public static <T> Result<T> of(Integer code, String msg) {
        return new Result<T>()
                .setState(code)
                .setMessage(msg);
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}