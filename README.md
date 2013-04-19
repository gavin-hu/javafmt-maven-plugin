# Javafmt Maven Plugin
======================

1. Organize Imports
2. Format Java Code by Eclipse Code Formatter

======================

### Step1
```
<properties>
    <maven.compiler.source>1.6</maven.compiler.source>
    <maven.compiler.target>1.6</maven.compiler.target>
</properties>
```

### Step2
```
<plugin>
    <groupId>javafmt-maven-plugin</groupId>
    <artifactId>javafmt-maven-plugin</artifactId>
    <version>1.0.2</version>
    <executions>
        <execution>
            <id>java-format</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>format</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Step3
```
<pluginRepositories>
    <pluginRepository>
        <id>mvn-repo</id>
        <url>https://github.com/gavin-hu/mvn-repo/raw/master/releases</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </pluginRepository>
</pluginRepositories>

```
