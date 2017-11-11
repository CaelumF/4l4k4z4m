/**
 * Created by paranoidcake on 11/11/17.
 */



import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.ContainerInfo
import com.spotify.docker.client.messages.HostConfig
import com.spotify.docker.client.messages.PortBinding
import org.apache.commons.io.output.NullOutputStream
import java.io.OutputStream


/**
 * Facade for docker containers for the purpose of running code.
 * Constructed with factory companion object.
 */
class CloudlinProfile private constructor(val id: String, newContainer: Boolean) {
    companion object factory {
        private val containerConfig: ContainerConfig
        private val docker: DefaultDockerClient = DefaultDockerClient.fromEnv().build()
        private val profiles = HashMap<String, CloudlinProfile>()

        init {
            val imageTag = "kotlin:latest"
            val portBindings = arrayListOf(80, 22).associate { Pair("$it", arrayListOf(PortBinding.of("0.0.0.0", it + 10090))) } as HashMap
            portBindings["443"] = arrayListOf<PortBinding>(PortBinding.randomPort("0.0.0.0"))

            containerConfig = ContainerConfig.builder().apply {
                hostConfig(HostConfig.builder().portBindings(portBindings as Map<String, MutableList<PortBinding>>?).build())
                image(imageTag)
                tty(true)
                cmd("tail")

            }.build()
        }

        /**
         * Returns profile by [id] if it exists. If it doesn't, creates it and its corresponding container and returns
         * the new profile.
         *
         * Not pure.
         */
        fun getOrCreateProfile(id: String): CloudlinProfile {
            return profiles.getOrPut(id, {
                val isExisting = docker.listContainers(DockerClient.ListContainersParam.allContainers())
                        .find { it.names()?.contains("/$id") ?: false } != null
                return CloudlinProfile(id, !isExisting)
            })
        }
    }

    private val container: ContainerInfo

    init {
        if (newContainer) docker.createContainer(containerConfig, id)
        container = docker.inspectContainer(id)
        start()
    }

    /**
     * Writes, compiles and executes [kotlinCode] and returns the stdout and stderr of its execution to
     * [out] and [err] respectively, if either of which are null, a [NullOutputStream]
     * is attached in place of the null [OutputStream].
     *
     * This method is non-blocking and not currently thread safe.
     */
    fun execute(kotlinCode: String, out: OutputStream? = null, err: OutputStream? = null) {
        //Write code
        val escapedCode = kotlinCode.replace("\"", "\\\"")
        val writeCodeCommand = "echo \"$escapedCode\" > currentKotlin.kt"
        val execId = docker.execCreate(id, arrayOf("sh", "-c", writeCodeCommand))
        docker.execStart(execId.id(), DockerClient.ExecStartParameter.DETACH)

        //Compile code
        val compileAndRunCMD = docker.execCreate(id,
                arrayOf("sh", "-c", "bash ./runKotlin.sh currentKotlin.sh"),
                DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr())

        val compileAndRunExec = docker.execStart(compileAndRunCMD.id())
        //Attach either/both/no output streams
        if (out != null || err != null) {
            compileAndRunExec.attach(out ?: NullOutputStream(), err ?: NullOutputStream())
        }
    }

    fun isRunning() = getInspection().state().running()
    fun getInspection() = docker.inspectContainer(id)

    fun start() {
        if (isRunning()) return
        return docker.startContainer(id)
    }

    fun stop() = docker.stopContainer(id, 0)
    fun restart() = docker.restartContainer(id)
}