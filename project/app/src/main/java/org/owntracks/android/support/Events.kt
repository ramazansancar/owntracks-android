package org.owntracks.android.support

import java.util.*

class Events {
    abstract class E internal constructor() {
        val date: Date = Date()
    }

    class RestartApp : E()
}
