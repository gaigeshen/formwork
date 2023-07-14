package work.gaigeshen.formwork.basal.upload;

/**
 * 文件上传失败异常
 *
 * @author gaigeshen
 */
public class UploadException extends RuntimeException {
    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
