

import com.gitee.hperfect.yapi.parse.GenericType;

import java.io.FileNotFoundException;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/25 11:34 上午
 */
public class TestMain2 {


    public static void main(String[] args) throws FileNotFoundException {
        String a = "com.xqcrm.tb.web.common.api.Result<java.util.List<java.lang.String>>";
        GenericType parse = GenericType.parse(a);
        System.out.println(parse);
    }

}
