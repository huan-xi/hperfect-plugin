package com.gitee.hperfect.yapi.parse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;

import java.util.List;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/2/2 3:31 下午
 */
public class ParseUtils {
    /**
     * 移除双引号
     *
     * @param string
     * @return
     */
    public static String removeDoubleQuotation(String string) {
        return StrUtil.sub(string, 1, string.length() - 1);
    }

    public static String addWithBackslash(String string) {
        if (string == null) {
            return "";
        }
        if (!string.startsWith("/")) {
            string = "/" + string;
        }
        return string;
    }

    public static String getRouteString(String string) {
        return addWithBackslash(removeDoubleQuotation(string));
    }


    public static boolean isHttpMethod(String mapper) {
        // List<String> methods = CollUtil.toList("GetMapping", "PostMapping", "DeleteMapping", "PutMapping");
        return mapper.endsWith("Mapping");
    }


    public static String getPsiAnnotationValueByName(PsiAnnotation annotation, String name) {
        String value = null;
        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
        for (PsiNameValuePair attribute : attributes) {
            boolean isValue = (attribute.getName() == null && name == null) || (name == null && "value".equals(attribute.getName()));
            if (isValue || (name != null && name.equals(attribute.getName()))) {
                value = attribute.getLiteralValue();
            }
        }
        return value;
    }

    public static String getPsiAnnotationValue(PsiAnnotation annotation) {
        return getPsiAnnotationValueByName(annotation, null);
    }

    public static String getJavaDoc(String comment) {
        StringBuilder builder = new StringBuilder();
        for (char c : comment.toCharArray()) {
            if ('*' != c && '/' != c & '\t' != c && '\n' != c) {
                if (c == '@') {
                    break;
                }
                builder.append(c);
            }
        }
        return builder.toString().trim();
    }

    public static String getParamDesc(PsiMethod psiMethodTarget, String paramName) {
        if (psiMethodTarget.getDocComment() != null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if ((psiDocTag.getText().contains("@param") || psiDocTag.getText().contains("@Param"))
                        && (!psiDocTag.getText().contains("[")) && psiDocTag.getText().contains(paramName)) {
                    return trimFirstAndLastChar(psiDocTag.getText().replace("@param", "").replace("@Param", "").replace(paramName, "").replace(":", "").replace("*", "")
                            .replace("\n", " "), ' ').trim();
                }
            }
        }
        return "";
    }

    /**
     * 去除字符串首尾出现的某个字符.
     *
     * @param source  源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    public static String trimFirstAndLastChar(String source, char element) {
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do {
            if (Strings.isNullOrEmpty(source.trim()) || source.equals(String.valueOf(element))) {
                source = "";
                break;
            }
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }

    public static String getJavaDoc(PsiDocComment docComment) {
        if (docComment != null) {
            return getJavaDoc(docComment.getText());
        }
        return null;
    }

    public static String removePackage(String typeName) {
        if (StrUtil.isNotBlank(typeName) && typeName.contains(".")) {
            List<String> split = StrUtil.split(typeName, '.');
            typeName = split.get(split.size() - 1);
        }
        return typeName;
    }
}
