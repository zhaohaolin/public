/**
 * 
 */
package edu.hziee.common.serialization.kv.codec;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Transformer;
import edu.hziee.common.serialization.kv.KVUtils;
import edu.hziee.common.serialization.kv.annotation.KeyValueAttribute;
import edu.hziee.common.serialization.kv.context.DecContext;
import edu.hziee.common.serialization.kv.context.DecContextFactory;
import edu.hziee.common.serialization.kv.context.DefaultDecContextFactory;
import edu.hziee.common.serialization.kv.context.DefaultEncContextFactory;
import edu.hziee.common.serialization.kv.context.EncContext;
import edu.hziee.common.serialization.kv.context.EncContextFactory;

/**
 * @author archie
 * 
 */
public class DefaultKVCodec implements KVCodec {

  private static final Logger                            logger            = LoggerFactory.getLogger(DefaultKVCodec.class);

  private DecContextFactory                              decContextFactory = new DefaultDecContextFactory();
  private EncContextFactory                              encContextFactory = new DefaultEncContextFactory();

  private Transformer<String, Map<String, List<String>>> urlDecoded2KV     = new UrlDecoded2KV();
  private Transformer<Map<String, List<String>>, String> kvEncoded2Url     = new KVEncoded2Url();

  @Override
  public Object decode(DecContext ctx) {
    String string = ctx.getDecString();
    Class<?> clazz = ctx.getDecClass();

    Object target = null;

    try {
      target = clazz.newInstance();

      Map<String, List<String>> from = urlDecoded2KV.transform(string);

      Field[] fields = KVUtils.getKVFieldsOf(target.getClass());

      for (Field field : fields) {
        KeyValueAttribute param = field.getAnnotation(KeyValueAttribute.class);

        if (null == param) {
          // not KVAttribute
          continue;
        }

        String key = "".equals(param.key()) ? field.getName() : param.key();
        List<String> values = from.get(key);

        if (null != values && !values.isEmpty()) {

          if (logger.isTraceEnabled()) {
            logger.trace("found field[" + field + "]");
          }

          try {
            Class<?> fieldType = field.getType();
            Class<?> kvType = fieldType;
            if (fieldType.isArray()) {
              kvType = fieldType.getComponentType();
            }

            StringConverter converter = ctx.getConverterOf(kvType);
            if (null != converter) {
              Object v = null;
              if (!fieldType.isArray()) {
                v = converter.transform(values.get(0));
              } else {
                v = Array.newInstance(kvType, values.size());
                int idx = 0;
                for (String e : values) {
                  Array.set(v, idx++, converter.transform(e));
                }
              }
              if (logger.isDebugEnabled()) {
                logger.debug(",and set value[" + v + "]");
              }

              field.setAccessible(true);
              field.set(target, v);
            }
          } catch (Exception e) {
            logger.error("convert", e);
          }
        }
      }
    } catch (InstantiationException e) {
      logger.error("KVCodec:", e);
    } catch (IllegalAccessException e) {
      logger.error("KVCodec:", e);
    }

    return target;
  }

  @Override
  public String encode(EncContext ctx) {
    Object bean = ctx.getEncObject();
    if (null == bean) {
      return "";
    }

    Field[] fields = KVUtils.getKVFieldsOf(bean.getClass());

    Map<String, List<String>> from = new HashMap<String, List<String>>();
    for (Field field : fields) {

      KeyValueAttribute param = field.getAnnotation(KeyValueAttribute.class);

      if (null == param) {
        // not KVAttribute
        continue;
      }

      Class<?> fieldType = field.getType();
      field.setAccessible(true);
      Object fieldValue = null;

      try {
        fieldValue = field.get(bean);

        List<String> value = null;
        if (!fieldType.isArray()) {
          value = new ArrayList<String>(1);
          value.add(String.valueOf(fieldValue));
        } else {
          value = new ArrayList<String>();
          for (int idx = 0; idx < Array.getLength(fieldValue); idx++) {
            Object e = Array.get(fieldValue, idx);
            value.add(String.valueOf(e));
          }
        }

        String key = "".equals(param.key()) ? field.getName() : param.key();
        from.put(key, value);
      } catch (IllegalArgumentException e) {
        logger.error("KVCodec:", e);
      } catch (IllegalAccessException e) {
        logger.error("KVCodec:", e);
      }
    }

    return kvEncoded2Url.transform(from);
  }

  public Transformer<String, Map<String, List<String>>> getUrlDecoded2KV() {
    return urlDecoded2KV;
  }

  public void setUrlDecoded2KV(Transformer<String, Map<String, List<String>>> urlDecoded2KV) {
    this.urlDecoded2KV = urlDecoded2KV;
  }

  public Transformer<Map<String, List<String>>, String> getKvEncoded2Url() {
    return kvEncoded2Url;
  }

  public void setKvEncoded2Url(Transformer<Map<String, List<String>>, String> kvEncoded2Url) {
    this.kvEncoded2Url = kvEncoded2Url;
  }

  @Override
  public DecContextFactory getDecContextFactory() {
    return this.decContextFactory;
  }

  public void setDecContextFactory(DecContextFactory decContextFactory) {
    this.decContextFactory = decContextFactory;
  }

  @Override
  public EncContextFactory getEncContextFactory() {
    return encContextFactory;
  }

  public void setEncContextFactory(EncContextFactory encContextFactory) {
    this.encContextFactory = encContextFactory;
  }

}
