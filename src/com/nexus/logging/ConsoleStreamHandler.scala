package com.nexus.logging

import java.util.logging.StreamHandler
import java.io.OutputStream
import java.util.logging.LogRecord

class ConsoleStreamHandler(private final val out:OutputStream) extends StreamHandler {
	this.setOutputStream(this.out)
	override def close = this.synchronized{this.flush()}
	override def publish(record:LogRecord) = this.synchronized{
		super.publish(record)
		this.flush()
	}
}