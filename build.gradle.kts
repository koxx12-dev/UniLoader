allprojects {
    group = extra["project.group"]?.toString()
        ?: throw groovy.lang.MissingPropertyException("Project group was not set!")
    version = extra["project.version"]?.toString()
        ?: throw groovy.lang.MissingPropertyException("Project version was not set!")

    repositories {
        // Default repositories
        mavenCentral()

        // Repositories
        maven("https://maven.unifycraft.xyz/releases")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://jitpack.io/")

        // Snapshots
        maven("https://maven.unifycraft.xyz/snapshots")
        mavenLocal()
    }
}
