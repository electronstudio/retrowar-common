package uk.co.electronstudio.retrowar.network

interface isNetworked {
    fun processIncomingMessage(
        obj: Any
    )
}
