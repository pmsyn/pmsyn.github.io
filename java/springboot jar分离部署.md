# springboot jar分离部署

## 1.具体配置如下：

```xml
<build>
        <plugins>
            <!--更换maven的jar打包插件
            先前使用的是spring-boot-maven-plugin来打包，这个插件会将项目所有的依赖打入BOOT-INF/lib下，
            替换为maven-jar-plugin-->
            <!-- 1. springboot应用与jar分离部署配置-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.1.1</version>
                <configuration>
                    <archive>
                        <manifest>
                            <!--需要加入到类构建路径-->
                            <addClasspath>true</addClasspath>
                            <!--指定生成的Manifest文件中Class-Path依赖lib前面都加上路径,构建出lib/xx.jar-->
                            <classpathPrefix>lib/</classpathPrefix>
                            <mainClass>com.ms.serviceapi.ServiceapiApplication</mainClass>
                        </manifest>
                    </archive>
                    <!-- 3.排除resources配置文件 在jar同级目录增加配置文件-->
                    <excludes >
                        <exclude>**/*.properties</exclude>
                        <exclude>**/*.xml</exclude>
                        <exclude>**/*.yml</exclude>
                    </excludes>
                </configuration>
            </plugin>
            <!-- 2.拷贝依赖到jar外面的lib目录-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-lib</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>target/lib</outputDirectory>
                            <excludeTransitive>false</excludeTransitive>
                            <stripVersion>false</stripVersion>
                            <includeScope>runtime</includeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <!--自动化配置实现内容拷贝-->

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-antrun-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>run</goal>
                        </goals>

                        <configuration>
                            <target>
                                <property name="dist" value="target/publish"></property>
                                <property name="dist-tmp" value="${dist}/tmp"></property>
                                <property name="app-name" value="${project.artifactId}-${project.version}"></property>

                                <copy file="target/${app-name}.jar" tofile="${dist}/${app-name}.jar" />
                                <delete dir="${dist}/${app-name}-classes.jar" />

                                <!--web项目启用
                                    <mkdir dir="${dist-tmp}" />
                                    <copy file="target/${app-name}.jar" tofile="${dist-tmp}/${app-name}.jar" />
                                    <unzip src="${dist-tmp}/${app-name}.jar" dest="${dist-tmp}" />
                                    <zip destfile="${dist}/${app-name}-pages.jar">
                                    <zipfileset dir="${dist-tmp}/META-INF" prefix="META-INF" />
                                    <zipfileset dir="target/classes/static" prefix="static" />
                                    <zipfileset dir="target/classes/templates" prefix="templates" />
                                </zip>-->
                                <delete dir="${dist-tmp}" />

                                <move todir="${dist}/lib">
                                    <fileset dir="target/lib" />
                                </move>

                                <copy todir="${dist}">
                                    <fileset dir="target/classes">
                                        <include name="**/*.properties" />
                                        <include name="**/*.xml" />
                                        <include name="**/*.yml" />
                                    </fileset>
                                </copy>
                            </target>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <!--属性替换-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <configuration>
                    <delimiters>
                        <!-- 指定过滤的表达式
                            <delimit>$</delimit> : 表示 过滤开头为 $ 结束为 $ 的内容，例如 $project.versionb$
                            <delimit>${*}</delimit> 表示 过滤${}包裹的内容，例如 ${project.vserion}
                         -->
                        <delimit>${*}</delimit>
                    </delimiters>
                </configuration>
            </plugin>
            <!--4.启动项目 java -jar -Dloader.path=.,lib xx.jar - -debuge查看项目日志 -->
        </plugins>
    </build>

```

## 2.打包目录如下：

```txt
lib
xxx.yml
xxx-1.0.jar
```

## 3.运行：

```shell
java -jar -Dloader.path=lib xxx.jar
```

