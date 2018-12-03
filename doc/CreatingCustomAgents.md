Creating Testrecorder Custom Agents
===================================

The simplest way of getting a configuration to work with testrecorder is placing it on the class path (configuration files in the `agentconfig` directory). This concept may become inefficient applied to larger applications using custom components and changing configurations. We should advance to manage our configurations in a separate project to be able to apply different configurations to our application.

Furthermore using the original testrecorder agent has a drawback: all testrecorder dependencies (e.g. asm, byte-buddy, objenenis) are placed on the classpath, possibly leading to version conflicts if the application uses the same dependencies in other versions.

For this reason we recommend to build a custom agent for larger projects:

* containing all configuration files in `agentconfig`
* containing all custom components on the classpath
* relocating all colliding dependencies to prevent version conflicts

First create a new maven project. The `pom.xml` should contain 

* all dependencies you need for your custom components. Try to depend on a minimal set of dependencies. Each dependency shared between the agent and the system under test could lead to version conflicts or class loader problems. Consider using the `provided` scope in such cases.


* a dependency to the testrecorder agent

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>testrecorder-agent</artifactId>
    <version>0.9.0</version>
</dependency>
```

* a section for the [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/) configured similar to:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <shadedArtifactAttached>true</shadedArtifactAttached>
                <shadedClassifierName>agent</shadedClassifierName>
                <keepDependenciesWithProvidedScope>false</keepDependenciesWithProvidedScope>
                <relocations>
                    <relocation>
                        <pattern>org.objectweb.asm</pattern>
                        <shadedPattern>net.amygdalum.shaded.objectweb.asm</shadedPattern>
                    </relocation>
                    <relocation>
                        <pattern>org.objenesis</pattern>
                        <shadedPattern>net.amygdalum.shaded.objenesis</shadedPattern>
                    </relocation>
                    <relocation>
                        <pattern>net.bytebuddy</pattern>
                        <shadedPattern>net.amygdalum.shaded.bytebuddy</shadedPattern>
                    </relocation>
                    <relocation>
                        <pattern>org.antlr</pattern>
                        <shadedPattern>net.amygdalum.shaded.antlr</shadedPattern>
                    </relocation>
                    <relocation>
                        <pattern>org.stringtemplate</pattern>
                        <shadedPattern>net.amygdalum.shaded.stringtemplate</shadedPattern>
                    </relocation>
                </relocations>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <manifestEntries>
                            <Premain-Class>net.amygdalum.testrecorder.TestRecorderAgent</Premain-Class>
                            <Agent-Class>net.amygdalum.testrecorder.TestRecorderAgent</Agent-Class>
                            <Can-Redefine-Classes>true</Can-Redefine-Classes>
                            <Can-Retransform-Classes>true</Can-Retransform-Classes>
                        </manifestEntries>
                    </transformer>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>agentconfig/net.amygdalum.testrecorder.deserializers.builder.SetupGenerator</resource>
                    </transformer>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>agentconfig/net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator</resource>
                    </transformer>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>agentconfig/net.amygdalum.testrecorder.profile.PerformanceProfile</resource>
                    </transformer>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>agentconfig/net.amygdalum.testrecorder.profile.SerializationProfile</resource>
                    </transformer>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>agentconfig/net.amygdalum.testrecorder.types.Serializer</resource>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

