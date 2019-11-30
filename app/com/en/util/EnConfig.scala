package com.en.util

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._

object EnConfig {
  lazy val elasticConfig: ElasticConfig = ElasticConfig(
    config.getConfig("elastic"))
  private lazy val config: Config = ConfigFactory.load()
}

case class ElasticConfig(
                          cluster: String,
                          documentsIndex: String,
                          documentsDocType: String,
                          hosts: Seq[String],
                          port: Int,
                          timeoutSeconds: Duration = 10 seconds
                        )

object ElasticConfig {
  def apply(
             cluster: String,
             documents_index: String,
             documents_doc_type: String,
             hosts: Seq[String],
             port: Int
           ): ElasticConfig =
    new ElasticConfig(cluster, documents_index, documents_doc_type, hosts, port)

  def apply(config: Config): ElasticConfig =
    new ElasticConfig(
      config.getString("cluster"),
      config.getString("documentIndex"),
      config.getString("documentDocType"),
      config.getList("hosts").toArray.toSeq.map(_.toString),
      config.getInt("port")
    )
}
