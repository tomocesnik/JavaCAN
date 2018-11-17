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
package tel.schich.javacan.isotp;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ProtocolParameters {

    private static final int SEPARATION_TIME_MAX_MILLIS = 127;
    private static final int SEPARATION_TIME_MICROS_BASE = 0xF0;
    private static final int SEPARATION_TIME_MICROS_FACTOR = 100;
    private static final int BLOCK_SIZE_MAX = 0xFF;

    public static final ProtocolParameters DEFAULT = new ProtocolParameters(false, 0, 0, SECONDS.toMillis(1), SECONDS.toMillis(1));

    public final boolean sendFDFrames;

    public final int inboundBlockSize;
    public final long inboundSeparationTime;

    public final byte inboundSeparationTimeByte;
    public final byte inboundBlockSizeByte;

    public final long outboundTimeout;
    public final long inboundTimeout;

    public ProtocolParameters(boolean sendFDFrames, int inboundBlockSize, long inboundSeparationTimeNanos, long outboundTimeout, long inboundTimeout) {
        if (inboundBlockSize > BLOCK_SIZE_MAX) {
            throw new IllegalArgumentException("The block size can be no more than " + BLOCK_SIZE_MAX + "!");
        }

        this.sendFDFrames = sendFDFrames;

        this.inboundBlockSize = inboundBlockSize;
        this.inboundSeparationTime = inboundSeparationTimeNanos;

        this.inboundBlockSizeByte = (byte)inboundBlockSize;
        this.inboundSeparationTimeByte = nanosToSeparationTimeByte(inboundSeparationTimeNanos);

        this.outboundTimeout = outboundTimeout;
        this.inboundTimeout = inboundTimeout;
    }

    public ProtocolParameters withSendingFDFrames(boolean sendFDFrames) {
        return new ProtocolParameters(sendFDFrames, inboundBlockSize, inboundSeparationTime, outboundTimeout, inboundTimeout);
    }

    public ProtocolParameters withBlockSize(int blockSize) {
        return new ProtocolParameters(sendFDFrames, blockSize, inboundSeparationTime, outboundTimeout, inboundTimeout);
    }

    public ProtocolParameters withSeparationTime(long separationTime) {
        return new ProtocolParameters(sendFDFrames, inboundBlockSize, separationTime, outboundTimeout, inboundTimeout);
    }

    public ProtocolParameters withOutboundTimeout(long outboundTimeout) {
        return new ProtocolParameters(sendFDFrames, inboundBlockSize, inboundSeparationTime, outboundTimeout, inboundTimeout);
    }

    public ProtocolParameters withInboundTimeout(long inboundTimeout) {
        return new ProtocolParameters(sendFDFrames, inboundBlockSize, inboundSeparationTime, outboundTimeout, inboundTimeout);
    }

    public static byte nanosToSeparationTimeByte(long nanos) {
        if (nanos == 0) {
            return 0;
        }
        long micros = TimeUnit.NANOSECONDS.toMicros(nanos);
        if (micros > 1000) {
            long millis = TimeUnit.MICROSECONDS.toMillis(micros);
            if (millis > SEPARATION_TIME_MAX_MILLIS) {
                throw new IllegalArgumentException("The separation time must be no more than " + SEPARATION_TIME_MAX_MILLIS + " milliseconds!");
            }
            return (byte)millis;
        } else {

            int microsCoefficient = (int)(Math.round(micros / (double) SEPARATION_TIME_MICROS_FACTOR));
            if (microsCoefficient == 10) {
                return 1;
            }
            return (byte)(SEPARATION_TIME_MICROS_BASE + microsCoefficient);
        }
    }

    public static long separationTimeByteToNanos(int separationTimeByte) {
        long separationTime = separationTimeByte & 0xFF;
        if (separationTime <= SEPARATION_TIME_MAX_MILLIS) {
            return TimeUnit.MILLISECONDS.toNanos(separationTime);
        } else {
            return TimeUnit.MICROSECONDS.toNanos((separationTime - SEPARATION_TIME_MICROS_BASE) * SEPARATION_TIME_MICROS_FACTOR);
        }
    }
}

