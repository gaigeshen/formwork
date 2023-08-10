package work.gaigeshen.formwork.basal.retrofit.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import retrofit2.Retrofit;

/**
 * 用于生成服务接口的实现
 *
 * @author gaigeshen
 */
public class RetrofitServiceFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    private final Class<T> serviceInterface;

    private ApplicationContext applicationContext;

    public RetrofitServiceFactoryBean(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() {
        return applicationContext.getBean(Retrofit.class).create(serviceInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }
}
