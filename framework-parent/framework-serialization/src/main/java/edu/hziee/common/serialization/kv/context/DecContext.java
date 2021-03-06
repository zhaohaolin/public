package edu.hziee.common.serialization.kv.context;

import edu.hziee.common.serialization.kv.codec.StringConverter;

/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: DecContext.java 14 2012-01-10 11:54:14Z archie $
 */
public interface DecContext {
  Object getDecOwner();
  String getDecString();
  Class<?> getDecClass();

  DecContextFactory getDecContextFactory();

  StringConverter getConverterOf(Class<?> from);
}
