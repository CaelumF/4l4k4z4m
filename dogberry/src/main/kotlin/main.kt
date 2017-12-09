import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.HostConfig
import com.spotify.docker.client.messages.PortBinding
import com.spotify.docker.client.messages.Volume

/**
 * Created by horse on 11/11/17.
 */

fun main(args: Array<String>) {
    val docker = DefaultDockerClient.fromEnv().build()
    val hostConfig: HostConfig = with(HostConfig.builder()){
        appendBinds("/var/run/docker.sock:/var/run/docker.sock")
        build()
    }

    val containerConfig = ContainerConfig.builder().apply {
        image("4l4k4z4m")
        tty(true)
        cmd("tail")
        hostConfig(hostConfig)
    }.build()

    val container = docker.createContainer(containerConfig)
    docker.startContainer(container.id())
}