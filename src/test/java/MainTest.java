import com.gitee.hperfect.task.windows.AddTaskPanel;
import com.gitee.hperfect.utils.YapiTypeUtils;

import javax.swing.*;

/**
 * @author huanxi
 * @version 1.0
 * @date 2021/1/22 5:20 下午
 */
public class MainTest {

    private static void createAndShowGUI() {
        //创建一个漂亮的外观风格
        JFrame.setDefaultLookAndFeelDecorated(true);
        //创建及设置窗口
        JFrame frame = new JFrame("title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new AddTaskPanel());

        //显示窗口
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println(YapiTypeUtils.NORMAL_TYPES.containsKey("Object"));
     /*   YapiUploadService yapiUploadService = new YapiUploadService( new YapiConfig());
        ApiParser apiParser = new ApiParser();
        apiParser.setCatDesc("测试控制器描述");
        apiParser.setCatName("测试控制器-test");
        apiParser.setApis(JSONUtil.toList(JSONUtil.parseArray("[{\"name\":\"测试2Post\",\"path\":\"/test2/test2\",\"formParams\":[{\"name\":\"ids\",\"type\":\"java.lang.String\",\"desc\":\"\",\"required\":false}],\"method\":\"POST\",\"desc\":\"测试\",\"returnType\":{\"paramModelList\":[{\"type\":\"array\",\"paramModelList\":[{\"type\":\"java.lang.String\",\"required\":false}],\"required\":false},{\"type\":\"array\",\"paramModelList\":[{\"type\":\"java.lang.String\",\"required\":false}],\"required\":false},{\"type\":\"array\",\"paramModelList\":[{\"type\":\"java.lang.String\",\"required\":false}],\"required\":false},{\"type\":\"array\",\"paramModelList\":[{\"type\":\"java.lang.String\",\"required\":false}],\"required\":false},{\"type\":\"array\",\"paramModelList\":[{\"type\":\"java.lang.String\",\"required\":false}],\"required\":false}],\"required\":false}}]"), ApiModel.class));
        yapiUploadService.upload(apiParser);*/
    }
}
