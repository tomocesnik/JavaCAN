/*
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
package tel.schich.javacan.test;

import org.junit.jupiter.api.Test;
import tel.schich.javacan.BcmCanChannel;
import tel.schich.javacan.BcmMessage;
import tel.schich.javacan.CanChannels;
import tel.schich.javacan.CanFrame;

import static org.junit.jupiter.api.Assertions.*;
import static tel.schich.javacan.CanFrame.FD_NO_FLAGS;
import static tel.schich.javacan.test.CanTestHelper.CAN_INTERFACE;

public class BcmCanSocketTest {
	@Test
	void testNonBlockingRead() throws Exception {
		try (final BcmCanChannel socket = CanChannels.newBcmChannel()) {
			socket.connect(CAN_INTERFACE);
			assertTrue(socket.isBlocking(), "Socket is blocking by default");

			final CanFrame input = CanFrame.create(0x7EA, FD_NO_FLAGS, new byte[] { 0x34, 0x52, 0x34 });
			socket.configureBlocking(false);
			assertFalse(socket.isBlocking(), "Socket is non blocking after setting it so");
			CanTestHelper.sendFrameViaUtils(CAN_INTERFACE, input);
			Thread.sleep(50);
			final BcmMessage output = socket.read();
			assertEquals(input, output, "What comes in should come out");
		}
	}
}
