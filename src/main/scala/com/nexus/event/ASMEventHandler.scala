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

import com.google.common.collect.Maps
import java.lang.reflect.Method
import java.io.File
import java.util
import java.io.FileOutputStream
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.{MethodVisitor, ClassWriter, Opcodes, Type}

/**
 * No description given
 *
 * @author jk-5
 */
object ASMEventHandler {
  private var IDs = 0
  private final val HANDLER_DESC = Type.getInternalName(classOf[IGeneratedEventListener])
  private final val HANDLER_FUNC_DESC = Type.getMethodDescriptor(classOf[IEventListener].getDeclaredMethods()(0))
  private final val HANDLER_GET_FUNC_DESC = Type.getMethodDescriptor(classOf[IGeneratedEventListener].getDeclaredMethods()(0))
  private final val LOADER = new ASMClassLoader()
  private final val CACHE: util.Map[Method, Class[_ <: IGeneratedEventListener]] = Maps.newHashMap()
  private final val SAVE_GENERATED_CLASSES = true
  private final val OUTPUT_DIR = new File("generated")
  private def getNextID:Int = {
    this.IDs += 1
    IDs
  }
  private class ASMClassLoader() extends ClassLoader(classOf[ASMClassLoader].getClassLoader) {
    def define(name:String, data:Array[Byte]):Class[_ <: IGeneratedEventListener] = this.defineClass(name, data, 0, data.length).asInstanceOf[Class[_ <: IGeneratedEventListener]]
  }
}

class ASMEventHandler(target:Any, method:Method) extends IGeneratedEventListener with Opcodes {

  private final val handler:IEventListener = this.createWrapper(method).getConstructor(classOf[Any]).newInstance(target.asInstanceOf[Object]).asInstanceOf[IEventListener]
  private final val subInfo:EventListener = method.getAnnotation(classOf[EventListener])

  override def invoke(event:Event){
    if(handler != null && (!event.isCancelable || !event.isCanceled || this.subInfo.receiveCancelled())){
      try{
        handler.invoke(event)
      }catch{case e:ClassCastException =>}
    }
  }
  def getPriority:EventPriority = this.subInfo.priority

  def createWrapper(callback:Method):Class[_ <: IGeneratedEventListener] = {
    if(ASMEventHandler.CACHE.containsKey(callback)) return ASMEventHandler.CACHE.get(callback)

    val cw = new ClassWriter(0)
    var mv:MethodVisitor = null

    val name = this.getUniqueName(callback)
    val desc = name.replace('.', '/')
    val instType = Type.getInternalName(callback.getDeclaringClass)
    val eventType = Type.getInternalName(callback.getParameterTypes()(0))

    /*Add:
     *	import com.nexus.event.events.PlaylistEvent.Open;
     *	import com.nexus.playlist.PlaylistManager;
     *
     *	public class ASMEventHandler_0_PlaylistManager_OnOpenPlaylist_Open implements IEventListener{
     *
     *		public PlaylistManager instance;
     *
     *		public ASMEventHandler_0_PlaylistManager_OnOpenPlaylist_Open(PlaylistManager paramObject){
     *			instance = paramObject;
     *		}
     *
     *		public void invoke(Event paramEvent){
     *			instance.OnOpenPlaylist((PlaylistEvent.Open)paramEvent);
     *		}
     *
     *		public Object getTarget(){
     *			return instance;
     *		}
     *	}
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

  private def getUniqueName(callback:Method):String = "com.nexus.event.dynamic.EventHandlerBridge_%d_%s_%s_%s".format(ASMEventHandler.getNextID, callback.getDeclaringClass.getSimpleName, callback.getName, callback.getParameterTypes()(0).getSimpleName)
  override def getTarget:IEventListener = this.handler
}