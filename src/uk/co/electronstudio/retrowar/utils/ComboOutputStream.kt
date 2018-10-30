package uk.co.electronstudio.retrowar.utils

import java.io.OutputStream

/**
 * combines two outputsteams into one outputstream
 */
class ComboOutputStream(
    val stream1: OutputStream,
    val stream2: OutputStream
) : OutputStream() {

    override fun write(
        b: Int
    ) {

        stream2.write(
            b
        )
        stream1.write(
            b
        )
    }

    override fun close() {
        stream1.close()
        stream2.close()
    }

    override fun flush() {
        stream1.flush()
        stream2.flush()
    }
}