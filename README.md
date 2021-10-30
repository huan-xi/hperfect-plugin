## 待修复
   1. 对象参数Map<string,Object> 无法解析
   2. 生成js函数

## 计划功能
   1. api备注
   2. api默认值

## 描述

解析springmvc接口到yapi文档,支持swagger(优先)和注释结合

## 使用方法

1. 设置->tools->yapi项目配置 设置yapi项目信息 

   ![image-20210204211931043](https://i.loli.net/2021/02/04/OdEiBJ5C2pYaN8w.png)

2. 分类解析

   1. 通过swagger 注解

      ![image-20210204195954937](https://i.loli.net/2021/02/04/RTJ2c6nSKgaPv5e.png)

   2. 通过注释

      ![image-20210204190706028](https://i.loli.net/2021/02/04/way3xMbrdmSjEIK.png)

3. api解析

   1. 路径只能通过Mapping解析
   2. 通过@ApiOperation或注释解析api名称
   3. 返回类型通过函数返回类型解析

4. api参数解析

   1. 不带@RequestBody解析为表单参数

      1. 数组类型会解析成字符串,描述中会添加传那个类型数组,只支持基本数据类型
      2. 对象类型会将所有字段解析成表单参数

   2. @RequestBody解析为json格式参数, 和返回类型一样

   3. api字段名称从@RequestParam注解中解析，不存在则使用参数名称,描述从@APIParam中解析，不存在则取方法注释,具体用法如下图

      ![image-20210204201031247](https://i.loli.net/2021/02/04/3KuXitsGBD9TSyg.png)

   4. 对象参数解析: 如下图

      ![image-20210204201150466](https://i.loli.net/2021/02/04/KN8OsuUnXYJCpEg.png)

## 说明

1. javaDoc方式解析将新增@tag方式

2. 如有其他解析方式可提issue

3. 解析过程

   1. 控制器class解析分类(ApiCat)

   2. 从class方法中解析api模型(ApiModel)

   3. 从参数中解析参数模型(ApiApiParamModelNode 树形结构的节点)

   4. 再将api模型转成yapi上传数据类型

      > 理论上可以自己写解析器(将接口文件解析成apiModel)可以达到自定义解析的效果
      >
      > 也可以apiModel转换成其他平台接口上传数据(如有需求可以一起探讨)





