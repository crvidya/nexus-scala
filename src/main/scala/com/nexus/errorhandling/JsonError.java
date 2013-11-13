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

package com.nexus.errorhandling;

import com.nexus.data.json.JsonObject;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * No description given
 *
 * @author jk-5
 */
public enum JsonError {
    SERVER_ERROR(1, HttpResponseStatus.INTERNAL_SERVER_ERROR),
    UNHANDLED_METHOD(2, HttpResponseStatus.METHOD_NOT_ALLOWED),
    HANDLER_NOT_FOUND(3, HttpResponseStatus.NOT_FOUND),
    LOGIN_USERNAME_UNDEFINED(10),
    LOGIN_PASSWORD_UNDEFINED(11),
    LOGIN_TFAKEY_UNDEFINED(12),
    LOGIN_USERNAME_WRONG(15),
    LOGIN_PASSWORD_WRONG(16),
    LOGIN_TFAKEY_WRONG(17),
    LOGIN_TFAKEY_NONUMBER(18);

    private final int errorCode;
    private final JsonObject json;
    private final JsonObject errorJson;
    private final JsonErrorException exception;
    private final HttpResponseStatus status;

    private JsonError(int code){
        this(code, HttpResponseStatus.OK);
    }

    private JsonError(int code, HttpResponseStatus status){
        String name = this.name().toLowerCase().replace("_", ".");
        this.errorCode = code;
        this.json = new JsonObject().add("id", code).add("name", name);
        this.errorJson = new JsonObject().add("error", this.json);
        this.exception = new JsonErrorException(this);
        this.status = status;
    }

    public int getErrorCode(){
        return this.errorCode;
    }

    public HttpResponseStatus getStatus(){
        return this.status;
    }

    public JsonObject toJson(){
        return this.json;
    }

    public JsonObject toErrorJson(){
        return this.errorJson;
    }

    public JsonErrorException getException(){
        return this.exception;
    }

    public void throwException() throws JsonErrorException {
        throw this.exception;
    }
}
