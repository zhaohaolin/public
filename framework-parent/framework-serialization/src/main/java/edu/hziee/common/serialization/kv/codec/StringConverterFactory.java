/**
 * 
 */
package edu.hziee.common.serialization.kv.codec;


/**
 * @author Administrator
 * 
 */
public interface StringConverterFactory {
  StringConverter getCodecOf(Class<?> clazz);
}
