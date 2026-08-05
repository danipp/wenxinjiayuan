package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author zane
 */
@Schema(name = "登录入参")
@Data
public class AdminLoginVo {

    @Schema(description = "手机号", name = "cellphone", required = true)
    @NotBlank(message = "手机号不能为空")
    private String cellphone;

    @Schema(description = "密码", name = "passWord", required = true)
    @NotBlank(message = "密码不能为空")
    private String passWord;

    public static void main(String[] args) throws IOException {
        // 设定要处理的文件夹路径
        String parentDirPath = "G:\\新建文件夹";
        File parentDir = new File(parentDirPath);

        // 获取目录下的所有子文件夹
        File[] directories = parentDir.listFiles(File::isDirectory);

        if (directories != null) {
            for (File dir : directories) {
                String dirName = dir.getName();
                // 按照"qsazxswqaz"切割
                String[] parts = dirName.split("qsazxswqaz");

                if (parts.length > 1) {
                    // 提取父文件夹名和后缀
                    String parentName = parts[0];
                    String suffix = parts[1];

                    // 创建新的文件夹对象
                    File newParentDir = new File("G:\\"+parentName);
                    if (!newParentDir.exists()) {
                        // 创建文件夹
                        if (newParentDir.mkdir()) {
                            System.out.println("文件夹创建成功: " + newParentDir);
                        } else {
                            continue;
                        }
                    }
                    try {
                        Files.move(Paths.get("G:\\新建文件夹\\"+dirName), Paths.get("G:\\"+parentName+"\\"+suffix), StandardCopyOption.REPLACE_EXISTING);
                    }catch (Exception e){

                    }
                }
            }
        } else {
            System.out.println("没有找到任何子文件夹。");
        }
    }

}
