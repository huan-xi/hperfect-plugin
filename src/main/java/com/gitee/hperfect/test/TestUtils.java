package com.gitee.hperfect.test;

import com.intellij.psi.PsiElement;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UastContextKt;
import org.jetbrains.uast.UastFacade;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/12/16 11:54 上午
 */
public class TestUtils {

    public void test(PsiElement referenceAt) {
        UElement uElement = UastContextKt.toUElement(referenceAt, UElement.class);
        System.out.println(uElement);
        UElement uElement1 = UastFacade.INSTANCE.convertElementWithParent(referenceAt,
                new Class[]{UClass.class});
        System.out.println("iiii2dfafadsfasdfaf");
        System.out.println(uElement1);
    }
}
