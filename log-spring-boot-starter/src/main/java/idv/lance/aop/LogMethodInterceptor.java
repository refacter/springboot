package idv.lance.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class LogMethodInterceptor implements MethodInterceptor {
    private Logger logger = LoggerFactory.getLogger(LogMethodInterceptor.class);
    private List<String> exclude;
    public LogMethodInterceptor(String[] exclude) {
        this.exclude = Arrays.asList(exclude);
    }
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        if(exclude.contains(methodName)) {
            return invocation.proceed();
        }
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long end = System.currentTimeMillis();
        logger.info("call method({}), cost time({}) ", methodName, (end - start));
        return result;
    }
}