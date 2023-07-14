package work.gaigeshen.formwork.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.gaigeshen.formwork.basal.upload.Upload;
import work.gaigeshen.formwork.basal.upload.UploadResult;
import work.gaigeshen.formwork.basal.upload.Uploader;
import work.gaigeshen.formwork.basal.web.Result;
import work.gaigeshen.formwork.basal.web.Results;

import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
@Tag(name = "文件接口", description = "用于文件上传等操作")
@RequestMapping("/file")
@RestController
public class FileController {

    private final Uploader uploader;

    public FileController(Uploader uploader) {
        this.uploader = uploader;
    }

    @Operation(summary = "上传文件", parameters = {
            @Parameter(name = "file", description = "文件", required = true),
            @Parameter(name = "bucket", description = "上传到的目标桶", required = true),
            @Parameter(name = "object", description = "完整名称可以包含多级文件夹", required = true)
    })
    @PostMapping("/upload")
    public Result<?> upload(@RequestPart("file") MultipartFile file,
                            @RequestParam String bucket,
                            @RequestParam String object) throws IOException {
        String contentType = file.getContentType();
        Upload upload = Upload.builder().bucket(bucket).object(object)
                .contentType(Objects.isNull(contentType) ? "application/octet-stream": contentType)
                .inputStream(file.getInputStream())
                .build();
        UploadResult uploadResult = uploader.upload(upload);
        return Results.create(uploadResult);
    }
}
