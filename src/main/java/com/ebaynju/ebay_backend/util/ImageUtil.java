
package com.ebaynju.ebay_backend.util;

import com.ebaynju.ebay_backend.controller.GoodsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 处理图像
 * 
 * @author cardigan
 * @version 1.0
 *          Create by 2022/8/21 21:54
 */
public class ImageUtil {
    /**
     * 上传图像
     * 
     * @param multipartFile
     * @param httpServletRequest
     * @return
     * @throws IOException
     */

    private final static Logger logger = LoggerFactory.getLogger(GoodsController.class);

    public static String imgUpload(MultipartFile multipartFile, HttpServletRequest httpServletRequest, int id)
            throws IOException {
        // 获取前端上传的文件名称
        String originalFileName = multipartFile.getOriginalFilename();
        // 取文件名下标，给文件重命名的时候使用
        String suffix = originalFileName.substring(originalFileName.indexOf("."));
        // 取一个随机id给文件重命名使用
        String uuid = UUID.randomUUID().toString();
        // 你的接收的文件新的名字
        String filename = uuid + suffix;
        String uri = httpServletRequest.getSession().getServletContext().getRealPath("/");
        File f = new File(uri + "/" + id);
        f.mkdir();
        // 在项目新建一个你重新生成名称的文件
        File file = new File(uri + "/" + id + "/" + filename);
        // 将接收的到的 multipartFile 类型的文件转为file
        multipartFile.transferTo(file);
        // 获取接收到的并存在项目本地的文件，这样你就可以拿着这个文件随意处理啦
        String filePath = file.getAbsolutePath();

        return filePath;
    }

}
