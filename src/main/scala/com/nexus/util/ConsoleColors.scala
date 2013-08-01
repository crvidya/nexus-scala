/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Jeffrey Kog (jk-5), Martijn Reening (martijnreening)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.nexus.util

object ConsoleColors {

	final val SANE = "\u001B[0m"
	
	final val HIGH_INTENSITY = "\u001B[1m"
	final val LOW_INTESITY = "\u001B[2m"
	
	final val ITALIC = "\u001B[3m"
	final val UNDERLINE = "\u001B[4m"
	final val BLINK = "\u001B[5m"
	final val RAPID_BLINK = "\u001B[6m"
	final val REVERSE_VIDEO = "\u001B[7m"
	final val INVISIBLE_TEXT = "\u001B[8m"
	
	final val BLACK = "\u001B[30m"
	final val RED = "\u001B[31m"
	final val GREEN = "\u001B[32m"
	final val YELLOW = "\u001B[33m"
	final val BLUE = "\u001B[34m"
	final val MAGENTA = "\u001B[35m"
	final val CYAN = "\u001B[36m"
	final val WHITE = "\u001B[37m"
	
	final val BACKGROUND_BLACK = "\u001B[40m"
	final val BACKGROUND_RED = "\u001B[41m"
	final val BACKGROUND_GREEN = "\u001B[42m"
	final val BACKGROUND_YELLOW = "\u001B[43m"
	final val BACKGROUND_BLUE = "\u001B[44m"
	final val BACKGROUND_MAGENTA = "\u001B[45m"
	final val BACKGROUND_CYAN = "\u001B[46m"
	final val BACKGROUND_WHITE = "\u001B[47m"
	
	final val NORMAL = "\u000f"
	final val BOLD = "\u0002"
	final val REVERSE = "\u0016"
	final val DARK_BLUE = "\u000302"
	final val DARK_GREEN = "\u000303"
	final val BROWN = "\u000305"
	final val PURPLE = "\u000306"
	final val OLIVE = "\u000307"
	final val TEAL = "\u000310"
	final val DARK_GRAY = "\u000314"
	final val LIGHT_GRAY = "\u000315"
}
