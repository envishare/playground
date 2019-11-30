package com.en.config

import java.net.InetSocketAddress

import com.en.util.{ElasticConfig, EnConfig}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient

trait ElasticCluster {
  lazy val elasticConfig: ElasticConfig = EnConfig.elasticConfig
  lazy val hosts: Seq[String] = elasticConfig.hosts
  lazy val port: Int = elasticConfig.port
}
