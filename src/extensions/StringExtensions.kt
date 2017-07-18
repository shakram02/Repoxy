package network_io

/**
 * Created by ahmed on 7/18/17.
 */
fun String.toBytes(): List<Byte> {
    val msg = "Hello\n".toByteArray()
    val bytes = msg.toList()
    return bytes
}