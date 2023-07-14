package work.gaigeshen.formwork.basal.upload;

/**
 * 文件上传结果
 *
 * @author gaigeshen
 */
public class UploadResult {

    /**
     * 访问文件用的服务器地址
     */
    private final String serverHost;

    /**
     * 文件所在桶
     */
    private final String bucket;

    /**
     * 文件名称可能包含多级文件夹
     */
    private final String object;

    public UploadResult(String serverHost, String bucket, String object) {
        this.serverHost = serverHost;
        this.bucket = bucket;
        this.object = object;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getBucket() {
        return bucket;
    }

    public String getObject() {
        return object;
    }
}
