package io.github.ackeecz.danger.lint

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Path

internal class PathRelativizerTest : FunSpec({

    val cwd = Path.of("/fake/project/root")

    test("absolute path under CWD is relativized") {
        val underTest = PathRelativizer(cwd)

        val result = underTest.relativize("/fake/project/root/app/src/Foo.kt")

        result shouldBe "app/src/Foo.kt"
    }

    test("relative path is returned unchanged") {
        val underTest = PathRelativizer(cwd)

        val result = underTest.relativize("app/src/Foo.kt")

        result shouldBe "app/src/Foo.kt"
    }

    test("absolute path not under CWD is returned unchanged") {
        val underTest = PathRelativizer(cwd)

        val result = underTest.relativize("/var/lib/gitlab-runner/builds/other/Foo.kt")

        result shouldBe "/var/lib/gitlab-runner/builds/other/Foo.kt"
    }

    test("cross-job path with different build ID is relativized") {
        val crossJobCwd = Path.of("/var/lib/gitlab-runner/builds/YCfGdqxMq/0/Android/skeleton")
        val underTest = PathRelativizer(crossJobCwd)

        val result = underTest.relativize("/var/lib/gitlab-runner/builds/5oEY4qhBW/0/Android/skeleton/feature/src/Foo.kt")

        result shouldBe "feature/src/Foo.kt"
    }

    test("completely unrelated absolute path is returned unchanged") {
        val underTest = PathRelativizer(cwd)

        val result = underTest.relativize("/completely/different/root/Foo.kt")

        result shouldBe "/completely/different/root/Foo.kt"
    }

    test("cross-job path with short CWD tail is relativized") {
        val shortTailCwd = Path.of("/var/builds/YCfGdqxMq/project")
        val underTest = PathRelativizer(shortTailCwd)

        val result = underTest.relativize("/var/builds/5oEY4qhBW/project/src/Foo.kt")

        result shouldBe "src/Foo.kt"
    }

    test("cross-job path ending at CWD tail boundary returns unchanged") {
        val crossJobCwd = Path.of("/var/builds/YCfGdqxMq/0/Android/skeleton")
        val underTest = PathRelativizer(crossJobCwd)

        val result = underTest.relativize("/var/builds/5oEY4qhBW/0/Android/skeleton")

        result shouldBe "/var/builds/5oEY4qhBW/0/Android/skeleton"
    }
})
