package idv.lance.starter.interceptor;

import idv.lance.starter.property.Confidential;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.BeanUtils;

@Intercepts({
  @Signature(
      type = ResultSetHandler.class,
      method = "handleResultSets",
      args = {Statement.class})
})
public class ConfidentialInterceptor implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object result = invocation.proceed();
    if (result instanceof Collection) {
      Collection<Object> objList= (Collection) result;
      List<Object> resultList=new ArrayList<>();
      for (Object obj : objList) {
        resultList.add(desensitize(obj));
      }
      return resultList;
    }else {
      return desensitize(result);
    }
  }

  private Object desensitize(Object object) throws InvocationTargetException, IllegalAccessException {
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
      Confidential confidential = field.getAnnotation(Confidential.class);
      if (confidential==null){
        continue;
      }
      PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(object.getClass(), field.getName());
      if (ps.getReadMethod() == null || ps.getWriteMethod() == null) {
        continue;
      }
      Object value = ps.getReadMethod().invoke(object);
      if (value != null) {
        ps.getWriteMethod().invoke(object, "***");
      }
    }
    return object;
  }
}
