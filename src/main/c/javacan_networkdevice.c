/**
 * The MIT License
 * Copyright © 2018 Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
#include "common.h"
#include <tel_schich_javacan_linux_LinuxNetworkDevice.h>
#include <net/if.h>
#include <stdbool.h>
#include <string.h>
#include <jni.h>

JNIEXPORT jlong JNICALL Java_tel_schich_javacan_linux_LinuxNetworkDevice_resolveInterfaceName(JNIEnv *env, jclass class, jstring interface_name) {
    const char *ifname = (*env)->GetStringUTFChars(env, interface_name, false);
    unsigned int ifindex = if_nametoindex(ifname);
    if (ifindex == 0) {
    	const char *errMsg = "Failed to resolve the interface: %s";
    	char msg[strlen(errMsg) + strlen(ifname)];
    	sprintf(msg, errMsg, ifname);
        throwLinuxNativeOperationException(env, msg);
    }
    (*env)->ReleaseStringUTFChars(env, interface_name, ifname);
    return ifindex;
}
