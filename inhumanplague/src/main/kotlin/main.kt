import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.HostConfig
import com.spotify.docker.client.messages.PortBinding

/**
 * Created by horse on 11/11/17.
 */

fun main(args: Array<String>) {
    val docker = DefaultDockerClient.fromEnv().build()
    val containerConfig = ContainerConfig.builder().apply {
        image("ubuntu")
        tty(true)
        cmd("tail")
    }.build()

    val container = docker.createContainer(containerConfig)
    docker.startContainer(container.id())
}