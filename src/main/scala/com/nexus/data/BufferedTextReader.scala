package com.nexus.data

import java.io.Reader

/**
 * A more efficient way of reading strings from a reader.
 *
 * @author jk-5
 */
class BufferedTextReader(private final val reader: Reader, private final val _buffersize: Int = 1024) {
  if(this._buffersize < 0) throw new IllegalArgumentException("Illegal Buffersize: " + this._buffersize)

  private var buffer = new Array[Char](this._buffersize)
  private var line = 1
  private var start = -1
  private var offset: Int = _
  private var index: Int = _
  private var fill: Int = _
  private var lineOffset: Int = _
  private var atNewLine: Boolean = _

  def getIndex = this.offset + this.index
  def getLine = this.line
  def getColumn = this.getIndex - this.lineOffset

  def read(): Int = {
    if(this.fill == -1) return -1
    if(this.index == this.fill){
      if(this.fill == this.buffer.length){
        if(this.start == -1){
          this.advanceBuffer()
        }else if(this.start == 0){
          expandBuffer()
        }else{
          shiftBuffer()
        }
      }
      fillBuffer()
      if(fill == -1) return -1
    }
    if(this.atNewLine) {
      line += 1
      lineOffset = this.getIndex
    }
    val current = this.buffer(this.index)
    this.index += 1
    this.atNewLine = current == '\n'
    current
  }

  def startCapture() = this.start = this.index - 1
  def endCapture: String = {
    if(this.start == -1 ) return ""
    val end = if(this.fill == -1) this.index else this.index - 1
    val recorded = new String(this.buffer, this.start, end - this.start)
    this.start = -1
    recorded
  }

  private def advanceBuffer(){
    this.offset += this.fill
    this.fill = 0
    this.index = 0
  }

  private def shiftBuffer(){
    this.offset += this.start
    this.fill -= this.start
    System.arraycopy(this.buffer, this.start, this.buffer, 0, this.fill)
    this.index = this.fill
    this.start = 0
  }

  private def expandBuffer(){
    val newBuffer = new Array[Char](this.buffer.length * 2)
    System.arraycopy(buffer, 0, newBuffer, 0, fill)
    this.buffer = newBuffer
  }

  private def fillBuffer(){
    val read = reader.read(buffer, fill, buffer.length - fill)
    if(read == -1) this.fill = -1
    else fill += read
  }
}
