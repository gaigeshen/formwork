package work.gaigeshen.formwork.basal.upload;

import java.io.InputStream;
import java.util.Objects;

/**
 * 文件上传参数
 *
 * @author gaigeshen
 */
public class Upload {

    /**
     * 文件输入流
     */
    private final InputStream inputStream;

    /**
     * 文件类型
     */
    private final String contentType;

    /**
     * 上传到目标桶
     */
    private final String bucket;

    /**
     * 文件名称可能包含多级文件夹
     */
    private final String object;

    private Upload(Builder builder) {
        this.inputStream = builder.inputStream;
        this.contentType = builder.contentType;
        this.bucket = builder.bucket;
        this.object = builder.object;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBucket() {
        return bucket;
    }

    public String getObject() {
        return object;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private InputStream inputStream;

        private String contentType;

        private String bucket;

        private String object;

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder object(String object) {
            this.object = object;
            return this;
        }

        public Upload build() {
            if (Objects.isNull(inputStream)) {
                throw new IllegalArgumentException("inputStream cannot be null");
            }
            if (Objects.isNull(contentType)) {
                throw new IllegalArgumentException("contentType cannot be null");
            }
            if (Objects.isNull(bucket)) {
                throw new IllegalArgumentException("bucket cannot be null");
            }
            if (Objects.isNull(object)) {
                throw new IllegalArgumentException("object cannot be null");
            }
            return new Upload(this);
        }
    }
}
