ssl数字证书生成命令：keytool -genkey -keystore "D:\localhost.keystore" -alias localhost -keyalg RSA
密钥：admin123

<Connector
	   protocol="org.apache.coyote.http11.Http11NioProtocol"
	   port="8443" maxThreads="200"
	   scheme="https" secure="true" SSLEnabled="true"
	   keystoreFile="D:/workspace_idea/ssl.keystore" keystorePass="admin123"
	   clientAuth="false" sslProtocol="TLS"/>