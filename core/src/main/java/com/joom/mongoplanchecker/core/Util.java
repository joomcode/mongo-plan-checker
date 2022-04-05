package com.joom.mongoplanchecker.core;

import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.IterableCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

class Util {
  static final CodecRegistry CODEC_REGISTRY =
      CodecRegistries.fromProviders(
          new DocumentCodecProvider(),
          new BsonValueCodecProvider(),
          new ValueCodecProvider(),
          new IterableCodecProvider());
}
