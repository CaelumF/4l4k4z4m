FROM openjdk:8
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
#SDKMAN! dependencies
RUN apt-get update && apt-get install -y \
    zip
RUN apt-get install -y bash
RUN apt-get install -y make
RUN apt-get install -y docker
#Install Kotlin with SDKMAN!
RUN curl -s https://get.sdkman.io | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh"
RUN yes | /bin/bash -l -c "sdk install kotlin"

COPY runKotlin.sh runKotlin.sh

#Kotlin Script
RUN mkdir ~/bin/
RUN yes | /bin/bash -l -c "sdk install maven"
RUN yes | /bin/bash -l -c "sdk install kscript"
RUN ln -s ~/.sdkman/candidates/kotlin/current/bin/kotlin /bin/kotlin
RUN ln -s ~/.sdkman/candidates/kotlin/current/bin/kotlinc /bin/kotlinc
RUN ln -s ~/.sdkman/candidates/maven/current/bin/mvn /bin/mvn
RUN export SDKMAN_DIR="~/.sdkman"

#Install nailgun for faster startup
RUN apt-get update && apt-get install -y build-essential
RUN git clone https://github.com/facebook/nailgun
RUN cd nailgun && make && make install
COPY dogberry/nailgun-server-0.9.2-SNAPSHOT.jar nailgun-server.jar
RUN export JAVACMD=$(which ng)
#RUN ~/.sdkman/candidates/kscript/2.2.1/bin/kscript 'println("hello")'
ENTRYPOINT java -jar nailgun-server.jar && /bin/bash