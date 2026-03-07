package io.github.ackeecz.danger.lint

import systems.danger.kotlin.sdk.DangerContext
import systems.danger.kotlin.sdk.Violation

internal class FakeDangerContext : DangerContext {

    override var fails: MutableList<Violation> = mutableListOf()
    override val markdowns: MutableList<Violation> = mutableListOf()
    override val messages: MutableList<Violation> = mutableListOf()
    override val warnings: MutableList<Violation> = mutableListOf()

    override fun fail(message: String) {
        fails.add(Violation(message))
    }

    override fun fail(message: String, file: String, line: Int) {
        fails.add(Violation(message, file, line))
    }

    override fun markdown(message: String) {
        markdowns.add(Violation(message))
    }

    override fun markdown(message: String, file: String, line: Int) {
        markdowns.add(Violation(message, file, line))
    }

    override fun message(message: String) {
        messages.add(Violation(message))
    }

    override fun message(message: String, file: String, line: Int) {
        messages.add(Violation(message, file, line))
    }

    override fun suggest(code: String, file: String, line: Int) {
        // not implemented
    }

    override fun warn(message: String) {
        warnings.add(Violation(message))
    }

    override fun warn(message: String, file: String, line: Int) {
        warnings.add(Violation(message, file, line))
    }
}
