FROM openjdk:8
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
#SDKMAN! dependencies
RUN apt-get update && apt-get install -y \
    zip
RUN apt-get install -y bash
RUN apt-get install -y docker
#Install Kotlin with SDKMAN!
RUN curl -s https://get.sdkman.io | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh"
RUN yes | /bin/bash -l -c "sdk install kotlin" && echo t

COPY runKotlin.sh runKotlin.sh

#Kotlin Script
RUN mkdir ~/bin/
RUN curl -s https://raw.githubusercontent.com/andrewoma/kotlin-script/master/kotlins > ~/bin/kotlins && chmod u+x ~/bin/kotlins