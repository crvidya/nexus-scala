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

package com.nexus.util;

import java.lang.Double;
import java.lang.Float;

/**
 * No description given
 *
 * @author jk-5
 */
public class PrimitiveChecks {

    public static boolean isInfinite(float f){
        return Float.isInfinite(f);
    }

    public static boolean isNaN(float f){
        return Float.isNaN(f);
    }

    public static boolean isInfinite(double d){
        return Double.isInfinite(d);
    }

    public static boolean isNaN(double d){
        return Double.isNaN(d);
    }
}
