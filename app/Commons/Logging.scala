package Commons

import play.api.Logger

trait Logging {
  @transient protected lazy val logger: Logger = Logger(this.getClass)
}
