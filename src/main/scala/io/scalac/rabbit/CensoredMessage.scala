package io.scalac.rabbit

sealed trait ActorMsg {
  def msg: String
}

sealed trait CensoredMessage {
  def message: String
}
case class MessageSafe(message: String) extends CensoredMessage
case class MessageThreat(message: String) extends CensoredMessage
case class InitMsg(msg: String) extends ActorMsg
case class TermMsg(msg: String) extends ActorMsg