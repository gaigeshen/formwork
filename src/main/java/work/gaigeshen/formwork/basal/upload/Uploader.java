package work.gaigeshen.formwork.basal.upload;

/**
 * 用于上传文件用
 *
 * @author gaigeshen
 */
public interface Uploader {

    /**
     * 执行上传文件的操作
     *
     * @param upload 需要上传的文件和其属性
     * @return 上传文件的结果
     * @throws UploadException 上传文件失败
     */
    UploadResult upload(Upload upload) throws UploadException;
}
