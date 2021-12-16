package com.gitee.hperfect.yapi.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.gitee.hperfect.settings.AppSettingsState;
import com.gitee.hperfect.utils.ClipboardUtils;
import com.gitee.hperfect.utils.MessageUtils;
import com.gitee.hperfect.yapi.dto.*;
import com.gitee.hperfect.yapi.model.ApiCat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gitee.hperfect.utils.YapiTypeUtils;
import com.gitee.hperfect.yapi.config.Yapis;
import com.gitee.hperfect.yapi.model.ApiModel;
import com.gitee.hperfect.yapi.model.ApiParamModelNode;
import com.intellij.openapi.project.Project;

import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

/**
 * 上传到yapi
 *
 * @author huanxi
 * @version 1.0
 * @date 2021/2/3 12:30 下午
 */
public class YapiUploadService {

    private final AppSettingsState settings;

    public YapiUploadService(Project project) {
        this.settings = AppSettingsState.getInstance(project);
    }

    private final Gson gson = new Gson();

    public void upload(ApiCat apiCat) {
        if (!settings.validateYapi()) {
            MessageUtils.error("请在 设置->tools->yapi项目配置 中配置相关属性");
            return;
        }

        //创建或获取分类
        Integer catId = this.getOrCreateCat(apiCat.getCatName(), apiCat.getCatDesc());
        //上传api
        List<ApiModel> apis = apiCat.getApiModelList();
        int success = 0;
        Integer lastId = null;
        if (CollUtil.isNotEmpty(apis)) {
            //创建api
            for (ApiModel api : apis) {
                ApiDto apiDto = new ApiDto();
                apiDto.setCatId(catId);
                apiDto.setToken(settings.getYapiToken());
                apiDto.setMethod(api.getMethod());
                apiDto.setTitle(api.getName());
                apiDto.setPath(api.getPath());
                apiDto.setDesc(api.getDesc());

                //query参数
                List<ApiParamModelNode> formParams = api.getFormParams();
                apiDto.setReqQuery(toApiParamDtos(formParams));
                ApiParamModelNode returnType = api.getReturnType();
                if (returnType != null) {
                    apiDto.setResBody(gson.toJson(toYapiTypeDto(returnType)));
                }
                apiDto.setReqBodyOther(gson.toJson(toYapiTypeDto(api.getBodyParams())));
                String resp = HttpUtil.post(settings.getYapiHost() + Yapis.SAVE, gson.toJson(apiDto), 1000);
                if (YapiResponse.isSuccess(resp)) {
                    success++;
                    Type type = new TypeToken<YapiResponse<List<SaveApiVo>>>() {
                    }.getType();
                    YapiResponse<List<SaveApiVo>> vos = gson.fromJson(resp, type);
                    if (CollUtil.isNotEmpty(vos.getData())) {
                        lastId = vos.getData().get(0).getId();
                    }
                }
            }
        }
        if (apis.size() == 1 && lastId == null) {
            upload(apiCat);
            return;
        }
        MessageUtils.info(String.format("总共解析接口:%d个,成功上传:%d个,api已复制到剪切板", apis.size(), success));
        if (lastId != null) {
            ClipboardUtils.sendToClipboard(String.format("%s/project/%s/interface/api/%s", settings.getYapiHost(), settings.getYapiProjectId(), lastId));
        } else if (catId != null) {
            ClipboardUtils.sendToClipboard(String.format("%s/project/%s/interface/api/cat_%s", settings.getYapiHost(), settings.getYapiProjectId(), catId));
        }
    }

    /**
     * 数组处理
     *
     * @param paramModelNode
     * @return
     */
    private YapiTypeDto toYapiTypeDto(ApiParamModelNode paramModelNode) {
        YapiTypeDto yapiTypeDto = new YapiTypeDto();
        if (paramModelNode != null) {
            String type = YapiTypeUtils.toYapiType(paramModelNode.getType());
            yapiTypeDto.setType(type);
            if ("array".equals(type)) {
                if (CollUtil.isNotEmpty(paramModelNode.getParamModelList())) {
                    yapiTypeDto.setItems(toYapiTypeDto(paramModelNode.getParamModelList().get(0)));
                } else {
                    YapiTypeDto dto = new YapiTypeDto();
                    dto.setType("object");
                    yapiTypeDto.setItems(dto);
                }

            } else {
                yapiTypeDto.setTitle(paramModelNode.getName());
                yapiTypeDto.setDescription(paramModelNode.getDesc());
                List<ApiParamModelNode> paramModelList = paramModelNode.getParamModelList();
                if (CollUtil.isNotEmpty(paramModelList)) {
                    Map<String, YapiTypeDto> dtoMap = new HashMap<>();
                    //子属性
                    for (ApiParamModelNode apiParamModelNode : paramModelList) {
                        if (StrUtil.isNotBlank(apiParamModelNode.getName())) {
                            dtoMap.put(apiParamModelNode.getName(), toYapiTypeDto(apiParamModelNode));
                        }
                    }
                    yapiTypeDto.setProperties(dtoMap);
                }
            }

        }

        return yapiTypeDto;
    }

    private List<ApiParamDto> toApiParamDtos(List<ApiParamModelNode> formParams) {
        List<ApiParamDto> apiParamDtoList = new ArrayList<>();
        for (ApiParamModelNode formParam : formParams) {
            if (CollUtil.isNotEmpty(formParam.getParamModelList())) {
                apiParamDtoList.addAll(toApiParamDtos(formParam.getParamModelList()));
            } else if (StrUtil.isNotBlank(formParam.getName())) {
                apiParamDtoList.add(toParamDto(formParam));
            }
        }
        return apiParamDtoList;
    }

    private ApiParamDto toParamDto(ApiParamModelNode formParam) {
        ApiParamDto apiParamDto = new ApiParamDto();
        apiParamDto.setDesc(formParam.getDesc());
        apiParamDto.setName(formParam.getName());
        apiParamDto.setRequired(formParam.isRequired() ? "1" : "0");
        apiParamDto.setExample(formParam.getExample());
        return apiParamDto;
    }

    private Integer getOrCreateCat(String catName, String catDesc) {
        //空分类
        if (StrUtil.isNotBlank(catName)) {
            //获取所有分类
            String resp = HttpUtil.createGet(settings.getYapiHost() + Yapis.GET_CAT_MENU)
                    .form("project_id", settings.getYapiProjectId())
                    .form("token", settings.getYapiToken())
                    .execute()
                    .body();
            Gson gson = new Gson();
            Type type = new TypeToken<YapiResponse<List<CatDto>>>() {
            }.getType();
            YapiResponse<List<CatDto>> response = gson.fromJson(resp, type);
            Optional<CatDto> any = response.getData().stream().filter(i -> i.getName().equals(catName)).findAny();
            if (any.isPresent()) {
                CatDto catDto = any.get();
                if (StrUtil.isNotBlank(catDesc) && !catDesc.equals(catDto.getDesc())) {
                    //todo 更新
                }
                return catDto.getId();
            } else {
                //创建
                CatDto cat = createCat(catName, catDesc);
                if (cat != null) {
                    return cat.getId();
                }
            }
        }
        return null;
    }

    public CatDto createCat(String name, String desc) {
        String resp = HttpUtil.createPost(settings.getYapiHost() + Yapis.ADD_CAT)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form("project_id", settings.getYapiProjectId())
                .form("token", settings.getYapiToken())
                .form("name", name)
                .form("desc", desc)
                .execute()
                .body();
        if (YapiResponse.isSuccess(resp)) {
            Gson gson = new Gson();
            Type type = new TypeToken<YapiResponse<CatDto>>() {
            }.getType();
            YapiResponse<CatDto> response = gson.fromJson(resp, type);
            return response.getData();
        }
        return null;
    }
}
