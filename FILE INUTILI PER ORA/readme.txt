Cartella ui, da reinserire (nel caso) in s_balneare/src/main/java/com/example/s_balneare
Cartella css, da reinserire (nel caso) in s_balneare/src/main/resources/com/example/s_balneare
Cartella fxml, da reinserire (nel caso) in s_balneare/src/main/resources/com/example/s_balneare
Classi, da reinserire (nel caso) in s_balneare/src/main/java/com/example/s_balneare

In module-info.java inserire:

    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.s_balneare to javafx.fxml;
    opens com.example.s_balneare.ui.controller to javafx.controls;

In pom.xml inserire:

	<dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>21.0.6</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>21.0.6</version>
        </dependency>

	    <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <executions>
                    <execution>
                        <!-- Default configuration for running with: mvn clean javafx:run -->
                        <id>default-cli</id>
                        <configuration>
                            <mainClass>com.example.s_balneare/com.example.s_balneare.App</mainClass>
                            <launcher>app</launcher>
                            <jlinkZipName>app</jlinkZipName>
                            <jlinkImageName>app</jlinkImageName>
                            <noManPages>true</noManPages>
                            <stripDebug>true</stripDebug>
                            <noHeaderFiles>true</noHeaderFiles>
                        </configuration>
                    </execution>
                </executions>
            </plugin>