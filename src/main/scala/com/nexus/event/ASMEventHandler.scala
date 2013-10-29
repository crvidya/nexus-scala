/*
 * Copyright 2013 TeamNexus
 *
 * TeamNexus Licenses this file to you under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License
 */

package com.nexus.event

import java.lang.reflect.Method
import java.io.File
import java.io.FileOutputStream
import org.objectweb.asm.{MethodVisitor, ClassWriter, Type}
import org.objectweb.asm.Opcodes._
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable

/**
 * This class will generate bridge classes for the event handlers, instead of calling the listeners with reflection
 * This is much more efficient
 *
 * @author jk-5
 */
object ASMEventHandler {
  private final val nextID = new AtomicInteger(0)
  private final val HANDLER_DESC = Type.getInternalName(classOf[IGeneratedEventListener])
  private final val HANDLER_FUNC_DESC = Type.getMethodDescriptor(classOf[IEventListener].getDeclaredMethods()(0))
  private final val HANDLER_GET_FUNC_DESC = Type.getMethodDescriptor(classOf[IGeneratedEventListener].getDeclaredMethods()(0))
  private final val LOADER = new ASMClassLoader()
  private final val CACHE = mutable.HashMap[Method, Class[_ <: IGeneratedEventListener]]()
  private final val SAVE_GENERATED_CLASSES = false
  private final val OUTPUT_DIR = new File("generated")
  private class ASMClassLoader() extends ClassLoader(classOf[ASMClassLoader].getClassLoader) {
    def define(name:String, data:Array[Byte]):Class[_ <: IGeneratedEventListener] = this.defineClass(name, data, 0, data.length).asInstanceOf[Class[_ <: IGeneratedEventListener]]
  }
}

class ASMEventHandler(target:Any, method:Method) extends IGeneratedEventListener {

  private final val handler = this.createWrapper(method).getConstructor(classOf[Any]).newInstance(target.asInstanceOf[AnyRef]).asInstanceOf[IEventListener]
  private final val subInfo = method.getAnnotation(classOf[EventHandler])

  override def invoke(event:Event){
    if(handler != null && (!event.isCancelable || !event.isCanceled || this.subInfo.receiveCanceled)){
      try{
        handler.invoke(event)
      }catch{case e:ClassCastException =>}
    }
  }
  def getPriority:EventPriority = this.subInfo.priority

  def createWrapper(callback:Method):Class[_ <: IGeneratedEventListener] = {
    if(ASMEventHandler.CACHE.get(callback).isDefined) return ASMEventHandler.CACHE.get(callback).get

    val cw = new ClassWriter(0)
    var mv: MethodVisitor = null

    val name = this.getUniqueName(callback)
    val desc = name.replace('.', '/')
    val instType = Type.getInternalName(callback.getDeclaringClass)
    val eventType = Type.getInternalName(callback.getParameterTypes()(0))

    /*Add:
     *        package com.nexus.event.dynamic
     *
     *        import com.nexus.event.events.PlaylistEvent.Open;
     *        import com.nexus.playlist.PlaylistManager;
     *
     *        public class EventHandlerBridge_0_PlaylistManager_OnOpenPlaylist_Open implements IEventListener{
     *
     *                public PlaylistManager instance;
     *
     *                public ASMEventHandler_0_PlaylistManager_OnOpenPlaylist_Open(PlaylistManager paramObject){
     *                        instance = paramObject;
     *                }
     *
     *                public void invoke(Event paramEvent){
     *                        instance.OnOpenPlaylist((PlaylistEvent.Open)paramEvent);
     *                }
     *
     *                public Object getTarget(){
     *                        return instance;
     *                }
     *        }
     */

    cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", Array[String](ASMEventHandler.HANDLER_DESC))

    cw.visitSource("NEXUS-EVENTBUS", null)
    cw.visitField(ACC_PUBLIC, "instance", "L" + instType + ";", null, null).visitEnd()

    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null)
    mv.visitCode()
    mv.visitVarInsn(ALOAD, 0)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
    mv.visitVarInsn(ALOAD, 0)
    mv.visitVarInsn(ALOAD, 1)
    mv.visitTypeInsn(CHECKCAST, instType)
    mv.visitFieldInsn(PUTFIELD, desc, "instance", "L" + instType + ";")
    mv.visitInsn(RETURN)
    mv.visitMaxs(2, 2)
    mv.visitEnd()

    mv = cw.visitMethod(ACC_PUBLIC, "invoke", ASMEventHandler.HANDLER_FUNC_DESC, null, null)
    mv.visitCode()
    mv.visitVarInsn(ALOAD, 0)
    mv.visitFieldInsn(GETFIELD, desc, "instance", "L" + instType + ";")
    mv.visitVarInsn(ALOAD, 1)
    mv.visitTypeInsn(CHECKCAST, eventType)
    mv.visitMethodInsn(INVOKEVIRTUAL, instType, callback.getName, Type.getMethodDescriptor(callback))
    mv.visitInsn(RETURN)
    mv.visitMaxs(2, 2)
    mv.visitEnd()

    mv = cw.visitMethod(ACC_PUBLIC, "getTarget", ASMEventHandler.HANDLER_GET_FUNC_DESC, null, null)
    mv.visitCode()
    mv.visitVarInsn(ALOAD, 0)
    mv.visitFieldInsn(GETFIELD, desc, "instance", "L" + instType + ";")
    mv.visitInsn(ARETURN)
    mv.visitMaxs(1, 1)
    mv.visitEnd()
    cw.visitEnd()

    if(ASMEventHandler.SAVE_GENERATED_CLASSES){
      if(!ASMEventHandler.OUTPUT_DIR.exists()) ASMEventHandler.OUTPUT_DIR.mkdir()
      try{
        val fos = new FileOutputStream(new File(ASMEventHandler.OUTPUT_DIR, name + ".class"))
        fos.write(cw.toByteArray)
        fos.close()
      }catch{case e:Exception =>}
    }

    val cl = ASMEventHandler.LOADER.define(name, cw.toByteArray)
    ASMEventHandler.CACHE.put(callback, cl)
    cl
  }

  private def getUniqueName(callback:Method) = "com.nexus.event.dynamic.EventHandlerBridge_%d_%s_%s_%s".format(ASMEventHandler.nextID.getAndIncrement, callback.getDeclaringClass.getSimpleName, callback.getName, callback.getParameterTypes()(0).getSimpleName)
  override def getTarget = this.handler
}
