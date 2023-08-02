@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

fun readConfig(name: String): String {
    return settings.extensions.extraProperties.properties[name] as String?
        ?: System.getenv(name) ?: ""
}

pluginManagement {
    fun createBuildLogicPath(): String {
        //If it is build by Jenkins, use the project directory to keep BuildLogic
        return if (System.getenv("BUILD_ID").isNullOrEmpty()) {
            "../BuildLogic"
        } else {
            "BuildLogic"
        }
    }

    fun initBuildLogic(buildLogicPath: String) {
        fun execCmd(workPath: String, cmd: String): String {
            val stdout = java.io.ByteArrayOutputStream()
            exec {
                workingDir = file(workPath)
                commandLine(cmd.split(" "))
                standardOutput = stdout
            }
            return stdout.toString().trim()
        }

        if (!file(buildLogicPath).exists()) {
            println("Init build logic...")
            //clone build logic to BuildLogic dir
            val result = execCmd(
                ".",
                "git clone -b main https://github.com/TW-Smart-CoE/BuildLogic.git $buildLogicPath"
            )
            if (result.isNotEmpty()) {
                println(result)
            }
            println("Build logic init success")
        } else {
//            Fix BuildLogic version to avoid Sync Failure brought by auto-update

//            println("Update build logic...")
//            val result = execCmd(buildLogicPath, "git pull")
//            if (result.isNotEmpty()) {
//                println(result)
//            }
//            println("Update build logic success")
        }
    }

    val buildLogicPath = createBuildLogicPath()
    initBuildLogic(buildLogicPath)
    includeBuild(buildLogicPath)

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal()

        maven {
            url = uri("http://10.205.215.4:8081/repository/maven-releases/")
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "IoT1234"
            }
        }
    }
}

rootProject.name = "Smart-Assistant"
include(":app")
include(":assistant")
