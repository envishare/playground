import com.en.repo.{ElasticRepository, ElasticRepositoryImpl}
import com.en.services.{ActivityRepo, ActivityRepoImpl, EnvishareService, EnvishareServiceImpl, UserRepo, UserRepoImpl}
import com.google.inject.AbstractModule

class Module extends AbstractModule {
  override def configure() = {

    bind(classOf[ElasticRepository])
      .to(classOf[ElasticRepositoryImpl])

    bind(classOf[EnvishareService])
      .to(classOf[EnvishareServiceImpl])

    bind(classOf[ActivityRepo])
      .to(classOf[ActivityRepoImpl])

    bind(classOf[UserRepo])
      .to(classOf[UserRepoImpl])
  }
}