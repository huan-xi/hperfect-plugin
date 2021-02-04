package com.gitee.hperfect.yapi.config;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/2 7:50 下午
 */
public interface AnnotationCons {
    String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    String CONTROLLER = "org.springframework.web.bind.annotation.Controller";
    String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    String API = "io.swagger.annotations.Api";
    String API_OPERATION = "io.swagger.annotations.ApiOperation";
    String API_PARAM = "io.swagger.annotations.ApiParam";
    String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    String API_MODEL_PROPERTY = "io.swagger.annotations.ApiModelProperty";
    String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
}
